package com.xsd.safecardapp.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.MD5Utils;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 重置密码
 * @author OnlyWay
 *
 */
public class ResetPassWordActivity extends Activity {

	private EditText etPhoneNumber;
	private EditText etPasswordOld;
	private EditText etPasswordNew;
	private MyLoadingDialog dialog;
	
	private String phoneNumber;
	private String passwordOld;
	private String passwordNew;
	
	private Message message;
	private String resultCode;
	
	private SharedPreferences sp;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			if(dialog!=null && dialog.isShowing())
	        dialog.dismiss();
			
			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:				
				if ("0".equals(resultCode)) {
					Toast.makeText(ResetPassWordActivity.this, "修改成功",
							Toast.LENGTH_SHORT).show();
					finish();
				}				
				if ("12".equals(resultCode)) {
					Toast.makeText(ResetPassWordActivity.this, "修改失败",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case Consts.MESSAGE_FAIL:
				Toast.makeText(ResetPassWordActivity.this, "网络异常",
						Toast.LENGTH_SHORT).show();
				break;
			
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);
		etPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
		etPasswordOld = (EditText) findViewById(R.id.et_password_old);
		etPasswordNew = (EditText) findViewById(R.id.et_password_new);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		etPhoneNumber.setText(sp.getString("username", ""));
	}

	public void resetPassword(View v){
		phoneNumber = etPhoneNumber.getText().toString().trim();
		passwordOld = etPasswordOld.getText().toString().trim();
		passwordNew = etPasswordNew.getText().toString().trim();
		if(TextUtils.isEmpty(phoneNumber)||
				TextUtils.isEmpty(passwordOld)||TextUtils.isEmpty(passwordNew)){
			Toast.makeText(this, "请输入完整信息", 0).show();
			return ;
		}else{
			requestResetPassword();
		}
	}

	private void requestResetPassword() {

		if (NetUtils.isNetworkAvailable(this)) {
			
			showLoadingDialog();//显示加载中对话框
			
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "UPW"));
					params.add(new BasicNameValuePair("PC", phoneNumber));
					params.add(new BasicNameValuePair("TY", "2"));
					params.add(new BasicNameValuePair("NPW", MD5Utils
							.MD5(passwordNew)));
					params.add(new BasicNameValuePair("OPW", MD5Utils
							.MD5(passwordOld)));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("UPW")));

					try {
						// 对参数编码
						String param = URLEncodedUtils.format(params, "UTF-8");
						System.out.println("fsfsfs3214" + Consts.URL_PATH
								+ "?" + param);
						String jsonString = MyHttpSend.httpSend(
								MyHttpSend.TYPE_GET, Consts.URL_PATH, params);
						System.out.println("jsonString" + jsonString);
						JSONObject object = new JSONObject(jsonString);
						resultCode = object.getString("code");
						System.out.println("xxxxxx" + resultCode);
						if (!TextUtils.isEmpty(resultCode)) {
							message = Message.obtain();
							message.what = Consts.MESSAGE_SUCCESS;
							handler.sendMessage(message);
						} else {
							message = Message.obtain();
							message.what = Consts.MESSAGE_FAIL;
							handler.sendMessage(message);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} else {
			message = Message.obtain();
			message.what = Consts.MESSAGE_FAIL;
			handler.sendMessage(message);
		}
	
		
	}
	
	 private void showLoadingDialog(){	
			dialog = MyLoadingDialog.getMyLoadingDialog(this,R.style.add_dialog);
			dialog.setMessage("数据加载中");
			dialog.show();				
		}
}

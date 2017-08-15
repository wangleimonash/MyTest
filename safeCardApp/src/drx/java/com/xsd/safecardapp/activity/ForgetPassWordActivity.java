package com.xsd.safecardapp.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
/**
 * 忘记密码
 *
 */
public class ForgetPassWordActivity extends Activity {

	private EditText etUsername;
	private EditText etSmsNumber;

	private Button btnSendSms;
	private MyLoadingDialog dialog; // 加载中对话框

	private Message message;
	private String resultCode;
	private String smsResult;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:
				Toast.makeText(ForgetPassWordActivity.this, "短信已经发送",
						Toast.LENGTH_SHORT).show();

				break;
			case Consts.MESSAGE_FAIL:
				Toast.makeText(ForgetPassWordActivity.this, "短信发送失败",
						Toast.LENGTH_SHORT).show();
				break;
			case 110:
				if ("0".equals(resultCode)) {
					Toast.makeText(ForgetPassWordActivity.this, "重置密码成功",
							Toast.LENGTH_SHORT).show();

					finish();
				}
				if ("2".equals(resultCode)) {
					Toast.makeText(ForgetPassWordActivity.this, "该账户不存在",
							Toast.LENGTH_SHORT).show();
					finish();
				}
				if ("12".equals(resultCode)) {
					Toast.makeText(ForgetPassWordActivity.this, "修改失败",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 9527:
				btnSendSms.setClickable(true);
				btnSendSms.setBackgroundResource(R.drawable.selector_btn_login);
				break;

			default:

				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);
		etUsername = (EditText) findViewById(R.id.et_username);
		etUsername.setText(getPhoneNumber());
		etSmsNumber = (EditText) findViewById(R.id.et_sms_number);
		btnSendSms = (Button) findViewById(R.id.btn_send_sms);
	}

	/**
	 * 下一步按钮  发送忘记密码请求
	 * @param v
	 */
	public void nextStep(View v) {
				
		if (smsResult.equals(etSmsNumber.getText().toString().trim())) {
			requestForgetData();
		} else {
			Toast.makeText(ForgetPassWordActivity.this, "短信验证错误", Toast.LENGTH_SHORT)
					.show();
			return;
		}
	}
	
	/**
	 * 发送忘记密码请求
	 */
	public void requestForgetData(){

		if (NetUtils.isNetworkAvailable(this)) {
			
			showLoadingDialog();
			
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "RPW"));
					params.add(new BasicNameValuePair("PC", etUsername
							.getText().toString().trim()));					
					params.add(new BasicNameValuePair("AU", AUUtils
							.getAU("RPW")));
					try {
						String jsonString = MyHttpSend.httpSend(
								MyHttpSend.TYPE_GET, Consts.URL_PATH, params);
						JSONObject object = new JSONObject(jsonString);
						resultCode = object.getString("code");
						System.out.println("hhsfds" + resultCode);
						if (!TextUtils.isEmpty(resultCode)) {
							message = Message.obtain();
							message.what = 110;
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
	
	/**
	 * 请求服务器发送短信
	 * 
	 * @param v
	 */
	public void sendSms(View v) {

		if (etUsername.getText().toString().trim().length() != 11) {
			Toast.makeText(this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
			return;
		}

		if (NetUtils.isNetworkAvailable(this)) {

			btnSendSms.setClickable(false);
			btnSendSms.setBackgroundColor(Color.GRAY);
			SendSmsBtnClickable();

			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", Consts.SMS_ORDER));
					params.add(new BasicNameValuePair("PC", etUsername
							.getText().toString().trim()));
					params.add(new BasicNameValuePair("AU", AUUtils
							.getAU(Consts.SMS_ORDER)));
					try {
						//对参数编码  
//				      	String param = URLEncodedUtils.format(params, "UTF-8"); 
//				        //TargetUrl             
//						String targetUrl = Consts.URL_PATH+"?"+param;  
//						System.out.println("fsfsdffsd"+targetUrl);
						String jsonString = MyHttpSend.httpSend(
								MyHttpSend.TYPE_GET, Consts.URL_PATH, params);
						
						JSONObject object = new JSONObject(jsonString);
						smsResult = (String) object.get("result");
						if (!TextUtils.isEmpty(smsResult)) {
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

	private void SendSmsBtnClickable() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				message = Message.obtain();
				message.what = 9527;
				handler.sendMessage(message);
			}
		}).start();

	};
	
	private String getPhoneNumber() {
		// 获取手机号码
		TelephonyManager tm = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String tel = tm.getLine1Number();// 获取本机号码
		if (!TextUtils.isEmpty(tel)) {
			return tel.substring(3);
		}
		return "";
	}

}

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
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
/**
 * 手动输入添加学生卡
 *
 */
public class InputDeviceActivity extends Activity {

	private EditText etIMEI;

	private String str;
	private String imeiNumber = "";
	private String simNumber = "";
	private Message message;
	private String resultCode = "";	
	private SharedPreferences sp;
	
	private MyLoadingDialog dialog;

	private Handler handler = new Handler() {
				
		public void handleMessage(android.os.Message msg) {
			
			if(dialog!=null && dialog.isShowing())
		        dialog.dismiss();
			
			imeiNumber = "";
			simNumber = "";
			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:
				
				if ("0".equals(resultCode)) {
					Toast.makeText(InputDeviceActivity.this, "添加成功",
							Toast.LENGTH_SHORT).show();
					setResult(9527);
					finish();
				}
				if ("4".equals(resultCode)) {
					Toast.makeText(InputDeviceActivity.this, "平安卡已被其他账号添加过了",
							Toast.LENGTH_SHORT).show();
					setResult(4);
					finish();
				}
				if ("12".equals(resultCode)) {
					Toast.makeText(InputDeviceActivity.this, "添加失败，请重新添加",
							Toast.LENGTH_SHORT).show();
					setResult(12);
					finish();
				}
				break;
			case Consts.MESSAGE_FAIL:
				Toast.makeText(InputDeviceActivity.this, "网络或输入信息有误",
						Toast.LENGTH_SHORT).show();
				    setResult(90016);
				    finish();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_device_input);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		etIMEI = (EditText) findViewById(R.id.et_imei);
	}

	public void addDevice(View v) {
		str = etIMEI.getText().toString().trim();
		if (TextUtils.isEmpty(str)) {
			Toast.makeText(this, "信息不能为空", 0).show();
			return;
		} else if (str.length() == 11) {
			simNumber = str;
		} else {
			imeiNumber = str;
		}

		requestAddDeviceData(imeiNumber, simNumber, sp.getString("username",""));
	}

	private void requestAddDeviceData(final String imeiNumber, final String simNumber,
			final String username) {
		
		if (NetUtils.isNetworkAvailable(this)) {
			
			showLoadingDialog();
			
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "ADD"));
					params.add(new BasicNameValuePair("ID", imeiNumber));
					params.add(new BasicNameValuePair("SIM", simNumber));
					params.add(new BasicNameValuePair("PC", username));
					params.add(new BasicNameValuePair("AU", AUUtils
							.getAU("ADD")));

					try {
						
						String param = URLEncodedUtils.format(params, "UTF-8"); 
				        
				        //TargetUrl             
						String targetUrl = Consts.URL_PATH+"?"+param; 
						
						System.out.println("dsdfsdfd"+targetUrl);
						
						String jsonString = MyHttpSend.httpSend(
								MyHttpSend.TYPE_GET, Consts.URL_PATH, params);	
						System.out.println("dsfdghhghg"+jsonString);
						JSONObject object = new JSONObject(jsonString);
						resultCode = object.getString("code");
						
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
						message = Message.obtain();
						message.what = Consts.MESSAGE_FAIL;
						handler.sendMessage(message);
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

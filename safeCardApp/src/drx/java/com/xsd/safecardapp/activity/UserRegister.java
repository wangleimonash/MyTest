package com.xsd.safecardapp.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 用户注册
 * @author OnlyWay
 *
 */
public class UserRegister extends Activity {

	private EditText etUsername;
	private EditText etSmsNumber;
	private EditText etPassword;
	private EditText etPasswordConfirm;
	private Button btnSendSms;
	private MyLoadingDialog dialog; //加载中对话框

	private String password;
	private String passwordConfirm;
	
	private Message message;
	private String resultCode;
	private String smsResult;
	
	private SharedPreferences sp;
	private Editor editor;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			if(dialog!=null && dialog.isShowing())
		        dialog.dismiss();
			
			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:
				Toast.makeText(UserRegister.this, "短信已经发送", Toast.LENGTH_SHORT)
						.show();				
				break;
			case Consts.MESSAGE_FAIL:
				Toast.makeText(UserRegister.this, "短信发送失败", Toast.LENGTH_SHORT)
						.show();
				break;
			case 10086:
				Bundle bundle = msg.getData();
				if (bundle != null) {
					etSmsNumber.setText(bundle.getString("content"));
				}
				break;
			case 110:
				
				if ("0".equals(resultCode)) {
					Toast.makeText(UserRegister.this, "注册成功",
							Toast.LENGTH_SHORT).show();					
					
					editor.putString(Consts.USER_NAME, etUsername.getText().toString());
					editor.putString(Consts.PASSWORD, password);
					editor.commit();
					
					setResult(Consts.RESULT_OK);
					finish();
				}
				if ("4".equals(resultCode)) {
					Toast.makeText(UserRegister.this, "该账户已经注册",
							Toast.LENGTH_SHORT).show();
					finish();
				}
				if ("12".equals(resultCode)) {
					Toast.makeText(UserRegister.this, "注册失败，请稍后在试",
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
		setContentView(R.layout.activity_register);
		
		sp = getSharedPreferences(Consts.CONFIG, MODE_PRIVATE);
		editor = sp.edit();
		
		initView();
		registeReceiver();
	}

	private void registeReceiver() {
		//动态注册短信接收广播
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(smsBroadcastReceiver, filter);
	}

	private void initView() {
		etUsername = (EditText) findViewById(R.id.et_username);
		etUsername.setText(getPhoneNumber());
		etSmsNumber = (EditText) findViewById(R.id.et_sms_number);
		etPassword = (EditText) findViewById(R.id.et_password);
		etPassword = (EditText) findViewById(R.id.et_password);
		etPasswordConfirm = (EditText) findViewById(R.id.et_password_confirm);
		btnSendSms = (Button) findViewById(R.id.btn_send_sms);
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
			Toast.makeText(this, "请检查网络", Toast.LENGTH_SHORT).show();
		}
	}

	public void nextStep(View v) {
		password = etPassword.getText().toString().trim();
		passwordConfirm = etPasswordConfirm.getText().toString().trim();
		if (TextUtils.isEmpty(etSmsNumber.getText())
				|| TextUtils.isEmpty(etUsername.getText())
				|| TextUtils.isEmpty(password)
				|| TextUtils.isEmpty(passwordConfirm)) {
			Toast.makeText(UserRegister.this, "输入信息不完整", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (smsResult.equals(etSmsNumber.getText().toString())) {
			if (password.length() < 6) {
				Toast.makeText(UserRegister.this, "密码长度不得小于6",
						Toast.LENGTH_SHORT).show();
				return;
			}
			requestRegisterData();
		} else {
			Toast.makeText(UserRegister.this, "短信验证错误", Toast.LENGTH_SHORT)
					.show();
			return;
		}
	}
	
	private void showLoadingDialog(){	
		dialog = MyLoadingDialog.getMyLoadingDialog(this,R.style.add_dialog);
		dialog.setMessage("数据加载中");
		dialog.show();				
	}

	private void requestRegisterData() {

		if (NetUtils.isNetworkAvailable(this)) {
			
			showLoadingDialog();
			
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "RGG"));
					params.add(new BasicNameValuePair("PC", etUsername
							.getText().toString().trim()));
					params.add(new BasicNameValuePair("PW", MD5Utils
							.MD5(password)));
					params.add(new BasicNameValuePair("AU", AUUtils
							.getAU("RGG")));
					try {
						String jsonString = MyHttpSend.httpSend(
								MyHttpSend.TYPE_GET, Consts.URL_PATH, params);
						JSONObject object = new JSONObject(jsonString);
						resultCode = object.getString("code");
						System.out.println("hhsfds" + resultCode);
						if (!TextUtils.isEmpty(smsResult)) {
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
			Toast.makeText(this, "请检查网络", Toast.LENGTH_SHORT).show();
		}
	}

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
	

	/**
	 * 接收短信广播
	 */
	private BroadcastReceiver smsBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			SmsMessage msg;
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] pdusObj = (Object[]) bundle.get("pdus");
				for (Object p : pdusObj) {
					msg = SmsMessage.createFromPdu((byte[]) p);

					String msgTxt = msg.getMessageBody(); // 得到消息的内容

					if (msgTxt != null) {
						String smsNumber = msgTxt.replaceAll("\\D+", "");
						smsNumber = smsNumber.subSequence(0, 4).toString();
						// Pattern pattern = Pattern
						// .compile(".*忠犬安迪.*您的验证码是(\\d+).*");
						// Matcher matcher = pattern.matcher(msgTxt);
						// if (matcher.matches()) { // 匹配成功后，自动填写验证码 2015/5/8
						// 12:11
						Bundle bundle1 = new Bundle();
						bundle1.putString("content", smsNumber);

						message = Message.obtain();
						message.what = 10086;
						message.setData(bundle1);
						handler.sendMessage(message);
					}
				}
			}
		}
	};
	
	public void back(View v){
		finish();
	}
	
	@Override
	protected void onDestroy() {
		
		unregisterReceiver(smsBroadcastReceiver);
		smsBroadcastReceiver = null;
		super.onDestroy();
		
	};
}

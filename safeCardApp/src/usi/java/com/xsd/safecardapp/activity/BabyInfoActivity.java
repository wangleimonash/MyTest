package com.xsd.safecardapp.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.fragment.HomePageFragment;
import com.xsd.safecardapp.fragment.MeFragment;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BabyInfoActivity extends Activity{

	private ImageView ivHead;
	private TextView tvName;
	private TextView tvSex;
	private TextView tvSimNumber;
	private TextView tvBirthday;
	private Button btnChange;
	
	private String name;
	private int sex;
	private String simCard;
	private String birthday;
	
	private MyLoadingDialog dialog;
	
	private String resultCode;
	private String resultCodeUP;
	private Message message;
	private int temp = -1;	
	private int position = -1;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			if(dialog!=null && dialog.isShowing())
		        dialog.dismiss();
			
			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:
				if("0".equals(resultCode)){
					tvName.setText(name);
					String[] a = birthday.split(" ");
					tvBirthday.setText(a[0]);
					tvSimNumber.setText(simCard);
										
					if(sex!=1){
						ivHead.setImageResource(R.drawable.head_girl);
						tvSex.setText("女");
					}
				}else{
					Toast.makeText(BabyInfoActivity.this, "宝贝信息获取失败", Toast.LENGTH_SHORT)
					.show();
				}								
				break;
			case Consts.MESSAGE_FAIL:
				Toast.makeText(BabyInfoActivity.this, "网络错误", Toast.LENGTH_SHORT)
						.show();
				break;	
			case 90016:
				if("0".equals(resultCodeUP)){
					Toast.makeText(BabyInfoActivity.this, "宝贝信息修改成功", Toast.LENGTH_SHORT)
					.show();
					MainTabActivity.getmResult().get(position).setUserName(tvName.getText().toString().trim());
						finish();									
				}else{
					Toast.makeText(BabyInfoActivity.this, "宝贝信息修改失败", Toast.LENGTH_SHORT)
					.show();
				}	
				break;	
			default:
				
				break;
			}
		}		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_babyinfo);
		
		initView();
		
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("DOTA");
		if(bundle!=null){
			position = bundle.getInt("position");
			requestBabyInfo(MainTabActivity.getmResult().get(position).getImei());
			
		}else{		
			position = HomePageFragment.oldPosition;
		requestBabyInfo(MainTabActivity.getmResult().get(HomePageFragment.oldPosition).getImei());
		//position = MeFragment.oldPosition;
		//btnChange.setVisibility(View.INVISIBLE);
		}
		
		
		
		
		
	}
	
	public void updateInfo(View v){
		
		if(TextUtils.isEmpty(tvName.getText().toString().trim())){
			Toast.makeText(BabyInfoActivity.this, "备注不能为空", Toast.LENGTH_SHORT)
			.show();
			return ;
		}
		
		if("女".equals(tvSex.getText().toString().trim())){
			temp = 2;
		}else{
			temp = 1;
		}
		if(position!=-1)
		requestUpdateBabyInfo(MainTabActivity.getmResult().get(position).getImei());
	}

	private void initView() {
		ivHead = (ImageView)findViewById(R.id.iv_head);
		tvName = (EditText)findViewById(R.id.et_name);
		tvSex = (EditText)findViewById(R.id.et_sex);
		tvSimNumber = (EditText)findViewById(R.id.et_sim_number);
		tvBirthday = (EditText)findViewById(R.id.et_birthday);	
		btnChange = (Button)findViewById(R.id.btn_change);
	}

	private void showLoadingDialog(){	
		dialog = MyLoadingDialog.getMyLoadingDialog(this,R.style.add_dialog);
		dialog.setMessage("数据加载中");
		dialog.show();				
	}
	
	
	public void finishMyself(View v){
		finish();
	}
	
	private void requestUpdateBabyInfo(final String imei){

		

		if (NetUtils.isNetworkAvailable(this)) {
			
			showLoadingDialog();
			
			
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "UIF"));
					params.add(new BasicNameValuePair("ID",imei));
					params.add(new BasicNameValuePair("UN",tvName.getText().toString().trim()));
					params.add(new BasicNameValuePair("SEX",temp+""));
					params.add(new BasicNameValuePair("BD",tvBirthday.getText().toString().trim()));
					params.add(new BasicNameValuePair("AU", AUUtils
							.getAU("UIF")));
					try {
						// 对参数编码
						String param = URLEncodedUtils.format(params, "UTF-8");
						System.out.println("dasdasdag456" + Consts.URL_PATH
								+ "?" + param);
						String jsonString = MyHttpSend.httpSend(
								MyHttpSend.TYPE_GET, Consts.URL_PATH, params);
						JSONObject object = new JSONObject(jsonString);
						resultCodeUP = object.getString("code");
						
						if (!TextUtils.isEmpty(resultCode)) {
							message = Message.obtain();
							message.what = 90016;
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

	
		
	
	};
	
	private void requestBabyInfo(final String imei) {

		if (NetUtils.isNetworkAvailable(this)) {
			
			showLoadingDialog();
			
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "IF"));
					params.add(new BasicNameValuePair("ID",imei));
					params.add(new BasicNameValuePair("AU", AUUtils
							.getAU("IF")));
					try {
						// 对参数编码
						String param = URLEncodedUtils.format(params, "UTF-8");
						System.out.println("dasdasdag456" + Consts.URL_PATH
								+ "?" + param);
						String jsonString = MyHttpSend.httpSend(
								MyHttpSend.TYPE_GET, Consts.URL_PATH, params);
						JSONObject object = new JSONObject(jsonString);
						resultCode = object.getString("code");
						System.out.println("hhsfdsdasd" + jsonString);
						
						JSONObject obj = object.getJSONObject("result");
						name = obj.getString("username");
						sex = obj.getInt("sex");
						simCard = obj.getString("simcard");
						birthday = obj.getString("birthday");
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
}

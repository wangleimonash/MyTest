package com.xsd.safecardapp.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.adapter.AlarmAdapter;
import com.xsd.safecardapp.fragment.HomePageFragment;

import com.xsd.safecardapp.javabean.AlarmJson;
import com.xsd.safecardapp.javabean.AlarmJson.AlarmResult;
import com.xsd.safecardapp.javabean.ModeBean;
import com.xsd.safecardapp.javabean.ModeBean.ModeEntity;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 模式选择
 * @author OnlyWay
 *
 */
public class SelectModeActivity extends Activity implements OnClickListener {

	private TextView tvStart;
	private TextView tvEnd;
	private EditText etTime;
	private EditText etTT;

	private String jsonString;
	private Message msg;
	private AlarmJson mJson;
	private List<AlarmResult> mResult;

	private ListView lvAlarmRecord;
	private AlarmAdapter adapter;
	private MyLoadingDialog dialog;
	
	private String mCode;
	
	private ModeEntity modeEntity;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:

				Toast.makeText(getApplicationContext(), "设置成功",
						Toast.LENGTH_SHORT).show();
				break;

			case Consts.MESSAGE_FAIL:

				break;

			case Consts.MESSAGE_EMPTY:
				Toast.makeText(getApplicationContext(), "数据为空",
						Toast.LENGTH_SHORT).show();
				break;
			case 10086:

				break;
			case 10000:
				tvStart.setText(timeString(flushLeft('0',4,modeEntity.getStime())));
				tvEnd.setText(timeString(flushLeft('0',4,modeEntity.getEtime())));
				etTime.setText(modeEntity.getLocation()+"");
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_mode);
		tvStart = (TextView) findViewById(R.id.tv_start);
		tvEnd = (TextView) findViewById(R.id.tv_end);
		etTime = (EditText) findViewById(R.id.et_time);
		etTT = (EditText) findViewById(R.id.et_tt);
		tvStart.setOnClickListener(this);
		tvEnd.setOnClickListener(this);
		doRequest(MainTabActivity.getmResult().get(HomePageFragment.oldPosition).getImei());
	}
	
	public String timeString(String str){
		StringBuilder sb = new StringBuilder(str);
		sb.insert(2, ":");
		return sb.toString();
	}
	
	   /* c 要填充的字符    
	    *  length 填充后字符串的总长度    
	    *  content 要格式化的字符串   
	    *  格式化字符串，左对齐 
	    * */  
	 public String flushLeft(char c, int length, String content){             
	       String str = "";         
	       String cs = "";     
	       if (content.length() > length){     
	            str = content;     
	       }else{    
	            for (int i = 0; i < length - content.length(); i++){     
	                cs =  cs+c;     
	            }  
	          }  
	        str = cs+content ;      
	        return str;      
	   }    
	
	public void doRequest(final String imei){
			
		showLoadingDialog();
		
		if (NetUtils.isNetworkAvailable(this)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "DL"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("DL")));

					try {
						String abcString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						System.out.println("xiaosilaozi"+jsonString);

						Gson gson = new Gson();
						ModeBean modebean = gson.fromJson(abcString, ModeBean.class);
						
						System.out.println("fsdfdfhre" + mCode);

						if (modebean.getCode() == 0) {
							modeEntity = modebean.getResult();
							msg = Message.obtain();
							msg.what = 10000;
							handler.sendMessage(msg);
						} else {
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_EMPTY;
							handler.sendMessage(msg);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} else {
			msg = Message.obtain();
			msg.what = Consts.MESSAGE_FAIL;
			handler.sendMessage(msg);
		}
	}
	
	
	
	public void commit(View v){
		if(TextUtils.isEmpty(tvStart.getText().toString().trim())||
				TextUtils.isEmpty(tvEnd.getText().toString().trim())||
				TextUtils.isEmpty(etTime.getText().toString().trim())
				){
			Toast.makeText(this, "信息不能为空", 0).show();
			return;
		}
		
		requestModeData(MainTabActivity.getmResult().get(HomePageFragment.oldPosition).getImei(),
				tvStart.getText().toString().trim().replace(":", ""),
				tvEnd.getText().toString().trim().replace(":", ""),
				etTime.getText().toString().trim())
				;
	}

	public void back(View v) {
		finish();
	}
	
	private String formatTime(int time){
		String str;
		if(time<10){
			str = "0"+String.valueOf(time);
		}else{
			str = String.valueOf(time);
		}
		return str;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_start:
			/**
             * 实例化一个TimePickerDialog的对象
             * 第二个参数是一个TimePickerDialog.OnTimeSetListener匿名内部类，当用户选择好时间后点击done会调用里面的onTimeset方法
             */
            TimePickerDialog timePickerDialog = new TimePickerDialog(SelectModeActivity.this, new TimePickerDialog.OnTimeSetListener()
            {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                {	
                	String strStart = formatTime(hourOfDay)+":"+formatTime(minute);
                	tvStart.setText(strStart);
                }
            }, 0, 0, true);
            
            timePickerDialog.show();
			break;
		case R.id.tv_end:
			/**
             * 实例化一个TimePickerDialog的对象
             * 第二个参数是一个TimePickerDialog.OnTimeSetListener匿名内部类，当用户选择好时间后点击done会调用里面的onTimeset方法
             */
            TimePickerDialog timePickerDialog2 = new TimePickerDialog(SelectModeActivity.this, new TimePickerDialog.OnTimeSetListener()
            {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                {	
                	String strEnd = formatTime(hourOfDay)+":"+formatTime(minute);
                	tvEnd.setText(strEnd);
                }
            }, 0, 0, true);
            timePickerDialog2.show();
			break;

		default:
			break;
		}
		
	}
	
	private void showLoadingDialog() {
		dialog = MyLoadingDialog.getMyLoadingDialog(this, R.style.add_dialog);
		dialog.setMessage("数据加载中");
		dialog.show();
	}

	public void requestModeData(final String imei,final String startTime,final String endTime,final String lt) {

		showLoadingDialog();
		
		if (NetUtils.isNetworkAvailable(this)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "SL"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("ST", startTime));
					params.add(new BasicNameValuePair("ET", endTime));
					params.add(new BasicNameValuePair("LT", lt));
					params.add(new BasicNameValuePair("RP", lt));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("SL")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						System.out.println("xiaosilaozi"+jsonString);

						JSONObject ojb = new JSONObject(jsonString);
						mCode = ojb.getString("code");
						
						System.out.println("fsdfdfhre" + mCode);

						if (!TextUtils.isEmpty(mCode)) {
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_SUCCESS;
							handler.sendMessage(msg);
						} else {
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_EMPTY;
							handler.sendMessage(msg);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} else {
			msg = Message.obtain();
			msg.what = Consts.MESSAGE_FAIL;
			handler.sendMessage(msg);
		}

	}
	
}

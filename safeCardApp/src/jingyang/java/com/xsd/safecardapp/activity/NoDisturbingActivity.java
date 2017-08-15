package com.xsd.safecardapp.activity;

import java.io.IOException;
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
import com.xsd.safecardapp.javabean.TimeBean;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.CircleTextView;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
/**
 * 免打扰时间
 * @author OnlyWay
 *
 */
public class NoDisturbingActivity extends Activity implements OnClickListener{
	
	private TextView tvStart;
	private TextView tvEnd;
	
	private CircleTextView ctv1;
	private CircleTextView ctv2;
	private CircleTextView ctv3;
	private CircleTextView ctv4;
	private CircleTextView ctv5;
	private CircleTextView ctv6;
	private CircleTextView ctv7;
	
	private String strStart;
	private String strEnd;
	
	private String[] weeks={"0","0","0","0","0","0","0"};
	private CircleTextView[] ctvs ;
	
	private String[] times = new String[8];
	
	private String jsonString;
	private Message msg;
	private MyLoadingDialog dialog;

	private List<TimeBean> list;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:
				Toast.makeText(getApplicationContext(), "设置成功",
						Toast.LENGTH_SHORT).show();
				finish();
				
				break;

			case Consts.MESSAGE_FAIL:

				break;

			case Consts.MESSAGE_EMPTY:
				Toast.makeText(getApplicationContext(), "设置失败",
						Toast.LENGTH_SHORT).show();
				break;
			case 10086:
				
				break;
			case 10010:
				
				break;

			default:
				break;
			}
		};
	};
	
	
	public String timeString(String str){
		StringBuilder sb = new StringBuilder(str);
		sb.insert(2, ":");
		return sb.toString();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_no_disturbing);
		findViewById(R.id.ib_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		Intent intent = getIntent();
		Bundle b = intent.getBundleExtra("DATA");
		//list = (List<TimeBean>)b.getSerializable("DATA");
		list = ShowNoDisturbingActivity.timeBeanList;
		System.out.println("AXX"+list.size());
		
		for(int i = 0;i<list.size();i++){
			times[i]=list.get(i).getsTime()+"-"+list.get(i).geteTime();
		}
		for(int i = list.size();i<times.length;i++){
			times[i] = "0000-0000";
		}
		
		
		
		tvStart = (TextView) findViewById(R.id.tv_start);
		tvEnd = (TextView) findViewById(R.id.tv_end);
		tvStart.setOnClickListener(this);
		tvEnd.setOnClickListener(this);
		ctv1 = (CircleTextView) findViewById(R.id.tv_1);
		ctv2 = (CircleTextView) findViewById(R.id.tv_2);
		ctv3 = (CircleTextView) findViewById(R.id.tv_3);
		ctv4 = (CircleTextView) findViewById(R.id.tv_4);
		ctv5 = (CircleTextView) findViewById(R.id.tv_5);
		ctv6 = (CircleTextView) findViewById(R.id.tv_6);
		ctv7 = (CircleTextView) findViewById(R.id.tv_7);
		ctvs = new CircleTextView[]{ctv7,ctv6,ctv5,ctv4,ctv3,ctv2,ctv1};
//		cb1 = (CheckBox) findViewById(R.id.cb1);
//		cb2 = (CheckBox) findViewById(R.id.cb2);
//		cb3 = (CheckBox) findViewById(R.id.cb3);
//		cb4 = (CheckBox) findViewById(R.id.cb4);
//		cb5 = (CheckBox) findViewById(R.id.cb5);
//		cb6 = (CheckBox) findViewById(R.id.cb6);
//		cb7 = (CheckBox) findViewById(R.id.cb7);
//		cb1.setOnCheckedChangeListener(this);
//		cb2.setOnCheckedChangeListener(this);
//		cb3.setOnCheckedChangeListener(this);
//		cb4.setOnCheckedChangeListener(this);
//		cb5.setOnCheckedChangeListener(this);
//		cb6.setOnCheckedChangeListener(this);
//		cb7.setOnCheckedChangeListener(this);
//		
		findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				if(TextUtils.isEmpty(strStart)||TextUtils.isEmpty(strEnd)){
					Toast.makeText(getApplicationContext(), "请先设置时间段", 0).show();
				}else{
					StringBuffer  sb = new StringBuffer();
					for(int i=0;i<ctvs.length;i++){
						System.out.println("QQQ"+ctvs[i].isChecked());
						if(ctvs[i].isChecked()){
							weeks[i] = "1";
						}
						System.out.println("QQQ week"+weeks[i] );
						sb.append(weeks[i]);
					}
					
					
					System.out.println("QQQ toString"+sb.toString() );
					requestTime(MainTabActivity.getmResult().get(HomePageFragment.oldPosition).getImei(),Integer.valueOf(sb.toString(), 2).toString(),strStart,strEnd);
				}
				
			}
		});
		
		
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
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_start:
			/**
             * 实例化一个TimePickerDialog的对象
             * 第二个参数是一个TimePickerDialog.OnTimeSetListener匿名内部类，当用户选择好时间后点击done会调用里面的onTimeset方法
             */
            TimePickerDialog timePickerDialog = new TimePickerDialog(NoDisturbingActivity.this, new TimePickerDialog.OnTimeSetListener()
            {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                {	
                	strStart = formatTime(hourOfDay)+formatTime(minute);
                	tvStart.setText(formatTime(hourOfDay)+":"+formatTime(minute));
                	
                }
            }, 0, 0, true);
            
            timePickerDialog.show();
            
            break;
		case R.id.tv_end:
			/**
             * 实例化一个TimePickerDialog的对象
             * 第二个参数是一个TimePickerDialog.OnTimeSetListener匿名内部类，当用户选择好时间后点击done会调用里面的onTimeset方法
             */
            TimePickerDialog timePickerDialog2 = new TimePickerDialog(NoDisturbingActivity.this, new TimePickerDialog.OnTimeSetListener()
            {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                {
                	strEnd = formatTime(hourOfDay)+formatTime(minute);
                	tvEnd.setText(formatTime(hourOfDay)+":"+formatTime(minute));
                }
            }, 0, 0, true);
            
            timePickerDialog2.show();
            
            break;
        }
			

		
		}

//	@Override
//	public void onCheckedChanged(CompoundButton button, boolean arg1) {
//		switch (button.getId()) {
//		case R.id.cb1:
//			if(arg1)
//				weeks[6] = "1";
//			else
//				weeks[6] = "0";
//			break;
//		case R.id.cb2:
//			if(arg1)
//				weeks[5] = "1";
//			else
//				weeks[5] = "0";
//			break;
//		case R.id.cb3:
//			if(arg1)
//				weeks[4] = "1";
//			else
//				weeks[4] = "0";
//			break;
//		case R.id.cb4:
//			if(arg1)
//				weeks[3] = "1";
//			else
//				weeks[3] = "0";
//			break;
//		case R.id.cb5:
//			if(arg1)
//				weeks[2] = "1";
//			else
//				weeks[2] = "0";
//			break;
//		case R.id.cb6:
//			if(arg1)
//				weeks[1] = "1";
//			else
//				weeks[1] = "0";
//			break;
//		case R.id.cb7:
//			if(arg1)
//				weeks[0] = "1";
//			else
//				weeks[0] = "0";
//			break;
//		default:
//			
//			break;
//		}
//		
//	}
	
	
	/**
	 * 设置休眠时间段
	 */
	private void requestTime(final String imei, final String wp , final String st, final String et) {

		showLoadingDialog();
		
		

		if (NetUtils.isNetworkAvailable(this)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "SS"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("WP", wp));
					params.add(new BasicNameValuePair("TD", "255"));
					String str = "";
					times[list.size()] =strStart+"-"+strEnd;
					for(int i=0;i<times.length;i++){
						if(i!=(times.length-1)){
						str = str +times[i]+",";
						}else{
							str =str+times[i];
						}
					}
					System.out.println("AXX"+str);
					params.add(new BasicNameValuePair("TL",str));					
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("SS")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						System.out.println("EEEEE" + jsonString);

						JSONObject obj;
						
						obj = new JSONObject(jsonString);
						
						String code = obj.getString("code");
						

						if ("0".equals(code)) {
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

	private void showLoadingDialog() {
		dialog = MyLoadingDialog.getMyLoadingDialog(this, R.style.add_dialog);
		dialog.setMessage("数据加载中");
		dialog.show();
	}

	
	
		
	}




package com.xsd.safecardapp.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.adapter.TimeAdapter;
import com.xsd.safecardapp.fragment.HomePageFragment;
import com.xsd.safecardapp.javabean.NoDisturbingJson;
import com.xsd.safecardapp.javabean.SwitchJson;
import com.xsd.safecardapp.javabean.TimeBean;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.CircleTextView;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 免打扰时间段展示
 * @author OnlyWay
 *
 */
public class ShowNoDisturbingActivity extends Activity{
	
	private String jsonString;
	private Message msg;
	private MyLoadingDialog dialog;
	private String strWeekper;
	private int intWeekper;
	private String strStime1;
	private String strEtime1;
	
	private String weeksString;
	private TextView tvWeeks;
	private TextView tvTime;
	private StringBuilder sb;
	
	private Boolean isOpend = false;
	
	NoDisturbingJson json;
	SwitchJson switchJson ;
	
	private CircleTextView[] ctvs;
	private CircleTextView ctv1,ctv2,ctv3,ctv4,ctv5,ctv6,ctv7;
	
	private LinearLayout ll;
	
	private ImageButton ibAdd;
	private ImageButton ibToggle;
	
	String[] weeks = {"周一","周二","周三","周四","周五","周六","周日"};
	
	private ListView lvContent;
	public static List<TimeBean> timeBeanList;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();
			
			

			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:
//				Toast.makeText(getApplicationContext(), "设置成功",
//						Toast.LENGTH_SHORT).show();
				
				
				for(int i = 0;i>ctvs.length;i++){
					ctvs[i].setChecked(false);
				}
				
				String weekper = Integer.toBinaryString(intWeekper);
				System.out.println("XXX"+weekper);
				char[] b=weekper.toCharArray();
				sb = new StringBuilder();
				for(int i = weekper.length()-1;i>=0;i--){
					System.out.println("XXXY"+b[i]);
					ctvs[weekper.length()-1-i].setChecked(false);
					if("1".equals(String.valueOf(b[i]))){
						//sb.append(weeks[weekper.length()-1-i]+" ");
						ctvs[weekper.length()-1-i].setChecked(true);
					}
				}
				//tvWeeks.setText(sb.toString());
				//tvTime.setText(timeString(flushLeft('0',4,json.getResult().getStime1()))+" -- "+timeString(flushLeft('0',4,json.getResult().getEtime1())));
					
				ll = (LinearLayout) findViewById(R.id.ll);
				
				//TimeBean.setList(timeBeanList);
				lvContent.setAdapter(new TimeAdapter(timeBeanList, ShowNoDisturbingActivity.this));
				
				
				if(switchJson.getResult().getSt() == 1){
					ibToggle.setImageResource(R.drawable.switch_on);
					isOpend = true;
				}else{
					ibToggle.setImageResource(R.drawable.switch_off);
					isOpend = false;
				}
				if(isOpend){
					ibToggle.setImageResource(R.drawable.switch_on);
					ll.setVisibility(View.VISIBLE);
					ibAdd.setVisibility(View.VISIBLE);
				}else{
					ibToggle.setImageResource(R.drawable.switch_off);
					ll.setVisibility(View.INVISIBLE);
					ibAdd.setVisibility(View.INVISIBLE);
				}
				break;

			case Consts.MESSAGE_FAIL:

				break;

			case Consts.MESSAGE_EMPTY:
				Toast.makeText(getApplicationContext(), "信息获取失败",
						Toast.LENGTH_SHORT).show();
				break;
			case 10086:
				for(int i = 0;i>ctvs.length;i++){
					ctvs[i].setChecked(false);
				}
				
				
				
				String weekper2 = Integer.toBinaryString(intWeekper);
				System.out.println("XXX"+weekper2);
				char[] b2=weekper2.toCharArray();
				sb = new StringBuilder();
				
				
				
				for(int i = weekper2.length()-1;i>=0;i--){
					System.out.println("XXXY"+b2[i]);
					ctvs[weekper2.length()-1-i].setChecked(false);
					if("1".equals(String.valueOf(b2[i]))){
						//sb.append(weeks[weekper.length()-1-i]+" ");
						ctvs[weekper2.length()-1-i].setChecked(true);
					}
				}
				//tvWeeks.setText(sb.toString());
				//tvTime.setText(timeString(flushLeft('0',4,json.getResult().getStime1()))+" -- "+timeString(flushLeft('0',4,json.getResult().getEtime1())));
				if(isOpend){
					ibToggle.setImageResource(R.drawable.switch_on);
					ll.setVisibility(View.VISIBLE);
					ibAdd.setVisibility(View.VISIBLE);
				}else{
					ibToggle.setImageResource(R.drawable.switch_off);
					ll.setVisibility(View.INVISIBLE);
					ibAdd.setVisibility(View.INVISIBLE);
				}
				Toast.makeText(getApplicationContext(), "功能开关尚未打开",
						Toast.LENGTH_SHORT).show();
				break;
			case 10087:
				isOpend = !isOpend;
				String str = isOpend?"休眠开关已开启":"休眠开关已关闭";
				Toast.makeText(getApplicationContext(), str,
						Toast.LENGTH_SHORT).show();
				if(isOpend){
					ibToggle.setImageResource(R.drawable.switch_on);
					ll.setVisibility(View.VISIBLE);
					ibAdd.setVisibility(View.VISIBLE);
				}else{
					ibToggle.setImageResource(R.drawable.switch_off);
					ll.setVisibility(View.INVISIBLE);
					ibAdd.setVisibility(View.INVISIBLE);
				}
				break;

			default:
				break;
			}
		};
	};
	
	private String formatTime(int time){
		String str;
		if(time<10){
			str = "0"+String.valueOf(time);
		}else{
			str = String.valueOf(time);
		}
		return str;
	}
	
	
	public void addTimeBean(String sTime,String eTime){
		if(timeBeanList == null){
			timeBeanList = new ArrayList<TimeBean>();
		}
		if(sTime.equals("0")&&eTime.equals("0")){
			return;
		}else{
			TimeBean bean = new TimeBean();
			bean.setsTime(flushLeft('0',4,sTime));
			bean.seteTime(flushLeft('0',4,eTime));
			timeBeanList.add(bean);
		}
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_no_disturbing);
		tvTime = (TextView) findViewById(R.id.tv_time);
		tvTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
//				Bundle b = new Bundle();
//				//TimeBean.setList(timeBeanList);
//				b.putSerializable("DATA",(Serializable) TimeBean.getList() );
//				i.putExtra("DATA", b);
				//timeBeanList.clear();
			}
		});
		ibToggle = (ImageButton) findViewById(R.id.ib_toggle);
		ibToggle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String state = !isOpend?"1":"0";
				requestToggle(MainTabActivity.getmResult().get(HomePageFragment.oldPosition).getImei(),state);
			}
		});
		findViewById(R.id.ib_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		ibAdd = (ImageButton) findViewById(R.id.ib_add);
		ibAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(timeBeanList.size()<8){
				
				
				Intent i = new Intent(ShowNoDisturbingActivity.this,NoDisturbingActivity.class);
				startActivityForResult(i, 999);
				}else{
					Toast.makeText(ShowNoDisturbingActivity.this, "时段不能超过8个", 0).show();
				}
				
			}
		});
		
		ctv1 = (CircleTextView) findViewById(R.id.tv_1);
		ctv2 = (CircleTextView) findViewById(R.id.tv_2);
		ctv3 = (CircleTextView) findViewById(R.id.tv_3);
		ctv4 = (CircleTextView) findViewById(R.id.tv_4);
		ctv5 = (CircleTextView) findViewById(R.id.tv_5);
		ctv6 = (CircleTextView) findViewById(R.id.tv_6);
		ctv7 = (CircleTextView) findViewById(R.id.tv_7);
		ctv1.setClickable(false);
		ctv2.setClickable(false);
		ctv3.setClickable(false);
		ctv4.setClickable(false);
		ctv5.setClickable(false);
		ctv6.setClickable(false);
		ctv7.setClickable(false);
		
		ctvs = new CircleTextView[]{ctv1,ctv2,ctv3,ctv4,ctv5,ctv6,ctv7};
		lvContent = (ListView) findViewById(R.id.lv_content);
		lvContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int positon, long arg3) {
				Intent i = new Intent(ShowNoDisturbingActivity.this,EditDisturbinActivity.class);
				Bundle b = new Bundle();
				b.putInt("DATA", positon);
				b.putInt("intWeekper", intWeekper);
				i.putExtra("DATA", b);
				startActivityForResult(i, 999);
				
			}
		});
		
		requestData(MainTabActivity.getmResult().get(HomePageFragment.oldPosition).getImei());
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//requestData(MainTabActivity.getmResult().get(HomePageFragment.oldPosition).getImei());
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		requestData(MainTabActivity.getmResult().get(HomePageFragment.oldPosition).getImei());
		
		super.onActivityResult(requestCode, resultCode, data);
		
	}
	
	
	
	/**
	 * 设置开关咯
	 */
	private void requestToggle(final String imei, final String state ) {

		showLoadingDialog();
		
		if (NetUtils.isNetworkAvailable(this)) {
			
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "SETST"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("TY", "5"));
					params.add(new BasicNameValuePair("ST", state));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("SETST")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						System.out.println("EEEEE" + jsonString);

						JSONObject obj;
						
						obj = new JSONObject(jsonString);
						
						String code = obj.getString("code");
						

						if ("0".equals(code)) {
							msg = Message.obtain();
							msg.what = 10087;
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
	
	
	/**
	 * 查询休眠时间段
	 */
	private void requestData(final String imei) {
		
		showLoadingDialog();
		timeBeanList = null;
		
		ctv1.setChecked(false);
		ctv2.setChecked(false);
		ctv3.setChecked(false);
		ctv4.setChecked(false);
		ctv5.setChecked(false);
		ctv6.setChecked(false);
		ctv7.setChecked(false);

		if (NetUtils.isNetworkAvailable(this)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "DS"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("DS")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						System.out.println("EEEEE" + jsonString);
						
						
						Gson gson = new Gson();
						if(json !=null){
							json =null;
						}
						json = gson.fromJson(jsonString, NoDisturbingJson.class);

						

						if ("0".equals(json.getCode())) {
							
							strWeekper = json.getResult().getWeekper();
							//strStime1 = json.getResult().getStime1();
							//strEtime1 = json.getResult().getEtime1();
							addTimeBean(json.getResult().getStime1(), json.getResult().getEtime1());
							addTimeBean(json.getResult().getStime2(), json.getResult().getEtime2());
							addTimeBean(json.getResult().getStime3(), json.getResult().getEtime3());
							addTimeBean(json.getResult().getStime4(), json.getResult().getEtime4());
							addTimeBean(json.getResult().getStime5(), json.getResult().getEtime5());
							addTimeBean(json.getResult().getStime6(), json.getResult().getEtime6());
							addTimeBean(json.getResult().getStime7(), json.getResult().getEtime7());
							addTimeBean(json.getResult().getStime8(), json.getResult().getEtime8());
							intWeekper = Integer.parseInt(strWeekper);
							
							// 先将参数放入List，再对参数进行URL编码
							List<BasicNameValuePair> params2 = new LinkedList<BasicNameValuePair>();
							params2.add(new BasicNameValuePair("CD", "GETST"));
							params2.add(new BasicNameValuePair("ID", imei));
							params2.add(new BasicNameValuePair("TY", "5"));
							params2.add(new BasicNameValuePair("AU", AUUtils.getAU("GETST")));

							
								jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
										Consts.URL_PATH, params2);

								System.out.println("EEEEE" + jsonString);
								
								Gson gson2 = new Gson();
								switchJson = gson2.fromJson(jsonString, SwitchJson.class);
								

								if (switchJson.getCode() == 0) {
									
									msg = Message.obtain();
									msg.what = Consts.MESSAGE_SUCCESS;
									handler.sendMessage(msg);
								}else if(switchJson.getCode() == 12){
									msg = Message.obtain();
									msg.what = 10086;
									handler.sendMessage(msg);
								} else {
									msg = Message.obtain();
									msg.what = Consts.MESSAGE_EMPTY;
									handler.sendMessage(msg);
								}
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
	
	@Override
	protected void onDestroy() {
		timeBeanList = null;
		super.onDestroy();
	}

}

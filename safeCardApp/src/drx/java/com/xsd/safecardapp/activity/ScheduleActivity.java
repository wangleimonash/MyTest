package com.xsd.safecardapp.activity;


import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.App;
import com.xsd.safecardapp.fragment.HomePageFragment;
import com.xsd.safecardapp.javabean.ScheduleJson;
import com.xsd.safecardapp.javabean.ScheduleJson.ResultEntity;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 课程表
 * @author OnlyWay
 *
 */
public class ScheduleActivity extends Activity{
	
	private ListView lvSchedule;
	private Message msg;
	private MyLoadingDialog dialog;
	private Adapter adapter;
	
	private static final int TYPE_COUNT = 2;
	private static final int TYPE_NORMAL = 1;
	private static final int TYPE_OTHER = 2;
	
	private String[] weeks = {"一","二","三","四","五","六","七","八",};
	
	private int mPosition;
	
	
	
	private  List<ResultEntity> result;
	
	private List<String[]> strs;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:
				lvSchedule.setAdapter(new MyAdapter());
				
				break;

			case Consts.MESSAGE_FAIL:

				break;

			case Consts.MESSAGE_EMPTY:
				Toast.makeText(getApplicationContext(), "数据为空",
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);
		lvSchedule = (ListView) findViewById(R.id.lv_schedule);
		try {
			if (App.getInstance().getmLoginReult().getResult().get(HomePageFragment.oldPosition).getClassId() != null) {
				requestAlarmRecord(App.getInstance().getmLoginReult().getResult().get(HomePageFragment.oldPosition).getClassId() + "");
			}
		}catch (Exception e){
			Log.e("wangmin",e==null?"null":e.getMessage());
		}
		
	}
	
	public void finishMyself(View v){
		finish();
	}
	
	private void showLoadingDialog() {
		dialog = MyLoadingDialog.getMyLoadingDialog(this, R.style.add_dialog);
		dialog.setMessage("数据加载中");
		dialog.show();
	}
	
	/**
	 * 请求报警日志信息
	 */
	private void requestAlarmRecord( final String cid)
		{

		showLoadingDialog();

		if (NetUtils.isNetworkAvailable(this)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "KCB"));
					params.add(new BasicNameValuePair("CID", cid));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("KCB")));

					try {
						String jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);

						System.out.println("fsdfdfhre" + jsonString);

						Gson gson = new Gson();
						ScheduleJson mJson = gson.fromJson(jsonString,
								ScheduleJson.class);
						 result = mJson.getResult();

						if (result != null && result.size() > 0) {
							strs = new ArrayList<String[]>();
							for(int i = 0;i<5;i++){
								ScheduleJson.ResultEntity entity =  result.get(i);
								System.out.println("AAAAAA"+i);
								String[] str = null;
								switch (i) {
								case 0:
									str = entity.getMonday().split(",");
									break;
								case 1:
									str = entity.getTuesday().split(",");
									break;
								case 2:
									str = entity.getWednesday().split(",");
									break;
								case 3:
									str = entity.getThursday().split(",");
									break;
								case 4:
									str = entity.getFriday().split(",");
									break;

								default:
									break;
								}
								System.out.println("WWWWWW"+str.length);
								mPosition = str.length;
								strs.add(str);
								
							}
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_SUCCESS;
							handler.sendMessage(msg);
						} else {
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_EMPTY;
							handler.sendMessage(msg);
						}

					} catch (Exception e) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								msg = Message.obtain();
								msg.what = Consts.MESSAGE_FAIL;
								handler.sendMessage(msg);
							}
						});
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
	
	private class MyAdapter extends BaseAdapter{
		
		@Override
		public int getItemViewType(int position) {
			int type;
			if(position!=4){
				type = TYPE_NORMAL;
			}else{
				type = TYPE_OTHER;
			}
			return type;
		}
		
		@Override
		public int getViewTypeCount() {
			return TYPE_COUNT;
		}

		@Override
		public int getCount() {
			return 9;
		}

		@Override
		public Object getItem(int positon) {
			return positon;
		}

		@Override
		public long getItemId(int positon) {
			return positon;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			View view;
			ViewHolder holder = new ViewHolder();
			if(position!=4){
				view = View.inflate(ScheduleActivity.this, R.layout.item_lv_schedule, null); 
				holder.tvWeek = (TextView) view.findViewById(R.id.tv_week);
				holder.tv1 = (TextView) view.findViewById(R.id.tv_1);
				holder.tv2 = (TextView) view.findViewById(R.id.tv_2);
				holder.tv3 = (TextView) view.findViewById(R.id.tv_3);
				holder.tv4 = (TextView) view.findViewById(R.id.tv_4);
				holder.tv5 = (TextView) view.findViewById(R.id.tv_5);
				if(position<4){
					holder.tvWeek.setText(weeks[position]);
					holder.tv1.setText(strs.get(0)[position]);
					holder.tv2.setText(strs.get(1)[position]);
					holder.tv3.setText(strs.get(2)[position]);
					holder.tv4.setText(strs.get(3)[position]);
					holder.tv5.setText(strs.get(4)[position]);
				}else if(position>4){
					holder.tvWeek.setText(weeks[position-1]);
					if(position - 1 <mPosition){
					
					holder.tv1.setText(strs.get(0)[position-1]);
					holder.tv2.setText(strs.get(1)[position-1]);
					holder.tv3.setText(strs.get(2)[position-1]);
					holder.tv4.setText(strs.get(3)[position-1]);
					holder.tv5.setText(strs.get(4)[position-1]);
					}
				}
			}else{
				view = View.inflate(ScheduleActivity.this, R.layout.item_lv_schedule_other, null);
			}
			return view;
		}
		
		class ViewHolder{
			TextView tvWeek;
			TextView tv1;
			TextView tv2;
			TextView tv3;
			TextView tv4;
			TextView tv5;
		}
		
	}

}

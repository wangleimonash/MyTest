package com.xsd.safecardapp.fragment;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.activity.MainTabActivity;
import com.xsd.safecardapp.adapter.PopupGridViewAdapter;
import com.xsd.safecardapp.javabean.LoginJson;
import com.xsd.safecardapp.javabean.StudentAttendanceJson;
import com.xsd.safecardapp.javabean.CardInfoBean.CardData;
import com.xsd.safecardapp.javabean.StudentAttendanceJson.AttendanceResult;
import com.xsd.safecardapp.popupwindow.SelectCardPopupWindow;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.DensityUtil;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.RequestDataUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyGridView;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * 作者： OnlyWay
 *
 * 作用： 考勤信息界面
 */
public class StudentAttendanceFragment extends Fragment implements
		OnItemClickListener, OnClickListener {

	private View rootView; // 根页面
	private MyGridView mGridView;
	private PopupGridViewAdapter adapter;
	private TextView tvNoInfo;
	private TextView tvArriveSchoolTime;
	private TextView tvLeaveSchoolTime;
	private TextView tvArriveSchoolAfternoon;
	private TextView tvLeaveSchoolAfternoon;
	private TextView tvDate;
	private TextView tvWeek;
	private TextView tvSelectStudent;

	private static boolean isStudentAttChanged;

	// 接口相关
	private Context mContext;
	private Message msg;
	private List<AttendanceResult> result;
	private String jsonString;
	private List<CardData> cardsInfo;// 平安卡信息列表
	
	private static int oldPosition;

	// 弹出窗口
	private PopupWindow popupWindow;
	private View popupView;

	private PopupWindow studentPopup;
	private View studentPopupView;
	private ListView lvPopupView;
	private int type = 0;//IMEI
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			tvArriveSchoolTime.setText("- -");
			tvArriveSchoolAfternoon.setText("- -");
			tvLeaveSchoolTime.setText("- -");
			tvLeaveSchoolAfternoon.setText("- -");
			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:
				// createPopupWindow();

				// showPopupWindow();
//				// tvArriveSchoolTime.setText(result.get(0).g);
//				if (result.size() == 4) {
//					tvArriveSchoolTime.setText(result.get(0).getTime());
//					tvLeaveSchoolTime.setText(result.get(1).getTime());
//					tvArriveSchoolAfternoon.setText(result.get(2).getTime());
//					tvLeaveSchoolAfternoon.setText(result.get(3).getTime());
//				}else{
					for(AttendanceResult   r : result){
						 Date date = null;
						 String time = null;
						try {
							date = format.parse(r.getTime());
							time = date.getHours()+":"+date.getMinutes();
						} catch (ParseException e) {
							e.printStackTrace();
						} 
						if(r.getFlag().equals("E")){
							
							if(date.getHours()<12){
								tvArriveSchoolTime.setText(time);
							}else{
								tvArriveSchoolAfternoon.setText(time);
							}
							
						}else{
							if(date.getHours()<12){
								tvLeaveSchoolTime.setText(time);
							}else{
								tvLeaveSchoolAfternoon.setText(time);
							}
						}
					}
//				}
				break;
			case Consts.MESSAGE_EMPTY:
				tvArriveSchoolTime.setText("- -");
				tvArriveSchoolAfternoon.setText("- -");
				tvLeaveSchoolTime.setText("- -");
				tvLeaveSchoolAfternoon.setText("- -");
				showPopupWindow();
				break;

			case 10086:
				break;

			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mContext = getActivity();

		rootView = inflater.inflate(R.layout.fragment_student_attendance,
				container, false);
		try {
			rootView.findViewById(R.id.ib_back).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					getActivity().finish();
					
				}
			});
			
			oldPosition= HomePageFragment.oldPosition;
		initView();
		
		createPopupWindow();
		
		createSelectStudentPopup();
			initKaoqing();
		} catch (Exception e) {			
			e.printStackTrace();
			//getActivity().finish();
			System.out.println("StuentAttendance  OnCreate 出错重新载入");
		}finally {
			return rootView;
		}
	}

	private void initKaoqing() {
		if(MainTabActivity.getmResult() == null||MainTabActivity.getmResult().size() == 0){
			throw new RuntimeException("MainTabActivity.getmResult() 为空");
		}
		LoginJson.LoginResult loginResults = MainTabActivity.getmResult().get(oldPosition);
		if(loginResults == null){
            throw new RuntimeException("loginResults == null");
        }
		String type = "";
		type = loginResults.getImei();
		if(TextUtils.isEmpty(type)){
            type = loginResults.getUserId();
            this.type = 1;
        }
		requestTodayInfo(type, this.type);
	}

	public void back(View v){
		getActivity().finish();
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
		try {
			initKaoqing();
		}catch (Exception e){
			Log.e("WM",e == null?"e == null":e.getMessage());
			e.printStackTrace();
		}
//		requestTodayInfo(MainTabActivity.getmResult()
//				.get(oldPosition).getImei());
	}

	private void initView() {
		mGridView = (MyGridView) rootView.findViewById(R.id.gridview);
		mGridView.setFocusable(false);
		//try {
						
		adapter = new PopupGridViewAdapter(getActivity(), MainTabActivity
					.getmResult().get(oldPosition).getImei());
		
		mGridView.setAdapter(adapter);
		
		mGridView.setOnItemClickListener(this);
		tvArriveSchoolTime = (TextView) rootView
				.findViewById(R.id.tv_arrive_school_time);
		tvLeaveSchoolTime = (TextView) rootView
				.findViewById(R.id.tv_leave_school_time);
		tvArriveSchoolAfternoon = (TextView) rootView
				.findViewById(R.id.tv_below_arrive_school_time);
		tvLeaveSchoolAfternoon = (TextView) rootView
				.findViewById(R.id.tv_below_leave_school_time);
		tvDate = (TextView) rootView.findViewById(R.id.tv_date);
		tvSelectStudent = (TextView) rootView
				.findViewById(R.id.tv_select_student);
		tvSelectStudent.setText(MainTabActivity.getmResult()
				.get(oldPosition).getUserName());
//		tvSelectStudent.setOnClickListener(this);
//		} catch (Exception e) {			
//			e.printStackTrace();
//			getActivity().finish();
//			System.out.println("StuentAttendance  出错重新载入");
//		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		try {
			if (MainTabActivity.getmResult() == null || MainTabActivity.getmResult().size() == 0) {
				throw new RuntimeException("MainTabActivity.getmResult() 为空");
			}
			LoginJson.LoginResult loginResults = MainTabActivity.getmResult().get(oldPosition);
			if (loginResults == null) {
				throw new RuntimeException("loginResults == null");
			}
			String type = "";
			type = loginResults.getImei();
			if (TextUtils.isEmpty(type)) {
				type = loginResults.getUserId();
				this.type = 1;
			}
			requestAttendanceInfo(
					type,
					RequestDataUtils.getStartTimeByDate(adapter.getListData().get(
							position)),
					RequestDataUtils.getEndTimeByDate(adapter.getListData().get(
							position)));
		}catch (Exception e){
			Log.e("WM",e == null?"e == null":e.getMessage());
			Toast.makeText(getActivity(),"访问失败",Toast.LENGTH_SHORT).show();
		}
	}

	private void showPopupWindow() {

		if (popupWindow!=null&&popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			// popupWindow.showAsDropDown(rlTop);
			popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
		}

	}
	
	@Override
	public void onDestroy() {
		popupWindow = null;
		super.onDestroy();
	}

	/**
	 * 请求今天的数据
	 * 
	 * @param imei
	 */
	private void requestTodayInfo(final String imei,final int type) {

		if (NetUtils.isNetworkAvailable(getActivity())) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "AI"));
					if(type == 0) {
						params.add(new BasicNameValuePair("ID", imei));
					}else{
						params.add(new BasicNameValuePair("SI", imei));
					}
					params.add(new BasicNameValuePair("ST", RequestDataUtils
							.getStartTimeByDate(adapter.listData
									.get(adapter.todayPositon))));
					params.add(new BasicNameValuePair("ET", RequestDataUtils
							.getEndTimeByDate(adapter.listData
									.get(adapter.todayPositon))));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("AI")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);
						Gson gson = new Gson();
						StudentAttendanceJson mJson = gson.fromJson(jsonString,
								StudentAttendanceJson.class);
						result = mJson.getResult();

						if (result != null && result.size() > 0) {
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_SUCCESS;
							handler.sendMessage(msg);
						} else {
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_EMPTY;
							handler.sendMessage(msg);
						}

					} catch (Exception e) {
						msg = Message.obtain();
						msg.what = Consts.MESSAGE_FAIL;
						handler.sendMessage(msg);
					}

				}
			});
		} else {
			msg = Message.obtain();
			msg.what = Consts.MESSAGE_FAIL;
			handler.sendMessage(msg);
		}

	}

	private void requestAttendanceInfo(final String imei,
			final String startTime, final String endTime) {
		if (NetUtils.isNetworkAvailable(getActivity())) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "AI"));
					if(type == 0) {
						params.add(new BasicNameValuePair("ID", imei));
					}else{
						params.add(new BasicNameValuePair("SI", imei));
					}
					params.add(new BasicNameValuePair("ST", startTime));
					params.add(new BasicNameValuePair("ET", endTime));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("AI")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);
						Gson gson = new Gson();
						StudentAttendanceJson mJson = gson.fromJson(jsonString,
								StudentAttendanceJson.class);
						result = mJson.getResult();

						if (result != null && result.size() > 0) {
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_SUCCESS;
							handler.sendMessage(msg);
						} else {
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_EMPTY;
							handler.sendMessage(msg);
						}

					} catch (Exception e) {
						msg = Message.obtain();
						msg.what = Consts.MESSAGE_FAIL;
						handler.sendMessage(msg);
					}

				}
			});
		} else {
			msg = Message.obtain();
			msg.what = Consts.MESSAGE_FAIL;
			handler.sendMessage(msg);
		}
	}

	private void createPopupWindow() {
		popupView = View.inflate(getActivity(),
				R.layout.popup_window_att_detail, null);
		// ListView lvAttendance = (ListView) popupView
		// .findViewById(R.id.lv_pop_window_list);
		// tvNoInfo = (TextView) popupView.findViewById(R.id.tv_no_info);
		// if (result != null && result.size() > 0) {
		//
		// lvAttendance.setAdapter(new AttendanceLVAdapter(getActivity(),
		// result));
		//
		// }

		popupWindow = new PopupWindow(popupView, DensityUtil.dip2px(
				getActivity(), 250), DensityUtil.dip2px(getActivity(), 150),
				true);
		popupWindow.setTouchable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(00000000));
		popupWindow.setOutsideTouchable(true);
	}
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_select_student:

//			createSelectStudentPopup();
//			showStuentPopupWindow();

			break;

		default:
			break;
		}

	}

	private void showStuentPopupWindow() {

		if (studentPopup.isShowing()) {
			studentPopup.dismiss();
		} else {
			studentPopup.showAsDropDown(tvSelectStudent);
			// studentPopup.showAtLocation(rootView, Gravity.CENTER, 0, 0);
		}

	}

	private void createSelectStudentPopup() {
		studentPopup = SelectCardPopupWindow.getPopupWindow(getActivity(),new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
				oldPosition = position;

				tvSelectStudent.setText(MainTabActivity.getmResult()
						.get(oldPosition).getUserName());
				try {
					initKaoqing();
				}catch (Exception e){
					Log.e("WM",e == null?"e == null":e.getMessage());
					e.printStackTrace();
				}
//				requestTodayInfo(MainTabActivity.getmResult()
//						.get(oldPosition).getImei());
				

				showStuentPopupWindow();
				
			}
			
		});
		
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isStudentAttChanged) {
			tvSelectStudent.setText(MainTabActivity.getmResult()
					.get(oldPosition).getUserName());
			try {
				initKaoqing();
			}catch (Exception e){
				Log.e("WM",e == null?"e == null":e.getMessage());
				e.printStackTrace();
			}
//			requestTodayInfo(MainTabActivity.getmResult()
//					.get(oldPosition).getImei());
			isStudentAttChanged = false;
		}
	}

}
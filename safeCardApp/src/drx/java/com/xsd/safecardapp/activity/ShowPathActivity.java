package com.xsd.safecardapp.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.message.BasicNameValuePair;

import com.amap.api.location.core.CoordinateConvert;
import com.amap.api.location.core.GeoPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.adapter.PopupGridViewAdapter;
import com.xsd.safecardapp.javabean.DateBean;
import com.xsd.safecardapp.javabean.PingAnPathJson;
import com.xsd.safecardapp.javabean.PingAnPathJson.PathResult;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.RequestDataUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 轨迹展示
 * @author OnlyWay
 *
 */
public class ShowPathActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	// 接口相关
	private String imei; // 从上个页面传过来的数据 用于获取轨迹信息
	private String jsonString;
	private Message msg;
	private List<PathResult> pathResult;// 轨迹信息集合

	private TextView tvLocation;
	private TextView tvTypeAndTime;
	private TextView tvNoPathInfo;
	private ImageButton ibMapMagnify;
	private ImageButton ibMapReduce;
	private RelativeLayout rlTopShowPath;
	private TextView tvSelectTime;// 上方选择日期按钮
	private ImageButton ibShowPath;
	private ImageButton ibBack;
	private PopupWindow popupWindow;
	private View popupView;
	private RelativeLayout rlNoData;
	private MyLoadingDialog dialog;
	private String lat;
	private String lng;
	private String loc;
	private String type;

	private SharedPreferences sp;
	private Editor editor;

	private int isShowingPath;
	private List<DateBean> listData = new ArrayList<DateBean>();
	private Calendar calendar = Calendar.getInstance();
	private int todayPositon;

	/**
	 * AMapV2地图中介绍如何使用mapview显示地图
	 */
	// 声明变量
	private MapView mapView;
	private UiSettings mUiSettings;
	private AMap aMap;
	private Marker marker;
	private Marker moveMarker;
	private int currentMarkerPosion = 0;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			switch (msg.what) {
			case Consts.MESSAGE_SUCCESS:
				
				tvLocation.setText(pathResult.get(pathResult.size() - 1)
						.getDesc());
				tvTypeAndTime.setText(pathResult.get(pathResult.size() - 1)
						.getType()
						+ " "
						+ pathResult.get(pathResult.size() - 1).getTime());

				/**
				 * 遍历添加轨迹点
				 */
				LatLng tempLatlng;

				for (PathResult presult : pathResult) {

					GeoPoint fromGpsToAMap = CoordinateConvert.fromGpsToAMap(presult.getLat(), presult.getLng());
					tempLatlng = new LatLng(fromGpsToAMap.getLatitudeE6()*1.E-6, fromGpsToAMap.getLongitudeE6()*1.E-6);
					
//					tempLatlng = LatLngUtils.transformFromWGSToGCJ(new LatLng(
//							presult.getLat(), presult.getLng()));
					marker = aMap.addMarker(new MarkerOptions()
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.ic_point))
					.anchor(0.5f, 0.5f)
					.position(tempLatlng));
					
					presult.setLat(tempLatlng.latitude);
					presult.setLng(tempLatlng.longitude);
//					marker = aMap.addMarker(new MarkerOptions()
//							.icon(BitmapDescriptorFactory
//									.fromResource(R.drawable.ic_point))
//							.anchor(0.5f, 0.5f)
//							.position(
//									new LatLng(presult.getLat(), presult
//											.getLng())));
				}
				
				showLocationByPoint();

				rlNoData.setVisibility(View.INVISIBLE);
				break;
			case Consts.MESSAGE_FAIL:
				Toast.makeText(getApplicationContext(), "请求错误", 0).show();
				rlNoData.setVisibility(View.VISIBLE);
				break;
			case Consts.MESSAGE_EMPTY:
				Toast.makeText(getApplicationContext(), "当天没有数据", 0).show();
				tvNoPathInfo.setText("当天没有数据");
				aMap.clear();
				rlNoData.setVisibility(View.VISIBLE);
				break;
			case 10086:
				isShowingPath = 1;

				if (moveMarker != null) {
					moveMarker.destroy();
				}
				if (currentMarkerPosion < pathResult.size()) {
					moveMarker = aMap.addMarker(new MarkerOptions()
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.head_boy))
							.anchor(0.5f, 0.5f)
							.position(
									new LatLng(pathResult.get(
											currentMarkerPosion).getLat(),
											pathResult.get(currentMarkerPosion)
													.getLng())));

					/**
					 * 添加折线
					 */
					// 绘制一条带有纹理的直线
					if (currentMarkerPosion > 0) {
						aMap.addPolyline((new PolylineOptions())
								.add(new LatLng(pathResult.get(
										currentMarkerPosion - 1).getLat(),
										pathResult.get(currentMarkerPosion - 1)
												.getLng()),
										new LatLng(pathResult.get(
												currentMarkerPosion).getLat(),
												pathResult.get(
														currentMarkerPosion)
														.getLng()))
								.geodesic(true).color(getResources().getColor(R.color.path_color)));
					}

					currentMarkerPosion++;
					msg = Message.obtain();
					msg.what = 10086;
					handler.sendMessageDelayed(msg, 500);

				} else {
					Toast.makeText(getApplicationContext(), "播放结束", 0).show();
					
					
					
					
					moveMarker = aMap.addMarker(new MarkerOptions()
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.head_boy))
							.anchor(0.5f, 0.5f)
							.position(
									new LatLng(pathResult.get(
											pathResult.size() - 1).getLat(),
											pathResult.get(
													pathResult.size() - 1)
													.getLng())));
					
					isShowingPath = -1;
					
					//currentMarkerPosion = 0;
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
		
		isShowingPath = -1 ;
				
		setContentView(R.layout.activity_show_path);
		initView(savedInstanceState);
		getData();
		
		sp = getSharedPreferences(Consts.CONFIG, Context.MODE_PRIVATE);
		editor = sp.edit();
		
		/**
		 * 获取上个页面穿过来的数据
		 */
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra(Consts.INTENT_DATA);
		if (bundle == null) {
			return;
		} else {
			imei = bundle.getString("IMEI");
			System.out.println("wodeimei"+imei);
		}
		
		createMyCalendar();
		
		String olat = sp.getString("lat", null);
		String olng = sp.getString("lng", null);
		//String oloc = sp.getString("loc", null);
		if (!(TextUtils.isEmpty(olat) || TextUtils.isEmpty(olng) )) {
			// 设置当前地图显示为当前位置
			aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
					new LatLng(Double.parseDouble(olat), Double.parseDouble(olng)),
					14));
		
		}
		

		// 请求当天数据
		try {
			requestPathData(imei,
					RequestDataUtils.getStartTimeByDate(listData.get(todayPositon)),
					RequestDataUtils.getEndTimeByDate(listData.get(todayPositon)));
		} catch (Exception e) {
			e.printStackTrace();
			finish();
			System.out.println("轨迹显示GameOver");
		}
		
		

	}

	/**
	 * 初始化控件
	 * 
	 * @param savedInstanceState
	 */
	private void initView(Bundle savedInstanceState) {
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 必须要写
		aMap = mapView.getMap();
		aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
		mUiSettings = aMap.getUiSettings();
		mUiSettings.setZoomControlsEnabled(false);

		tvLocation = (TextView) findViewById(R.id.tv_location_show_path);
		tvTypeAndTime = (TextView) findViewById(R.id.tv_type_and_time_show_path);

		ibShowPath = (ImageButton) findViewById(R.id.ib_show_path);
		ibShowPath.setOnClickListener(this);
		rlTopShowPath = (RelativeLayout) findViewById(R.id.rl_top_show_path);
		tvSelectTime = (TextView) findViewById(R.id.tv_time_show_path);
		tvSelectTime.setOnClickListener(this);
		tvNoPathInfo = (TextView) findViewById(R.id.tv_no_path_info);
		ibBack = (ImageButton) findViewById(R.id.ib_back_show_path);
		ibBack.setOnClickListener(this);
		rlNoData = (RelativeLayout) findViewById(R.id.rl_bottom_offline);

		ibMapMagnify = (ImageButton) findViewById(R.id.ib_map_magnify);
		ibMapMagnify.setOnClickListener(this);
		ibMapReduce = (ImageButton) findViewById(R.id.ib_map_reduce);
		ibMapReduce.setOnClickListener(this);
	}

	public void requestPathData(final String imei, final String startTime,
			final String endTime) {
		showLoadingDialog();
		if (NetUtils.isNetworkAvailable(this)) {
			ExecutorService executorService = MyExecutorService
					.getExecutorService();
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					pathResult = null;
					currentMarkerPosion = 0;
					// 先将参数放入List，再对参数进行URL编码
					List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("CD", "TL"));
					params.add(new BasicNameValuePair("ID", imei));
					params.add(new BasicNameValuePair("ST", startTime));
					params.add(new BasicNameValuePair("ET", endTime));
					params.add(new BasicNameValuePair("AU", AUUtils.getAU("TL")));

					try {
						jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
								Consts.URL_PATH, params);
						Gson gson = new Gson();
						PingAnPathJson mJson = gson.fromJson(jsonString,
								PingAnPathJson.class);
						pathResult = mJson.getResult();
						if (pathResult != null && pathResult.size() >= 1) {
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_SUCCESS;
							handler.sendMessage(msg);
						} else {
							msg = Message.obtain();
							msg.what = Consts.MESSAGE_EMPTY;
							handler.sendMessage(msg);
						}

					} catch (IOException e) {
						e.printStackTrace();
						msg = Message.obtain();
						msg.what = Consts.MESSAGE_EMPTY;
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_time_show_path:
			showMyCalendar();
			break;
		case R.id.ib_show_path:
			
			if(isShowingPath == 1){
				Toast.makeText(getApplicationContext(), "轨迹尚未播放完毕", 0).show();
				break;
			}else{		
				isShowingPath = 1;
				System.out.println("dasdasdasdqq11"+isShowingPath);
				
				if(currentMarkerPosion == 0){
			Toast.makeText(getApplicationContext(), "显示轨迹", 0).show();
			
			aMap.moveCamera(CameraUpdateFactory.zoomTo(10));
			
			showPathByAnimotion();
				}else
				if(currentMarkerPosion == pathResult.size()){
					Toast.makeText(getApplicationContext(), "轨迹已经播放结束", 0).show();
				}
			break;
			}
			
		case R.id.ib_back_show_path:

			finish();

			break;
		case R.id.ib_map_magnify:

			aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getCameraPosition().zoom + 1));

			break;

		case R.id.ib_map_reduce:

			aMap.moveCamera(CameraUpdateFactory.zoomTo(aMap.getCameraPosition().zoom - 1));

			break;
		default:
			break;
		}

	}

	private void showPathByAnimotion() {
		
			msg = Message.obtain();
			msg.what = 10086;
			handler.sendMessage(msg);
			
	}

	private void showMyCalendar() {

		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		} else {
			popupWindow.showAsDropDown(rlTopShowPath);
		}

	}

	public void setTitleTime(DateBean dateBean) {
		if (!dateBean.isToday()) {
			tvSelectTime.setText(dateBean.getMonth() + "月" + dateBean.getDay()
					+ "日");
		} else {
			tvSelectTime.setText("今天");
		}

	}

	/**
	 * 创建显示选择日期的日历PopupWindow
	 */
	private void createMyCalendar() {
		popupView = View.inflate(this, R.layout.popup_show_calendar, null);
		GridView gridView = (GridView) popupView.findViewById(R.id.gridview);
		gridView.setOnItemClickListener(this);
		gridView.setAdapter(new PopupGridViewAdapter(this, imei));

		popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		popupWindow.setTouchable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(00000000));
		popupWindow.setOutsideTouchable(true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		showMyCalendar();
		setTitleTime(listData.get(position));
		
		aMap.clear();
		
		requestPathData(
				imei,
				RequestDataUtils.getStartTimeByDate(listData.get(position)),
				RequestDataUtils.getEndTimeByDate(listData.get(position)));

	}

	private void getData() {
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		for (int i = 27 + week; i > 0; i--) {
			Calendar headCalender = Calendar.getInstance();
			DateBean dateBean = new DateBean();
			Calendar test = setStartTime(headCalender, -i);
			dateBean.setYear(test.get(Calendar.YEAR));
			dateBean.setMonth(test.get(Calendar.MONTH) + 1);
			dateBean.setDay(test.get(Calendar.DATE));
			dateBean.setWeek(test.get(Calendar.DAY_OF_WEEK));
			listData.add(dateBean);
		}
		todayPositon = 27 + week;
		// Calendar calendar3 = Calendar.getInstance();
		DateBean datedsaBean = new DateBean();
		datedsaBean.setYear(calendar.get(Calendar.YEAR));
		datedsaBean.setMonth(calendar.get(Calendar.MONTH) + 1);
		datedsaBean.setDay(calendar.get(Calendar.DATE));
		datedsaBean.setWeek(calendar.get(Calendar.DAY_OF_WEEK));
		datedsaBean.setToday(true);
		listData.add(datedsaBean);

		int j = week;
		int k = 1;
		while (7 - j > 0) {
			Calendar footCalendar = Calendar.getInstance();
			DateBean dateBean = new DateBean();
			Calendar test = setStartTime(footCalendar, k);
			dateBean.setYear(test.get(Calendar.YEAR));
			dateBean.setMonth(test.get(Calendar.MONTH) + 1);
			dateBean.setDay(test.get(Calendar.DATE));
			dateBean.setWeek(test.get(Calendar.DAY_OF_WEEK));
			listData.add(dateBean);
			k++;
			j++;
		}
	}

	private static Calendar setStartTime(Calendar calendar, int day) {
		calendar.add(Calendar.DATE, day);
		return calendar;
	}

	/**
	 * 添加Marker到当天最后的位置 并跳转到该位置
	 */
	private void showLocationByPoint() {
		

		moveMarker = aMap
				.addMarker(new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.head_boy))
						.anchor(0.5f, 0.5f)
						.position(
								new LatLng(pathResult
										.get(pathResult.size() - 1).getLat(),
										pathResult.get(pathResult.size() - 1)
												.getLng())));
		// 设置当前地图显示为当前位置
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
				new LatLng(pathResult.get(pathResult.size() - 1).getLat(),
						pathResult.get(pathResult.size() - 1).getLng()), 13));
	};
	
	
	private void showLoadingDialog() {

		dialog = MyLoadingDialog.getMyLoadingDialog(this, R.style.add_dialog);
		dialog.setMessage("数据加载中");
		dialog.show();

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			mapView.onDestroy();
			handler.removeMessages(10086);
			pathResult.clear();
			pathResult = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

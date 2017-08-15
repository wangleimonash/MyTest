package com.xsd.safecardapp.activity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.fragment.HomePageFragment;
import com.xsd.safecardapp.javabean.SafeAreaInfoJson.SafeAreaInfoResult;
import com.xsd.safecardapp.javabean.SafeAreaInfoJson.SafeAreaInfoResult.Regionpoint;
import com.xsd.safecardapp.utils.AUUtils;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.DensityUtil;
import com.xsd.safecardapp.utils.NetUtils;
import com.xsd.safecardapp.utils.connectionUtils.MyExecutorService;
import com.xsd.safecardapp.utils.connectionUtils.MyHttpSend;
import com.xsd.safecardapp.views.MyLoadingDialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 *添加安全区域
 */
public class AddSafeAreaActivity extends Activity implements
OnSeekBarChangeListener, OnMapClickListener, OnClickListener {

private SeekBar seekBar;
private int radius;
private TextView tvRadius;
private TextView tvAreaName;
private LatLng currentLatLng;
private String areaName;
private Message msg;
private String jsonString;
private int regionid;

private RelativeLayout llContainer;

private TextView tvPopupTitle;
private EditText etPopupContent;
private Button btnOk;
private Button btnCancel;

private MyLoadingDialog dialog;
private PopupWindow settingPopupWindow;
private View mPopupView;

/**
* AMapV2地图中介绍如何使用mapview显示地图
*/
// 声明变量
private MapView mapView;
private AMap aMap;
private Marker marker;
private Circle circle;// Marker周围画个圈

private Intent dataIntent;

private Handler handler = new Handler() {
public void handleMessage(Message msg) {
	
	if(dialog!=null && dialog.isShowing())
        dialog.dismiss();
	
	switch (msg.what) {
	case Consts.MESSAGE_SUCCESS:
		Toast.makeText(AddSafeAreaActivity.this, "添加成功",
				Toast.LENGTH_SHORT).show();
		Bundle bundle = new Bundle();
		SafeAreaInfoResult result = new SafeAreaInfoResult();
		result.setName(areaName);
		result.setRegionid(regionid);
		Regionpoint point = new Regionpoint();
		point.setLat(currentLatLng.latitude);
		point.setLng(currentLatLng.longitude);
		point.setPoint(radius);
		result.setRegionpoint(point);
		bundle.putSerializable("safeareainfo", result);
		dataIntent.putExtra(Consts.INTENT_DATA, bundle);
		setResult(Consts.SAFEAREA_TO_ADDSAFEAREA, dataIntent);
		finish();
		break;
	case Consts.MESSAGE_FAIL:
		Toast.makeText(AddSafeAreaActivity.this, "添加失败",
				Toast.LENGTH_SHORT).show();
		break;

	default:
		break;
	}
};
};

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_add_safe_area);
initView(savedInstanceState);
initData();
createPopupWindow();
}

private void initView(Bundle savedInstanceState) {
mapView = (MapView) findViewById(R.id.map);
mapView.onCreate(savedInstanceState);// 必须要写
aMap = mapView.getMap();
aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
aMap.setOnMapClickListener(this);
seekBar = (SeekBar) findViewById(R.id.seekbar);
seekBar.setOnSeekBarChangeListener(this);
tvRadius = (TextView) findViewById(R.id.tv_radius);
tvAreaName = (TextView) findViewById(R.id.tv_area_name);
tvAreaName.setOnClickListener(this);
llContainer = (RelativeLayout) findViewById(R.id.ll_container);
}

private void initData() {

dataIntent = new Intent();

radius = 1000;

if (MainTabActivity.currentLatLng != null) {
	currentLatLng = MainTabActivity.getCurrentLatLng();
	setCurrentPosition(currentLatLng, radius);
} else {
	Toast.makeText(this, "位置信息错误", Toast.LENGTH_SHORT).show();
}

}

private void setCurrentPosition(LatLng currentLatLng, int radius) {

aMap.clear();

// 得到当前位置并添加Marker

marker = aMap.addMarker(new MarkerOptions()
		.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.head_boy))
		.anchor(0.5f, 0.5f).position(currentLatLng));

// 设置当前地图显示为当前位置
aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14));

// 绘制一个圆形
circle = aMap.addCircle(new CircleOptions().center(currentLatLng)
		.radius(radius)
		.strokeColor(Color.TRANSPARENT)
		.fillColor(getResources().getColor(R.color.map_color)).strokeWidth(3));

}

/**
* 添加安全区域方法
* 
* @param v
*/
public void addSafeArea(View v) {

if (TextUtils.isEmpty(areaName)) {
	Toast.makeText(this, "请先编辑区域名称", Toast.LENGTH_SHORT).show();
	return;
} else {
	addSafeAreaData(MainTabActivity.getmResult()
			.get(HomePageFragment.oldPosition).getImei());
}
}

private void addSafeAreaData(final String imei) {

showLoadingDialog();
if (NetUtils.isNetworkAvailable(this)) {
	ExecutorService executorService = MyExecutorService
			.getExecutorService();
	executorService.execute(new Runnable() {

		@Override
		public void run() {
			// 先将参数放入List，再对参数进行URL编码
			List<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("CD", "AR"));
			params.add(new BasicNameValuePair("ID", imei));
			params.add(new BasicNameValuePair("NE", areaName));
			params.add(new BasicNameValuePair("SP", "2"));
			params.add(new BasicNameValuePair("RE", "" + radius + "-"
					+ currentLatLng.latitude + "-"
					+ currentLatLng.longitude));
			params.add(new BasicNameValuePair("AU", AUUtils.getAU("AR")));

			try {
				jsonString = MyHttpSend.httpSend(MyHttpSend.TYPE_GET,
						Consts.URL_PATH, params);

				JSONObject obj = new JSONObject(jsonString);
				String code = obj.getString("code");

				if ("0".equals(code)) {
					JSONObject resultObj = obj.getJSONObject("result");
					regionid = resultObj.getInt("regionid");
					System.out.println("dasfdf" + regionid);

					msg = Message.obtain();
					msg.what = Consts.MESSAGE_SUCCESS;
					handler.sendMessage(msg);
				} else {
					msg = Message.obtain();
					msg.what = Consts.MESSAGE_FAIL;
					handler.sendMessage(msg);
				}

				// if (pathResult.size() > 0) {
				// msg = Message.obtain();
				// msg.what = Consts.MESSAGE_SUCCESS;
				// handler.sendMessage(msg);
				// } else {
				// msg = Message.obtain();
				// msg.what = Consts.MESSAGE_EMPTY;
				// handler.sendMessage(msg);
				// }
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
* 搜索按钮触发方法
* 
* @param v
*/
public void search(View v) {

}

/**
* 返回键按钮触发方法
* 
* @param v
*/
public void finishMyself(View v) {
finish();
}

@Override
public void onMapClick(LatLng arg0) {

radius = 300;

currentLatLng = arg0;
setCurrentPosition(currentLatLng, radius);

}

@Override
public void onProgressChanged(SeekBar seekBar, int progress,
	boolean fromUser) {

switch (progress / 10) {
case 0:
	radius = 1000;
	break;
case 1:
	radius = 2000;
	break;
case 2:
	radius = 3000;
	break;
case 3:
	radius = 4000;
	break;
case 4:
	radius = 5000;
	break;
case 5:
	radius = 6000;
	break;
case 6:
	radius = 7000;
	break;
case 7:
	radius = 8000;
	break;
default:
	break;
}

circle.setRadius(radius);
tvRadius.setText("半径：" + radius + "米");
}

@Override
public void onStartTrackingTouch(SeekBar seekBar) {
// TODO Auto-generated method stub

}

@Override
public void onStopTrackingTouch(SeekBar seekBar) {
// TODO Auto-generated method stub

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
mapView.onDestroy();
}

@Override
public void onClick(View v) {
switch (v.getId()) {
case R.id.tv_area_name:
//	areaName = String.valueOf(System.currentTimeMillis());
//	tvAreaName.setText(areaName);
	showPopupWindow();
	break;

default:
	break;
}

}

private void createPopupWindow() {
mPopupView = View.inflate(this, R.layout.popup_setting, null);
tvPopupTitle = (TextView) mPopupView
		.findViewById(R.id.tv_setting_title);
tvPopupTitle.setText("请输入安全地址名称：");
etPopupContent = (EditText) mPopupView.findViewById(R.id.et_content);
btnOk = (Button) mPopupView.findViewById(R.id.btn_ok);
btnCancel = (Button) mPopupView.findViewById(R.id.btn_cancel);
btnCancel.setOnClickListener(new OnClickListener() {

	@Override
	public void onClick(View v) {

		settingPopupWindow.dismiss();
	}
});
btnOk.setOnClickListener(new OnClickListener() {

	@Override
	public void onClick(View v) {

		String number = etPopupContent.getText().toString();
		if (!TextUtils.isEmpty(number)) {
			
			areaName = etPopupContent.getText().toString().trim();
			tvAreaName.setText(areaName);
			showPopupWindow();
			
			}else{
				Toast.makeText(AddSafeAreaActivity.this, "安全区域地址不能为空", 0).show();
			}														
	}
});

settingPopupWindow = new PopupWindow(mPopupView, DensityUtil.dip2px(
		this, 250), DensityUtil.dip2px(this, 150), true);
settingPopupWindow.setTouchable(true);
settingPopupWindow.setBackgroundDrawable(new ColorDrawable(00000000));
settingPopupWindow.setOutsideTouchable(true);
}

private void showLoadingDialog(){	
dialog = MyLoadingDialog.getMyLoadingDialog(this,R.style.add_dialog);
dialog.setMessage("数据加载中");
dialog.show();				
}

private void showPopupWindow() {

if (settingPopupWindow.isShowing()) {
	settingPopupWindow.dismiss();
} else {
	// popupWindow.showAsDropDown(rlTop);
	settingPopupWindow
			.showAtLocation(llContainer, Gravity.CENTER, 0, 0);
}

}
}

package com.xsd.safecardapp.activity;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Text;
import com.hysd.usiapp.R;
import com.xsd.safecardapp.javabean.SafeAreaInfoJson.SafeAreaInfoResult;
import com.xsd.safecardapp.utils.Consts;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 修改电子围栏
 *
 */
public class EditSafeAreaActivity extends Activity {

	/**
	 * AMapV2地图中介绍如何使用mapview显示地图
	 */
	// 声明变量
	private MapView mapView;
	private AMap aMap;
	private Marker marker;
	private Circle circle;// Marker周围画个圈

	private SafeAreaInfoResult resultInfo;

	private Intent intent;
	
	private TextView tvName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_safe_area);

		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 必须要写
		aMap = mapView.getMap();
		aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式

		intent = getIntent();
		Bundle bundle = intent.getBundleExtra(Consts.INTENT_DATA);
		resultInfo = (SafeAreaInfoResult) bundle
				.getSerializable("safeareainfo");
		
		tvName = (TextView) findViewById(R.id.tv_name);
		tvName.setText(resultInfo.getName());
		setCurrentPosition(new LatLng(resultInfo.getRegionpoint().getLat(),
				resultInfo.getRegionpoint().getLng()), resultInfo.getRegionpoint().getPoint());
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
	
	public void editSafeArea(View v) {
		
	}
	
	/**
	 * 返回键按钮触发方法
	 * 
	 * @param v
	 */
	public void finishMyself(View v) {
		finish();
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

}

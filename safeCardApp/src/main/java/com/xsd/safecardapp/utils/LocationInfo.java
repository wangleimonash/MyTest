package com.xsd.safecardapp.utils;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

/**
 * 
 * 作者： OnlyWay
 *
 * 作用：获得定位信息工具类
 */
public class LocationInfo implements AMapLocationListener {
	
	private static Activity mContext;
	
	private LocationInfo(){}

	private static LocationInfo locationInfo;
	
	private Double geoLat;
	private Double geoLng;
	
	private Handler mHandler;
	
	public static LocationInfo getInstance(Activity context){
		if(locationInfo == null){
			locationInfo = new LocationInfo();
		} 
		mContext = context;
		return locationInfo;
	}
	
	public void getLatLng(Handler locationHandler){
		mHandler = locationHandler;
		LocationManagerProxy mLocationManagerProxy = LocationManagerProxy.getInstance(mContext);
		//此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
        //在定位结束后，在合适的生命周期调用destroy()方法     
        //其中如果间隔时间为-1，则定位只定一次
        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 60*1000, 15, this);
 
        mLocationManagerProxy.setGpsEnable(false);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if(amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0){
            //获取位置信息
			geoLat= amapLocation.getLatitude();
            geoLng = amapLocation.getLongitude();   
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putDouble("lat", geoLat);
            bundle.putDouble("lng", geoLng);
            bundle.putString("hehe","laozizuile");
            mHandler.sendMessage(msg);
        }
		
	}
	
	
	
	
}

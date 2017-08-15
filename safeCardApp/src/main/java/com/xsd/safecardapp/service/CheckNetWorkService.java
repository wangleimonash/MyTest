package com.xsd.safecardapp.service;

import com.xsd.safecardapp.utils.NetUtils;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CheckNetWorkService extends Service{
	
	private boolean isThreadRunning;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		isThreadRunning = true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
//				while(isThreadRunning){
//					try {
//						Thread.sleep(60000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					
//					if(NetUtils.isNetworkAvailable(CheckNetWorkService.this)){
//						System.out.println("网络连接正常");
//					}else{
//						System.out.println("网络连接异常");
//					}
//				}				
			}
		}).start();
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, START_NOT_STICKY, startId);
	}
	
	@Override
	public void onDestroy() {
		isThreadRunning = false;
		super.onDestroy();
	}
	
}

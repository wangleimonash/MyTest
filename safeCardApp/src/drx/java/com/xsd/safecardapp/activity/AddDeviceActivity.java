package com.xsd.safecardapp.activity;

import com.dtr.zxing.activity.CaptureActivity;


import com.hysd.usiapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

/**
 * 绑定设备界面
 */
public class AddDeviceActivity extends Activity {
	
	private Intent intent;
	private Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_device2);
		
		bundle = getIntent().getBundleExtra("DOTA");
	}
	
	public void quickAddDevice(View v){
		intent = new Intent(this,CaptureActivity.class);
		startActivityForResult(intent, 110);
	}
	
	public void changeCount(View v){
		intent = new Intent(this,UserLoginActivity.class);
		startActivity(intent);
		finish();
	}


	public void flash(View v) {
		intent = new Intent(this,CaptureActivity.class);
		startActivityForResult(intent, 110);
	}

	public void input(View v) {
		intent = new Intent(this,InputDeviceActivity.class);
		startActivityForResult(intent, 110);		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
						
		if(resultCode == 9527){			
			//intent = new Intent(this,MainTabActivity.class);
			//startActivity(intent);
			finish();
			return ;			
		}else{			
			return ;		
		}
	}
	
	/**
	 * 返回键响应事件
	 */
	private int backNumber = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) { 
			Toast.makeText(this, "再按一次退出", 0).show();
			if(backNumber>0)
				finish();
			backNumber ++;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					backNumber = 0;					
				}
			}).start();
			
			return false; 
			}else { 
			return super.onKeyDown(keyCode, event); 
			} 
	}
}

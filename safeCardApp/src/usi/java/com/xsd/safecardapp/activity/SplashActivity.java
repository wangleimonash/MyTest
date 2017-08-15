package com.xsd.safecardapp.activity;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.utils.Consts;
import com.xsd.safecardapp.utils.NetUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;

/**
 * 
 * 作者： OnlyWay
 *
 * 作用： Loading页面
 */
public class SplashActivity extends Activity implements AnimationListener{
	
	private LinearLayout llSplash;
	
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		
		sp = getSharedPreferences(Consts.CONFIG, MODE_PRIVATE);
		
		llSplash = (LinearLayout)findViewById(R.id.ll_splash);
								
		AlphaAnimation aa = new AlphaAnimation(0.7f, 1.0f);
		aa.setDuration(2000);
		aa.setFillAfter(true);
		aa.setAnimationListener(this);
			
		llSplash.setAnimation(aa);
	}

	@Override
	public void onAnimationStart(Animation animation) {
				
	}

	@Override
	public void onAnimationEnd(Animation animation) {	
		
		if(sp.getBoolean(Consts.IS_AUTO_LOGIN, false)){
			if(NetUtils.isNetworkAvailable(this)){
			Intent intent = new Intent(this,MainTabActivity.class);
			startActivity(intent);
			finish();
			return;
			}					
		}	
		Intent intent = new Intent(this,UserLoginActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}
}

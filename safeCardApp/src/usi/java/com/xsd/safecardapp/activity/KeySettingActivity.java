package com.xsd.safecardapp.activity;

import com.hysd.usiapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

/**
 * 按键设置
 *
 */
public class KeySettingActivity extends Activity{
	
	private RelativeLayout rlKeyFamily;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_key_setting);
		rlKeyFamily = (RelativeLayout) findViewById(R.id.rl_key_family);
		rlKeyFamily.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(KeySettingActivity.this,FamilyActivity.class);
				startActivity(i);
			}
		});
		findViewById(R.id.ib_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

}

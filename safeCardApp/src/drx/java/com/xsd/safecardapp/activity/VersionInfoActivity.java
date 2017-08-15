package com.xsd.safecardapp.activity;

import com.hysd.usiapp.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * 版本信息
 * @author OnlyWay
 *
 */
public class VersionInfoActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_version_info);
	}
	
	public void back(View v){
		finish();
	}

}

package com.xsd.safecardapp.activity;

import com.hysd.usiapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 报警详情item
 * @author OnlyWay
 *
 */
public class AlarmDetailActivity extends Activity{
	
	private TextView tvContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_detail);
		tvContent = (TextView) findViewById(R.id.tv_content);
		
		Intent i = getIntent();
		Bundle bundle = i.getBundleExtra("DOTA");
		
		if(bundle != null){
			tvContent.setText(bundle.getString("content"));
		}
	}
	
	
	public void finishMyself(View v){
		finish();
	}

}

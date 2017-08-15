package com.xsd.safecardapp.activity;

import com.hysd.usiapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 作业信息单条展示
 * @author OnlyWay
 *
 */
public class MessageItemActivity extends Activity{
	
	private TextView tvTop;
	private TextView tvTitle;
	private TextView tvContent;
	private TextView tvTime;
	private ImageView ivType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messafe_item);
		
		tvTop = (TextView)findViewById(R.id.tv_top);
		tvTitle = (TextView)findViewById(R.id.tv_message_type_label);
		tvContent = (TextView)findViewById(R.id.tv_detail_content);
		tvTime = (TextView)findViewById(R.id.tv_message_detail_date);
		ivType = (ImageView)findViewById(R.id.iv_message_status);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("DOTA");
		if(bundle!=null){
			tvTop.setText(bundle.getString("title"));
			tvTitle.setText(bundle.getString("title"));
			tvContent.setText(bundle.getString("content"));
			tvTime.setText(bundle.getString("time"));
			if("作业".equals(bundle.getString("title"))){
				ivType.setBackgroundResource(R.drawable.ic_time_1);
			}else{
				ivType.setBackgroundResource(R.drawable.ic_time_2);
			}
		}
	}
	
	public void back(View v){
		finish();
	}

}

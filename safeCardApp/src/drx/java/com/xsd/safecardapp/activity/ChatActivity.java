package com.xsd.safecardapp.activity;

import com.amap.api.maps.model.Text;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hysd.usiapp.R;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 聊天界面
 *
 */
public class ChatActivity extends FragmentActivity{
	
	private RelativeLayout rlContent;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		rlContent = (RelativeLayout) findViewById(R.id.rl_content);

		 //new出EaseChatFragment或其子类的实例
		 EaseChatFragment chatFragment = new EaseChatFragment();
		 //传入参数
		 Bundle args = new Bundle();
		 args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
		 String str = getIntent().getStringExtra("DOTA");
		 String name = getIntent().getStringExtra("NAME");
		 TextView tv = (TextView) findViewById(R.id.tv_name);
		 tv.setText(name);
		 args.putString(EaseConstant.EXTRA_USER_ID, str);
		 chatFragment.setArguments(args);
		 getSupportFragmentManager().beginTransaction().add(R.id.rl_content, chatFragment).commit();
	}
	
	public void finishMyself(View v){
		finish();
	}
	
	

}

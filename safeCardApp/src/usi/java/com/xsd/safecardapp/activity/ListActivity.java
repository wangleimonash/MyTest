package com.xsd.safecardapp.activity;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.easeui.ui.EaseConversationListFragment.EaseConversationListItemClickListener;
import com.hysd.usiapp.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.RelativeLayout;

/**
 * 通讯录
 *
 */
public class ListActivity extends FragmentActivity{
	

	
	private RelativeLayout rlContent;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		rlContent = (RelativeLayout) findViewById(R.id.rl_content);

		EaseConversationListFragment conversationListFragment = new EaseConversationListFragment();
		conversationListFragment.setConversationListItemClickListener(new EaseConversationListItemClickListener() {
		 
			@Override
			public void onListItemClicked(EMConversation conversation) {
				System.out.println("MMMMMM"+conversation.getUserName());
				startActivity(new Intent(ListActivity.this, ChatActivity.class).putExtra(EaseConstant.EXTRA_USER_ID, conversation.getUserName()));
			}
		});
		 getSupportFragmentManager().beginTransaction().add(R.id.rl_content, conversationListFragment).commit();
	}
	
	



}

package com.xsd.safecardapp.adapter;

import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.javabean.HomeworkResult;
import com.xsd.safecardapp.javabean.MessageBean;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeworkLVAdapter extends BaseAdapter {

	private Context mContext;

	private List<MessageBean> msgList;

	public HomeworkLVAdapter(Context mContext, List<MessageBean> msgList) {
		super();
		this.mContext = mContext;
		this.msgList = msgList;
	}

	@Override
	public int getItemViewType(int position) {
		return Integer.parseInt(msgList.get(position).getType())%2;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		return msgList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		ViewHolder2 holder2 = null;
		int viewType = getItemViewType(position);
		if (convertView == null) {
			if (viewType == 0) {
				convertView = View.inflate(mContext, R.layout.item_lv_information,
						null);
				holder = new ViewHolder();
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.tvTime = (TextView) convertView
						.findViewById(R.id.tv_time);
				holder.tvContent =(TextView) convertView
						.findViewById(R.id.tv_content);
				holder.ivMessageStatus =(ImageView) convertView
						.findViewById(R.id.iv_message_status);
				
				convertView.setTag(holder);
			} else {
				convertView = View.inflate(mContext, R.layout.item_homework_lv_notice,
						null);
				holder2 = new ViewHolder2();
				holder2.tvTitle = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder2.tvTime = (TextView) convertView
						.findViewById(R.id.tv_time);
				holder2.tvContent =(TextView) convertView
						.findViewById(R.id.tv_content);
				holder2.ivMessageStatus =(ImageView) convertView
						.findViewById(R.id.iv_message_status);
				convertView.setTag(holder2);
			}

		}else{
			if (viewType == 0) {
				holder =(ViewHolder)convertView.getTag();
			} else {
				holder2 = (ViewHolder2)convertView.getTag();
			}
			
		}
		
		if (viewType == 0) {
			if(msgList.get(position).isRead()){
				holder.ivMessageStatus.setImageResource(R.drawable.ic_time_1_read);
			}
			holder.tvTitle.setText(msgList.get(position).getTitle());
			holder.tvTime.setText(msgList.get(position).getCreateDate());
			holder.tvContent.setText(msgList.get(position).getContent());
		} else {
			if(msgList.get(position).isRead()){
				holder2.ivMessageStatus.setImageResource(R.drawable.ic_time_2_read);
			}
			holder2.tvTitle.setText(msgList.get(position).getTitle());
			holder2.tvTime.setText(msgList.get(position).getCreateDate());
			holder2.tvContent.setText(msgList.get(position).getContent());
		}
		return convertView;
	}

	class ViewHolder {
		ImageView ivMessageStatus;
		TextView tvTitle;
		TextView tvTime;
		TextView tvContent;
	}
	
	class ViewHolder2{
		ImageView ivMessageState;
		ImageView ivMessageStatus;
		TextView tvTitle;
		TextView tvTime;
		TextView tvContent;
	}

}

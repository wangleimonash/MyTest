package com.xsd.safecardapp.adapter;

import java.util.List;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.adapter.HomeworkLVAdapter.ViewHolder;
import com.xsd.safecardapp.javabean.AlarmJson.AlarmResult;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmAdapter extends BaseAdapter{
	
	private Context mContext;
	private List<AlarmResult> result;

	public AlarmAdapter(Context mContext, List<AlarmResult> result) {
		super();
		this.mContext = mContext;
		this.result = result;
	}

	@Override
	public int getCount() {
		return result.size();
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
		if(convertView == null){
			convertView = View.inflate(mContext, R.layout.item_alarm_lv, null);
			holder = new ViewHolder();
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.tv_title);
			holder.tvTime = (TextView) convertView
					.findViewById(R.id.tv_time);
			holder.tvContent =(TextView) convertView
					.findViewById(R.id.tv_content);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv_message_status);
			convertView.setTag(holder);
		}else{
			holder =(ViewHolder)convertView.getTag();
		}
		    holder.tvTitle.setText(result.get(position).getType());
		    holder.tvTime.setText(result.get(position).getTime());
		    holder.tvContent.setText(result.get(position).getDesc());
		    if(result.get(position).getType().equals("SOS报警")){
		    	holder.iv.setBackgroundResource(R.drawable.ic_time_1);
		    }else{
		    	holder.iv.setBackgroundResource(R.drawable.ic_time_2);
		    }
		return convertView;
	}
	
	class ViewHolder {
		TextView tvTitle;
		TextView tvTime;
		TextView tvContent;
		ImageView iv;
	}

}

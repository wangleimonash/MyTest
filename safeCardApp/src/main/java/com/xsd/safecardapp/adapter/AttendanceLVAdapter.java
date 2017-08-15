package com.xsd.safecardapp.adapter;

import java.util.List;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.javabean.StudentAttendanceJson.AttendanceResult;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AttendanceLVAdapter extends BaseAdapter{

	private Context mContext;
	private List<AttendanceResult> result;
	
	public AttendanceLVAdapter(Context mContext, List<AttendanceResult> result) {
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
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(mContext, R.layout.item_att_detal_lv, null);
			holder = new ViewHolder();
			holder.tvTimeAttDetil = (TextView)convertView.findViewById(R.id.tv_att_time);
			holder.tvStatus = (TextView)convertView.findViewById(R.id.tv_att_status);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.tvTimeAttDetil.setText(result.get(position).getTime());
		holder.tvStatus.setText(result.get(position).getFlag());
		return convertView;
	}

	class ViewHolder{
		TextView tvTimeAttDetil;
		TextView tvStatus;
	}
}

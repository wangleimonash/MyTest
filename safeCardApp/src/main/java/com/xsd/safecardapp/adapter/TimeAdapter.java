package com.xsd.safecardapp.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.javabean.TimeBean;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TimeAdapter extends BaseAdapter{
	
	private List<TimeBean> timeBeanlist;
	private Context mContext;
	
	public TimeAdapter(List<TimeBean> timeBeanlist,Context context){
		this.timeBeanlist = timeBeanlist;
		this.mContext = context;
	};

	@Override
	public int getCount() {
		return timeBeanlist.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(mContext, R.layout.item_lv_time, null);
			holder = new ViewHolder();
			holder.tv = (TextView) convertView.findViewById(R.id.tv);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.tv.setText(""+timeString(timeBeanlist.get(arg0).getsTime())+"- -"+timeString(timeBeanlist.get(arg0).geteTime()));
		
		return convertView;
	}
	
	class ViewHolder{
		TextView tv;
	}
	public String timeString(String str){
		StringBuilder sb = new StringBuilder(str);
		sb.insert(2, ":");
		return sb.toString();
	}
}

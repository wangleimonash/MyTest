package com.xsd.safecardapp.adapter;

import java.util.List;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.javabean.SafeAreaInfoJson.SafeAreaInfoResult;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SafeAreaLVAdapter extends BaseAdapter {

	private Context mContext;

	private List<SafeAreaInfoResult> infoResult;

	public SafeAreaLVAdapter(Context mContext,
			List<SafeAreaInfoResult> infoResult) {
		super();
		this.mContext = mContext;
		this.infoResult = infoResult;
	}

	@Override
	public int getCount() {
		return infoResult.size();
	}

	@Override
	public Object getItem(int position) {
		return infoResult.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_safe_area_lv,
					null);
			holder = new ViewHolder();
			holder.ivType = (ImageView) convertView
					.findViewById(R.id.iv_safe_area_lv);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.tv_name_safe_area_lv);
			holder.tvRange = (TextView) convertView
					.findViewById(R.id.tv_range_safe_area_lv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(infoResult.get(position).getName().contains("家")){
			holder.ivType.setImageResource(R.drawable.ic_zone_b);
		}else if(infoResult.get(position).getName().contains("校")){
			holder.ivType.setImageResource(R.drawable.ic_zone_g);
		}else{
			holder.ivType.setImageResource(R.drawable.ic_zone_y);
		}
		holder.tvName.setText(infoResult.get(position).getName());
		holder.tvRange.setText("直径"+infoResult.get(position).getRegionpoint().getPoint()+"米");
				
		return convertView;
	}

	class ViewHolder {
		ImageView ivType;
		TextView tvName;
		TextView tvRange;
	}

}

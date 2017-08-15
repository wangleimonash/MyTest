package com.xsd.safecardapp.adapter;

import java.util.List;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.javabean.SafeAreaInfoJson.SafeAreaInfoResult;
import com.xsd.safecardapp.javabean.WhiteBean.WhiteEntity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WhileListLVAdapter extends BaseAdapter {

	private Context mContext;

	private List<WhiteEntity> infoResult;

	public WhileListLVAdapter(Context mContext,
			List<WhiteEntity> infoResult) {
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
			convertView = View.inflate(mContext, R.layout.item_lv_white_list,
					null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView
					.findViewById(R.id.tv_name_safe_area_lv);
			holder.tvRange = (TextView) convertView
					.findViewById(R.id.tv_range_safe_area_lv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		holder.tvName.setText(infoResult.get(position).getNM());
		holder.tvRange.setText("号码："+infoResult.get(position).getPC());
				
		return convertView;
	}

	class ViewHolder {		
		TextView tvName;
		TextView tvRange;
	}

}

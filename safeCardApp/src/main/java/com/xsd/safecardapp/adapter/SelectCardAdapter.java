package com.xsd.safecardapp.adapter;

import java.util.List;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.activity.MainTabActivity;
import com.xsd.safecardapp.javabean.LoginJson.LoginResult;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SelectCardAdapter extends BaseAdapter{
	
	private Context mContext;
	private List<LoginResult> mResult;

	public SelectCardAdapter(Context mContext, List<LoginResult> mResult) {
		super();
		this.mContext = mContext;
		this.mResult = mResult;
	}

	@Override
	public int getCount() {
		return mResult == null?0:mResult.size();
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
				convertView = View.inflate(mContext, R.layout.item_lv_select_card, null);
				holder = new ViewHolder();
				holder.tvSelectCard = (TextView)convertView.findViewById(R.id.tv_select_card);
				holder.tvImei = (TextView)convertView.findViewById(R.id.tv_imei);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.tvSelectCard.setText(mResult.get(position).getUserName());
			if(!TextUtils.isEmpty(mResult.get(position).getImei()))
			holder.tvImei.setText(mResult.get(position).getImei());
		return convertView;
	}

	class ViewHolder{
		TextView tvSelectCard;
		TextView tvImei;
	}
}

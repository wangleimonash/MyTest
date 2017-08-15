package com.xsd.safecardapp.adapter;

import java.util.List;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.activity.MainTabActivity;
import com.xsd.safecardapp.javabean.LoginJson.LoginResult;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CardInfoLVAdapter extends BaseAdapter{
	
	private Context mContext;
	
	private List<LoginResult> mResult;

	public CardInfoLVAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		mResult = MainTabActivity.getmResult();
	}

	@Override
	public int getCount() {
		return mResult.size();
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
			convertView = View.inflate(mContext, R.layout.item_lv_cardinfo, null);
			holder = new ViewHolder();
			holder.tvName = (TextView)convertView.findViewById(R.id.tv_name);
			holder.tvNumber = (TextView)convertView.findViewById(R.id.tv_number);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.tvName.setText(mResult.get(position).getUserName());
		holder.tvNumber.setText(mResult.get(position).getImei());
		return convertView;
	}
	
	class ViewHolder{
		TextView tvName;
		TextView tvNumber;
		
	}

}

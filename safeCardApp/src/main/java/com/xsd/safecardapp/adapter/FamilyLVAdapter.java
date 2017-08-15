package com.xsd.safecardapp.adapter;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.javabean.FamilyJson.FamilyResult;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FamilyLVAdapter extends BaseAdapter{

	private Context mContext;
	private FamilyResult mResult;
	private String[] mTel;
	private String[] names = {"亲情号码A","亲情号码B","亲情号码C"};
		
	public FamilyLVAdapter(Context mContext, FamilyResult mResult) {
		super();
		this.mContext = mContext;
		this.mResult = mResult;		
	}

	@Override
	public int getCount() {
		
		return 3;
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
		
		String[] mTel = {mResult.getKey1(),mResult.getKey2(),mResult.getKey3()};
		
		if(convertView == null){
			convertView = View.inflate(mContext, R.layout.item_lv_family, null);
			holder = new ViewHolder();
			holder.tvNumber = (TextView)convertView.findViewById(R.id.tv_number);
			holder.tvDesc = (TextView)convertView.findViewById(R.id.tv_desc);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.tvNumber.setText(mTel[position]);
		holder.tvDesc.setText(names[position]);
		return convertView;
	}

	class ViewHolder{		
		TextView tvNumber;
		TextView tvDesc;
	}
}

package com.xsd.safecardapp.adapter;

import com.hysd.usiapp.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MeFragmentGVAdapter extends BaseAdapter{

	private Context mContext;
	
	private String[] names = {"家庭成员","学生卡信息",
			"电子围栏","推荐好友",
			};
	
	private int[] imgs = {R.drawable.ic_family_members,R.drawable.ic_greencard,
			R.drawable.ic_safe_zone,R.drawable.ic_bt,
			R.drawable.ic_recharge,R.drawable.ic_shutdwon
	};
	
	public MeFragmentGVAdapter(Context mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return names.length;
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
		View rootView = View.inflate(mContext, R.layout.item_gv_mefragment, null);
		ViewHolder holder = new ViewHolder();
		holder.ivGridView = (ImageView)rootView.findViewById(R.id.iv_mefragment_gv);
		holder.tvGridView = (TextView)rootView.findViewById(R.id.tv_mefragment_gv);
		holder.ivGridView.setImageResource(imgs[position]);
		holder.tvGridView.setText(names[position]);
		return rootView;
	}
	
	class ViewHolder{
		ImageView ivGridView;
		TextView tvGridView;
	}

}

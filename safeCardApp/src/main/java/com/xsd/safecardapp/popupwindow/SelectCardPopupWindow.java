package com.xsd.safecardapp.popupwindow;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.activity.MainTabActivity;
import com.xsd.safecardapp.adapter.SelectCardAdapter;
import com.xsd.safecardapp.utils.DensityUtil;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.LinearLayout.LayoutParams;

public class SelectCardPopupWindow {
	
	private Context mContext;
	private static View popupView;
	private static ListView lvPopupView;

	public SelectCardPopupWindow(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	private static PopupWindow popupWindow;
	
	public static PopupWindow getPopupWindow(Context mContext,OnItemClickListener onItemClick){
		if(popupWindow == null){
			popupView = View.inflate(mContext, R.layout.popup_select_card, null);
			lvPopupView = (ListView) popupView.findViewById(R.id.lv_select_card);
			lvPopupView.setAdapter(new SelectCardAdapter(mContext, MainTabActivity.getmResult()));
			lvPopupView.setOnItemClickListener(onItemClick);
			popupWindow= new PopupWindow(popupView, DensityUtil.dip2px(
					mContext, 150),
					LayoutParams.WRAP_CONTENT, true);
			popupWindow.setTouchable(true);
			popupWindow.setBackgroundDrawable(new ColorDrawable(00000000));
			popupWindow.setOutsideTouchable(true);
		}
		return popupWindow;
	}

}

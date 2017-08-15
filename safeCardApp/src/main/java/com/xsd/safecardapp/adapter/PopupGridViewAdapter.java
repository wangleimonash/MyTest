package com.xsd.safecardapp.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.activity.ShowPathActivity;
import com.xsd.safecardapp.javabean.DateBean;
import com.xsd.safecardapp.utils.RequestDataUtils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class PopupGridViewAdapter extends BaseAdapter {

	private Context context;

	private String imei;

	private Calendar calendar;

	private TextView tv;

	public static int todayPositon;

	public static List<DateBean> listData = new ArrayList<DateBean>();

	public PopupGridViewAdapter(Context context, String imei) {
		super();
		this.context = context;
		this.imei = imei;
		calendar = Calendar.getInstance();
		getData();
	}

	@Override
	public int getCount() {
		return 35;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rootView = View.inflate(context, R.layout.item_gv, null);
		tv = (TextView) rootView.findViewById(R.id.tv_date);
		DateBean bean = listData.get(position);
		tv.setText(bean.getDay()+"日");
		
		if (bean.getDay() == 1) {
			tv.setText(bean.getMonth()+"月");
			tv.setTextColor(Color.CYAN);
		}
		if (bean.isToday()) {
			tv.setBackgroundResource(R.drawable.bg_calendar);
		}
		if(position>todayPositon){
			tv.setBackgroundResource(R.color.login_gray);
			tv.setTextColor(Color.GRAY);
		}		
		return rootView;
	}

	class ViewHolder {
		TextView tv;
	}

	private void getData() {
		int week = calendar.get(Calendar.DAY_OF_WEEK);
		for (int i = 27 + week; i > 0; i--) {
			Calendar headCalender = Calendar.getInstance();
			DateBean dateBean = new DateBean();
			Calendar test = setStartTime(headCalender, -i);
			dateBean.setYear(test.get(Calendar.YEAR));
			dateBean.setMonth(test.get(Calendar.MONTH) + 1);
			dateBean.setDay(test.get(Calendar.DATE));
			dateBean.setWeek(test.get(Calendar.DAY_OF_WEEK));
			listData.add(dateBean);
		}
		todayPositon = 27 + week;
		// Calendar calendar3 = Calendar.getInstance();
		DateBean datedsaBean = new DateBean();
		datedsaBean.setYear(calendar.get(Calendar.YEAR));
		datedsaBean.setMonth(calendar.get(Calendar.MONTH) + 1);
		datedsaBean.setDay(calendar.get(Calendar.DATE));
		datedsaBean.setWeek(calendar.get(Calendar.DAY_OF_WEEK));
		datedsaBean.setToday(true);
		listData.add(datedsaBean);

		int j = week;
		int k = 1;
		while (7 - j > 0) {
			Calendar footCalendar = Calendar.getInstance();
			DateBean dateBean = new DateBean();
			Calendar test = setStartTime(footCalendar, k);
			dateBean.setYear(test.get(Calendar.YEAR));
			dateBean.setMonth(test.get(Calendar.MONTH) + 1);
			dateBean.setDay(test.get(Calendar.DATE));
			dateBean.setWeek(test.get(Calendar.DAY_OF_WEEK));
			listData.add(dateBean);
			k++;
			j++;
		}

	}

	private static Calendar setStartTime(Calendar calendar, int day) {
		calendar.add(Calendar.DATE, day);
		return calendar;
	}

	public List<DateBean> getListData() {
		return listData;
	}

	public void setListData(List<DateBean> listData) {
		this.listData = listData;
	}

}

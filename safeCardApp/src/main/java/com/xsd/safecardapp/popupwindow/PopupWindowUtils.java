package com.xsd.safecardapp.popupwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.hysd.usiapp.R;
import com.xsd.safecardapp.views.CalendarView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by du on 16/5/28.
 */
public class PopupWindowUtils {
    public static void show(Context context,Calendar calendar,View view,final OnPoupuItemClickListener onPoupuItemClickListener){
        View contentView = View.inflate(context,R.layout.popup_calendar,null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        CalendarView calendarView = (CalendarView)contentView.findViewById(R.id.clv_date);
        calendarView.setSelectMore(false);
        calendarView.setCalendarData(calendar.getTime());
        calendarView.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void OnItemClick(Date selectedStartDate, Date selectedEndDate, Date downDate) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(selectedStartDate);
                if(onPoupuItemClickListener!=null){
                    onPoupuItemClickListener.itemClick(calendar1,popupWindow);
                }
            }
        });
        popupWindow.setTouchable(true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);
    }


    public static interface OnPoupuItemClickListener{
        void itemClick(Calendar calendar, PopupWindow popupWindow);
    }
}

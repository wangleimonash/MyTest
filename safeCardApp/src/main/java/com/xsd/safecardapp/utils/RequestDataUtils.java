package com.xsd.safecardapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.xsd.safecardapp.javabean.DateBean;

/**
 * 
 * 作者： OnlyWay
 *
 * 作用： 与服务器交互用到的工具
 */
public class RequestDataUtils {

	private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String getEndTime(){
		String curretTime = formatTime.format(new Date());
		return curretTime;
	}
	
	public static String getStartTime(int day){		
        Calendar calendar = Calendar.getInstance();
        calendar = setStartTime(calendar,-day);
        String startTime = formatTime.format(calendar.getTime());
		return startTime;
	}
	
	private static Calendar setStartTime(Calendar calendar, int day) {
		calendar.add(Calendar.DATE, day);
        return calendar;
    }
	
	public static String getDailyDate(DateBean datebean){
		String startTime =datebean.getYear()+"-"+datebean.getMonth()+"-"+datebean.getDay();
		return startTime;
	}
	
	public static String getStartTimeByDate(DateBean datebean){
		String startTime =datebean.getYear()+"-"+datebean.getMonth()+"-"+datebean.getDay()+" 00:00:01";
		return startTime;
	}
	public static String getEndTimeByDate(DateBean datebean){
		String endTime =datebean.getYear()+"-"+datebean.getMonth()+"-"+datebean.getDay()+" 23:59:59";
		return endTime;
	}
}

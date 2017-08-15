package com.xsd.safecardapp.javabean;

import java.io.Serializable;
import java.util.List;

public class TimeBean implements Serializable{
	
	private String sTime;
	private String eTime;
	public String getsTime() {
		return sTime;
	}
	public void setsTime(String sTime) {
		this.sTime = sTime;
	}
	public String geteTime() {
		return eTime;
	}
	public void seteTime(String eTime) {
		this.eTime = eTime;
	}
	
	private static  List<TimeBean> tlist;
	
	public static List<TimeBean> getList() {
		return tlist;
	}
	public static void setList(List<TimeBean> list) {
		tlist = list;
	}

}

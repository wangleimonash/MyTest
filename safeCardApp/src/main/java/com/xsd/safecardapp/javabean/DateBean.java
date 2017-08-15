package com.xsd.safecardapp.javabean;

public class DateBean {
	
	private int year;
	private int month;
	private int day;
	private int week;
    public boolean isToday() {
		return isToday;
	}
	public void setToday(boolean isToday) {
		this.isToday = isToday;
	}
	private boolean isToday;
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getWeek() {
		return week;
	}
	public void setWeek(int week) {
		this.week = week;
	}

}

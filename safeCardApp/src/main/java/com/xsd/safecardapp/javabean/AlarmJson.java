package com.xsd.safecardapp.javabean;

import java.util.List;

public class AlarmJson {

	private String code;
	private List<AlarmResult> result;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<AlarmResult> getResult() {
		return result;
	}

	public void setResult(List<AlarmResult> result) {
		this.result = result;
	}

	public class AlarmResult{
       private double lat;
       private double lng;
       private String time;
       private String desc;
       private String type;
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	}
}

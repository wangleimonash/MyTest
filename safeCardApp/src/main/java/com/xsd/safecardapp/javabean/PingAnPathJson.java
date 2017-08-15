package com.xsd.safecardapp.javabean;

import java.util.List;

/**
 * 
 * 作者： OnlyWay
 *
 * 作用：Gson解析JavaBean
 */
public class PingAnPathJson {
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<PathResult> getResult() {
		return result;
	}

	public void setResult(List<PathResult> result) {
		this.result = result;
	}

	private String code;
	
	private List<PathResult> result; 
	
	public class PathResult{
		String desc;
		double lat;
		double lng;
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
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
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		String time;
		String type;
	}

}

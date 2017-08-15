package com.xsd.safecardapp.javabean;

/**
 * 
 * 作者： OnlyWay
 *
 * 作用：Gson解析JavaBean
 */
public class PingAnJson {
	
	public String code;
	
	private Result result;
	
	public class Result{
		
		private String desc;
		private double lat;
		private double lng;
		private String time;
		private String type;
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
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

}

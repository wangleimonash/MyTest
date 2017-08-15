package com.xsd.safecardapp.javabean;

public class ModeBean {

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public ModeEntity getResult() {
		return result;
	}

	public void setResult(ModeEntity result) {
		this.result = result;
	}

	public int code;
	public ModeEntity result;
	
	public class ModeEntity{
		public String stime;
		public String etime;
		public String location;
		public String getStime() {
			return stime;
		}
		public void setStime(String stime) {
			this.stime = stime;
		}
		public String getEtime() {
			return etime;
		}
		public void setEtime(String etime) {
			this.etime = etime;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
	}
}

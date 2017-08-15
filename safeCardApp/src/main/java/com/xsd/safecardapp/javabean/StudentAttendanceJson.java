package com.xsd.safecardapp.javabean;

import java.util.List;

public class StudentAttendanceJson {

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	

	public List<AttendanceResult> getResult() {
		return result;
	}

	public void setResult(List<AttendanceResult> result) {
		this.result = result;
	}



	private String code;
	
	private List<AttendanceResult> result;
	
	public class AttendanceResult{
		String flag;
		String time;
		public String getFlag() {
			return flag;
		}
		public void setFlag(String flag) {
			this.flag = flag;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
	}
}

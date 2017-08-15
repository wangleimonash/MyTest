package com.xsd.safecardapp.javabean;

import java.util.List;

public class LoginJson {

	private String code;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<LoginResult> getResult() {
		return result;
	}

	public void setResult(List<LoginResult> result) {
		this.result = result;
	}

	private List<LoginResult> result;
	
	public class LoginResult{
		private String imei;
		private String PhoneNumber;
		private String UserId;
		private String UserName;
		public String ClassId;
		private String UserNo;
		public String getUserNo() {
			return UserNo;
		}

		public void setUserNo(String userNo) {
			UserNo = userNo;
		}


		public String getClassId() {
			return ClassId;
		}
		public void setClassId(String classId) {
			ClassId = classId;
		}
		public String getUserId() {
			return UserId;
		}

		public void setUserId(String userId) {
			UserId = userId;
		}



		public String getImei() {
			return imei;
		}

		public void setImei(String imei) {
			this.imei = imei;
		}

		public String getPhoneNumber() {
			return PhoneNumber;
		}

		public void setPhoneNumber(String phoneNumber) {
			PhoneNumber = phoneNumber;
		}

		public String getUserName() {
			return UserName;
		}

		public void setUserName(String userName) {
			UserName = userName;
		}


	}
}

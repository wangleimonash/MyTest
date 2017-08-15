package com.xsd.safecardapp.javabean;

import java.util.List;

public class CardInfoBean {

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public List<CardData> getData() {
		return data;
	}

	public void setData(List<CardData> data) {
		this.data = data;
	}

	public String result;
	
	public List<CardData> data;
	
	public class CardData {
		public String fns1;
		public String fns2;
		public String getFns1() {
			return fns1;
		}
		public void setFns1(String fns1) {
			this.fns1 = fns1;
		}
		public String getFns2() {
			return fns2;
		}
		public void setFns2(String fns2) {
			this.fns2 = fns2;
		}
		public String getFns3() {
			return fns3;
		}
		public void setFns3(String fns3) {
			this.fns3 = fns3;
		}
		public String getImei() {
			return imei;
		}
		public void setImei(String imei) {
			this.imei = imei;
		}
		public String getIokid() {
			return iokid;
		}
		public void setIokid(String iokid) {
			this.iokid = iokid;
		}
		public String getPc() {
			return pc;
		}
		public void setPc(String pc) {
			this.pc = pc;
		}
		public String fns3;
		public String imei;
		public String iokid;
		public String pc;
	}
}

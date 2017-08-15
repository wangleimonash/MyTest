package com.xsd.safecardapp.javabean;

import java.util.List;

public class WhiteBean {
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public List<WhiteEntity> getResult() {
		return result;
	}

	public void setResult(List<WhiteEntity> result) {
		this.result = result;
	}

	public int code;
	public List<WhiteEntity> result;
	
	public class WhiteEntity{
		
		public String getID() {
			return ID;
		}
		public void setID(String nO) {
			ID = nO;
		}
		public String getNM() {
			return NM;
		}
		public void setNM(String nM) {
			NM = nM;
		}
		public String getPC() {
			return PC;
		}
		public void setPC(String pC) {
			PC = pC;
		}
		String ID;
		String NM;
		String PC;
	}

}

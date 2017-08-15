package com.xsd.safecardapp.javabean;

import java.io.Serializable;
import java.util.List;

public class SafeAreaInfoJson implements Serializable{

	private String code;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<SafeAreaInfoResult> getResult() {
		return result;
	}

	public void setResult(List<SafeAreaInfoResult> result) {
		this.result = result;
	}

	private List<SafeAreaInfoResult> result;
	
	
	public static class SafeAreaInfoResult implements Serializable{
		
		private String name;
		public String getName() {
			return name;
		}


		public void setName(String name) {
			this.name = name;
		}


		public int getRegionid() {
			return regionid;
		}


		public void setRegionid(int regionid) {
			this.regionid = regionid;
		}


		public Regionpoint getRegionpoint() {
			return regionpoint;
		}


		public void setRegionpoint(Regionpoint regionpoint) {
			this.regionpoint = regionpoint;
		}


		public int getShape() {
			return shape;
		}


		public void setShape(int shape) {
			this.shape = shape;
		}


		private int regionid;
		private Regionpoint regionpoint;
		private int shape = 2;
		
		
		public static class Regionpoint implements Serializable{
			private double lat;
			private double lng;
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
			public int getPoint() {
				return point;
			}
			public void setPoint(int point) {
				this.point = point;
			}
			private int point;
		}
	}
	
}

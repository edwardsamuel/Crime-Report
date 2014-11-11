package com.ganesia.crimereport.models;

public class SafetyRatingItem {

	String crimeType;
	String crimeDate;
	String crimeStatus;
	
	public SafetyRatingItem(String crimeType, String crimeDate, String crimeStatus){
		this.crimeType = crimeType;
		this.crimeStatus = crimeStatus;
		this.crimeDate = crimeDate;
	}
	
	public String getCrimeType() {
		return crimeType;
	}
	public void setCrimeType(String crimeType) {
		this.crimeType = crimeType;
	}
	public String getCrimeDate() {
		return crimeDate;
	}
	public void setCrimeDate(String crimeDate) {
		this.crimeDate = crimeDate;
	}
	public String getCrimeStatus() {
		return crimeStatus;
	}
	public void setCrimeStatus(String crimeStatus) {
		this.crimeStatus = crimeStatus;
	}
}

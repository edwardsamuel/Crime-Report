package com.ganesia.crimereport.models;

public class SafetyRatingItem {

	private String crimeType;
	private String crimeArrestedNum;
	private String crimeWantedNum;
	
	public SafetyRatingItem(String crimeType, String crimeArrestedNum, String crimeWantedNum){
		this.crimeType = crimeType;
		this.crimeArrestedNum = crimeArrestedNum;
		this.crimeWantedNum = crimeWantedNum;
	}	
	
	public String getCrimeType() {
		return crimeType;
	}

	public void setCrimeType(String crimeType) {
		this.crimeType = crimeType;
	}

	public String getCrimeArrestedNum() {
		return crimeArrestedNum;
	}

	public void setCrimeArrestedNum(String crimeArrestedNum) {
		this.crimeArrestedNum = crimeArrestedNum;
	}

	public String getCrimeWantedNum() {
		return crimeWantedNum;
	}

	public void setCrimeWantedNum(String crimeWantedNum) {
		this.crimeWantedNum = crimeWantedNum;
	}
	
}

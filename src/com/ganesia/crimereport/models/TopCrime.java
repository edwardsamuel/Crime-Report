package com.ganesia.crimereport.models;

public class TopCrime {

	String crimeTitle;
	String crimeCount;
	
	public TopCrime(String crimeTitle, String crimeCount) {
		super();
		this.crimeTitle = crimeTitle;
		this.crimeCount = crimeCount;
	}
	
	public String getCrimeTitle() {
		return crimeTitle;
	}

	public void setCrimeTitle(String crimeTitle) {
		this.crimeTitle = crimeTitle;
	}

	public String getCrimeCount() {
		return crimeCount;
	}

	public void setCrimeCount(String crimeCount) {
		this.crimeCount = crimeCount;
	}
}

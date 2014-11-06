package com.ganesia.crimereport.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class SafetyRating {
	
	private String safetyRating;
	private String safetyRatingDescription;
	private String crimeReportDate;
	private double latitude;
	private String crimeStartDate;
	private int radius;
	private List<Object> crimeList = new ArrayList<Object>();
	private double longitude;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	/**
	* 
	* @return
	* The safetyRating
	*/
	public String getSafetyRating() {
		return safetyRating;
	}
	
	/**
	* 
	* @param safetyRating
	* The safetyRating
	*/
	public void setSafetyRating(String safetyRating) {
		this.safetyRating = safetyRating;
	}
	
	/**
	* 
	* @return
	* The safetyRatingDescription
	*/
	public String getSafetyRatingDescription() {
		return safetyRatingDescription;
	}
	
	/**
	* 
	* @param safetyRatingDescription
	* The safetyRatingDescription
	*/
	public void setSafetyRatingDescription(String safetyRatingDescription) {
		this.safetyRatingDescription = safetyRatingDescription;
	}
	
	/**
	* 
	* @return
	* The crimeReportDate
	*/
	public String getCrimeReportDate() {
		return crimeReportDate;
	}
	
	/**
	* 
	* @param crimeReportDate
	* The crimeReportDate
	*/
	public void setCrimeReportDate(String crimeReportDate) {
		this.crimeReportDate = crimeReportDate;
	}
	
	/**
	* 
	* @return
	* The latitude
	*/
	public double getLatitude() {
		return latitude;
	}
	
	/**
	* 
	* @param latitude
	* The latitude
	*/
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	/**
	* 
	* @return
	* The crimeStartDate
	*/
	public String getCrimeStartDate() {
		return crimeStartDate;
	}
	
	/**
	* 
	* @param crimeStartDate
	* The crimeStartDate
	*/
	public void setCrimeStartDate(String crimeStartDate) {
		this.crimeStartDate = crimeStartDate;
	}
	
	/**
	* 
	* @return
	* The radius
	*/
	public int getRadius() {
		return radius;
	}
	
	/**
	* 
	* @param radius
	* The radius
	*/
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	/**
	* 
	* @return
	* The crimeList
	*/
	public List<Object> getCrimeList() {
		return crimeList;
	}
	
	/**
	* 
	* @param crimeList
	* The crimeList
	*/
	public void setCrimeList(List<Object> crimeList) {
		this.crimeList = crimeList;
	}
	
	/**
	* 
	* @return
	* The longitude
	*/
	public double getLongitude() {
		return longitude;
	}
	
	/**
	* 
	* @param longitude
	* The longitude
	*/
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}
	
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
	
//	public String toString() {
//		Map<String, List<CrimeItem>> hm = new HashMap<String, List<CrimeItem>>();
//		String result = "";
//		result = this.safetyRating + "/n" + this.safetyRatingDescription;
//		for (Object c : crimeList) {
//			if (c.getClass().equals(String.class)) {
//				// type of the crime
//			}
//			
//		}
//		return "";
//	}

}
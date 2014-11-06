package com.ganesia.crimereport.models;

import java.util.HashMap;
import java.util.Map;
public class CrimeItem {

private String crimeCaseID;
private String note;
private String crimeAddress;
private Double crimeReportDate;
private Double latitude;
private String timeZone;
private Double crimeDate;
private String crimeType;
private Double longitude;
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
* 
* @return
* The crimeCaseID
*/
public String getCrimeCaseID() {
return crimeCaseID;
}

/**
* 
* @param crimeCaseID
* The crimeCaseID
*/
public void setCrimeCaseID(String crimeCaseID) {
		this.crimeCaseID = crimeCaseID;
	}
	
	/**
	* 
	* @return
	* The note
	*/
	public String getNote() {
		return note;
	}
	
	/**
	* 
	* @param note
	* The note
	*/
	public void setNote(String note) {
		this.note = note;
	}
	
	/**
	* 
	* @return
	* The crimeAddress
	*/
	public String getCrimeAddress() {
		return crimeAddress;
	}
	
	/**
	* 
	* @param crimeAddress
	* The crimeAddress
	*/
	public void setCrimeAddress(String crimeAddress) {
		this.crimeAddress = crimeAddress;
	}
	
	/**
	* 
	* @return
	* The crimeReportDate
	*/
	public Double getCrimeReportDate() {
		return crimeReportDate;
	}
	
	/**
	* 
	* @param crimeReportDate
	* The crimeReportDate
	*/
	public void setCrimeReportDate(Double crimeReportDate) {
		this.crimeReportDate = crimeReportDate;
	}
	
	/**
	* 
	* @return
	* The latitude
	*/
	public Double getLatitude() {
		return latitude;
	}
	
	/**
	* 
	* @param latitude
	* The latitude
	*/
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	/**
	* 
	* @return
	* The timeZone
	*/
	public String getTimeZone() {
		return timeZone;
	}
	
	/**
	* 
	* @param timeZone
	* The timeZone
	*/
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	/**
	* 
	* @return
	* The crimeDate
	*/
	public Double getCrimeDate() {
		return crimeDate;
	}
	
	/**
	* 
	* @param crimeDate
	* The crimeDate
	*/
	public void setCrimeDate(Double crimeDate) {
		this.crimeDate = crimeDate;
	}
	
	/**
	* 
	* @return
	* The crimeType
	*/
	public String getCrimeType() {
		return crimeType;
	}
	
	/**
	* 
	* @param crimeType
	* The crimeType
	*/
	public void setCrimeType(String crimeType) {
		this.crimeType = crimeType;
	}
	
	/**
	* 
	* @return
	* The longitude
	*/
	public Double getLongitude() {
		return longitude;
	}
	
	/**
	* 
	* @param longitude
	* The longitude
	*/
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}
	
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
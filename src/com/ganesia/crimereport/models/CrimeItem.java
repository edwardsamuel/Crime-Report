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
	private Long crimeDate;
	private String crimeType;
	private Double longitude;
	private String type;
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	private Boolean arrest;
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
		// set and parse Note
		// e.g. note: "FOUND SUSPECT NARCOTICS, arrest:true"
		// into
		// note = FOUND SUSPECT NARCOTICS
		// arrest = true
		String[] words = note.split(", ");
		String arrest = words[1].split("arrest:")[1];
		this.setArrest(Boolean.parseBoolean(arrest));
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
		// set and parse CrimeAddress
		// e.g. crimeAddress: "079XX S EAST END AVE, type:STREET, district:004, community_area:46"
		// into 
		// crimeAddress = "079XX S EAST END AVE"
		// type = STREET
		String[] words = crimeAddress.split(", ");
		String type = words[1].split("type:")[1];
		this.setType(type);
		this.crimeAddress = words[0];
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
	public Long getCrimeDate() {
		return crimeDate;
	}
	
	/**
	* 
	* @param crimeDate
	* The crimeDate
	*/
	public void setCrimeDate(Long crimeDate) {
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

	/**
	 * @return the type from crimeAddress
	 * e.g. crimeAddress: "0000X S LA SALLE ST, type:STREET, district:001, community_area:32
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the arrest
	 */
	public boolean isArrest() {
		return arrest;
	}

	/**
	 * @param arrest the arrest to set
	 */
	public void setArrest(Boolean arrest) {
		this.arrest = arrest;
	}

}
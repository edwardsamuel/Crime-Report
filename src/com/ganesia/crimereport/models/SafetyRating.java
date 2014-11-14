package com.ganesia.crimereport.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
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
	
	public SafetyRating() {
		Log.d("coba2","--hidup--");
	}
	
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
		Log.d("coba", "createCrimeList");
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
	
	public HashMap<String, ArrayList<CrimeItem>> getNearestCrimeList() {
		// Return a linked hash map, with 
		// K : Crime Type  (Vehicle Theft, Narcotics, etc.)
		// V : List <CrimeItem>
		String key="";
		HashMap<String, ArrayList<CrimeItem>> hm = new HashMap<String, ArrayList<CrimeItem>>();
		
		for (Object c : this.crimeList) {
			// since the crimeList are consist of String and List<CrimeItem>
			// it needs to be separated into Key:String and V :List<CrimeItem>
			if (c.getClass().equals(String.class)) {
				// Key
				key = (String) c;
				// Instantiate empty list for its Value
				ArrayList<CrimeItem> tempValue = new ArrayList<CrimeItem>();
				hm.put(key, tempValue);
			}
			else {
				// Value
				// get the list from latest key,
				// since the order of API is always String-List<Object>-String-List
				// List <ArrayList<Map<K,V>>
				// K : CrimeCaseID, Note, Address, ..
				ArrayList<CrimeItem> value = hm.get(key); 
				ArrayList<Object> tempCrime = (ArrayList<Object>) c;
				for (int i = 0; i < tempCrime.size(); i++) {
					Map m = (Map) tempCrime.get(i);
					
//					Log.d("coba",m.keySet().toString());
					CrimeItem singleCrime = mapToCrimeItem(m);
					value.add(singleCrime);
				}
				
			}
		}
		return hm;
	}
	
	private CrimeItem mapToCrimeItem(Map<String, Object> m) {
		// convert Map(Crime Type, value) to CrimeItem
		CrimeItem result = new CrimeItem();
		List<String> mapKeys = new ArrayList(m.keySet());
		for (String key : mapKeys) {
			switch (key) {
				case "crimeCaseID":
					result.setCrimeCaseID((String) m.get(key));
					break;
				case "note":
					result.setNote((String) m.get(key));
					break;
				case "crimeAddress":
					result.setCrimeAddress((String) m.get(key));
					break;
				case "crimeReportDate":
					result.setCrimeReportDate((Double) m.get(key));
					break;
				case "latitude":
					result.setLatitude((Double) m.get(key));
					break;
				case "timeZone":
					result.setTimeZone((String) m.get(key));
					break;
				case "crimeDate":
					result.setCrimeDate((Double) m.get(key));
					break;
				case "crimeType":
					result.setCrimeType((String) m.get(key));
					break;
				case "longitude":
					result.setLongitude((Double) m.get(key));
					break;
			}
		}
		//crimeCaseID, note, crimeAddress, crimeReportDate, latitude, timeZone, crimeDate, crimeType, longitude]
		return result;
	}

}
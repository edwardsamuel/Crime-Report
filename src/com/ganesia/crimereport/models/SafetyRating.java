package com.ganesia.crimereport.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	}

	/**
	 * 
	 * @return The safetyRating
	 */
	public String getSafetyRating() {
		return safetyRating;
	}

	/**
	 * 
	 * @param safetyRating
	 *            The safetyRating
	 */
	public void setSafetyRating(String safetyRating) {
		this.safetyRating = safetyRating;
	}

	/**
	 * 
	 * @return The safetyRatingDescription
	 */
	public String getSafetyRatingDescription() {
		return safetyRatingDescription;
	}

	/**
	 * 
	 * @param safetyRatingDescription
	 *            The safetyRatingDescription
	 */
	public void setSafetyRatingDescription(String safetyRatingDescription) {
		this.safetyRatingDescription = safetyRatingDescription;
	}

	/**
	 * 
	 * @return The crimeReportDate
	 */
	public String getCrimeReportDate() {
		return crimeReportDate;
	}

	/**
	 * 
	 * @param crimeReportDate
	 *            The crimeReportDate
	 */
	public void setCrimeReportDate(String crimeReportDate) {
		this.crimeReportDate = crimeReportDate;
	}

	/**
	 * 
	 * @return The latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * 
	 * @param latitude
	 *            The latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * 
	 * @return The crimeStartDate
	 */
	public String getCrimeStartDate() {
		return crimeStartDate;
	}

	/**
	 * 
	 * @param crimeStartDate
	 *            The crimeStartDate
	 */
	public void setCrimeStartDate(String crimeStartDate) {
		this.crimeStartDate = crimeStartDate;
	}

	/**
	 * 
	 * @return The radius
	 */
	public int getRadius() {
		return radius;
	}

	/**
	 * 
	 * @param radius
	 *            The radius
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}

	/**
	 * 
	 * @return The crimeList
	 */
	public List<Object> getCrimeList() {
		return crimeList;
	}

	/**
	 * 
	 * @param crimeList
	 *            The crimeList
	 */
	public void setCrimeList(List<Object> crimeList) {
		List<Object> tempCrimeList = crimeList;
		this.crimeList = tempCrimeList;
	}

	/**
	 * 
	 * @return The longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * 
	 * @param longitude
	 *            The longitude
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
		// K : Crime Type (Vehicle Theft, Narcotics, etc.)
		// V : List <CrimeItem>
		String key = "";
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
			} else {
				// Value
				// get the list from latest key,
				// since the order of API is always
				// String-List<Object>-String-List
				// List <ArrayList<Map<K,V>>
				// K : CrimeCaseID, Note, Address, ..
				ArrayList<CrimeItem> value = hm.get(key);
				ArrayList<Object> tempCrime = (ArrayList<Object>) c;
				for (int i = 0; i < tempCrime.size(); i++) {
					Map m = (Map) tempCrime.get(i);
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
				result.setCrimeDate(Math.round((Double) m.get(key)));
				break;
			case "crimeType":
				result.setCrimeType((String) m.get(key));
				break;
			case "longitude":
				result.setLongitude((Double) m.get(key));
				break;
			}
		}
		// crimeCaseID, note, crimeAddress, crimeReportDate, latitude, timeZone,
		// crimeDate, crimeType, longitude]
		return result;
	}

	/***
	 * Get trending crimes around user's timedate in nTimeInMilis (milisecond) 
	 * by filtering returned CrimeList from SafetyRating API
	 * 
	 * @param nTimeInMilis
	 * @return
	 */
	public HashMap<String, ArrayList<CrimeItem>> getTrendingCrimeAroundUserTimedate(long nTimeInMilis) {
		// Deep Copy the HashMap which is returned from method
		// getNearestCrimeList()
		HashMap<String, ArrayList<CrimeItem>> trendingCrime = new HashMap<String, ArrayList<CrimeItem>>(this.getNearestCrimeList());
		Set<String> keysColl = trendingCrime.keySet();
		// List of removed elements, will be removed at the end of loop of every
		// key
		ArrayList<CrimeItem> removedCrimeItems = new ArrayList<CrimeItem>();

		for (Iterator keyIterator = keysColl.iterator(); keyIterator.hasNext();) {
			String k = (String) keyIterator.next();
			ArrayList<CrimeItem> crimeList = trendingCrime.get(k);
			for (Iterator valueIterator = crimeList.iterator(); valueIterator
					.hasNext();) {
				CrimeItem tempCrimeItem = (CrimeItem) valueIterator.next();
				// CrimeItem is in the valid time range
				// keep it on CrimeList
				if (!isTimeDifferenceNHours(nTimeInMilis,
						tempCrimeItem.getCrimeDate())) {
					// CrimeItem is not in the valid time range
					// remove the data which is not in the time range
					removedCrimeItems.add(tempCrimeItem);
				}
			}
			crimeList.removeAll(removedCrimeItems);
		}
		return trendingCrime;
	}

	private Boolean isTimeDifferenceNHours(long timeRangeInMilis,
			long targetTimeInMilis) {
		// hours difference in milisecond measurement
		// currentTime : user's timedate in MM:SS
		// targetTime : CrimeItem's crimeDate in MM:SS
		int f = 0;
		int t = 0;
		long result = 0;
		Calendar now = Calendar.getInstance();
		Calendar targetCal = Calendar.getInstance();
		targetCal.setTimeInMillis(targetTimeInMilis);
		targetCal.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
		targetCal.set(Calendar.MONTH, now.get(Calendar.MONTH));
		targetCal.set(Calendar.YEAR, now.get(Calendar.YEAR));

		long timeDelta = (now.getTimeInMillis() - targetCal.getTimeInMillis());
		// time difference is always in INTEGER range, so it's safe to cast long
		// to int directly
		int deltaSecond = Math.abs((int) timeDelta / 1000);
		// maxSecondRange is the maximum time difference between currentTime and
		// targetTime
		// e.g. we want to look at crime that happen around 2 hours of user's
		// timedate
		// if user's timedate is 00:00, we need to look at crime that happen at
		// 22:00 - 00:00 and 00:00 - 02:00
		int maxSecondRange = 24 * 3600 - ((int) timeRangeInMilis / 1000);
		if (deltaSecond > maxSecondRange) {
			deltaSecond = deltaSecond - maxSecondRange;
		}
		if (deltaSecond > timeRangeInMilis / 1000) {
			// remove the data which is not in the time range
			// the removal is located in getTrendingCrimeAroundUserTime
			return false;
		} else {
			return true;
		}
	}
}
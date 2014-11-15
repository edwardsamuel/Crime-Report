package com.ganesia.crimereport;

import com.google.android.gms.maps.model.LatLng;

/***
 * Application constants
 * 
 * @author Edward Samuel
 *
 */
public abstract class Constants {
	
	public static final String CITY = "Chicago";
	public static final LatLng CENTER_LATLNG = new LatLng(41.8337329, -87.7321555);
	public static double NW_LATITUDE = 42.02313500000000;
	public static double NW_LONGITUDE = -87.94010100000000;
	public static double SE_LATITUDE = 41.64428600000000;
	public static double SE_LONGITUDE = -87.52366100000000;
	public static final String CRIME_API_ENDPOINT = "http://crimedb.watchovermeapp.com:8080/crimereport/rs/data";
	
}

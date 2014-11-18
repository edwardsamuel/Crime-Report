package com.ganesia.crimereport;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/***
 * Application constants
 * 
 * @author Edward Samuel
 *
 */
public abstract class Constants {
	
	public static final String CITY = "Chicago";
	public static double N_LATITUDE = 42.02313500000000;
	public static double W_LONGITUDE = -87.94010100000000;
	public static double S_LATITUDE = 41.64428600000000;
	public static double E_LONGITUDE = -87.52366100000000;
	public static final LatLng CENTER_LATLNG = new LatLng(41.8337329, -87.7321555);
	public static final LatLng NE_LATLNG = new LatLng(N_LATITUDE, E_LONGITUDE);
	public static final LatLng SW_LATLNG = new LatLng(S_LATITUDE, W_LONGITUDE);
	public static final LatLngBounds LATLNG_BOUNDS = new LatLngBounds.Builder().include(NE_LATLNG).include(SW_LATLNG).build();
	public static final String CRIME_API_ENDPOINT = "http://crimedb.watchovermeapp.com:8080/crimereport/rs/data";
	
}

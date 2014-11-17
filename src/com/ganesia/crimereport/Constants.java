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
	
	//services
	// Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    private static final int UPDATE_INTERVAL_IN_SECONDS = 30;
    // Update frequency in milliseconds
    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 30;
    // A fast frequency ceiling in milliseconds
    public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    
}

package com.ganesia.crimereport.webservices;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

import com.ganesia.crimereport.models.SafetyRating;

public interface SafetyRatingInterface {
	@GET("/safetyrating")
	//<CrimeItem> groupList
	void getSafetyRating(@Query("city") String city, @Query("lat") double lat, @Query("lng") double lng, @Query("query") String query, Callback<SafetyRating> callback);
}

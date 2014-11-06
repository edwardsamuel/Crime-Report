package com.ganesia.crimereport.adapters;

import com.ganesia.crimereport.models.SafetyRating;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface SafetyRatingInterface {
	@GET("/safetyrating")
	//<CrimeItem> groupList
	void getSafetyRating(@Query("city") String city, @Query("lat") Double lat, @Query("lng") Double lng, Callback<SafetyRating> callback);
}

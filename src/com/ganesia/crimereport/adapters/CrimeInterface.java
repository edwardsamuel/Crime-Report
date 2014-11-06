package com.ganesia.crimereport.adapters;

import java.util.List;

import com.ganesia.crimereport.models.Crime;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface CrimeInterface {
	@GET("/query")
	//<CrimeItem> groupList
	void getCrimeList(@Query("city") String city, @Query("startDate") String date, Callback<Crime> callback);
}

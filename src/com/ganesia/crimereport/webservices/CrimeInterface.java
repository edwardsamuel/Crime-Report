package com.ganesia.crimereport.webservices;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

import com.ganesia.crimereport.models.CrimeQueryResult;

public interface CrimeInterface {
	@GET("/query")
	//<CrimeItem> groupList
	void getCrimeList(@Query("city") String city, @Query("startDate") String date, Callback<CrimeQueryResult> callback);
}

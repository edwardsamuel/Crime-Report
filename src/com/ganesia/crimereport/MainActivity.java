package com.ganesia.crimereport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.ganesia.crimereport.R;
import com.ganesia.crimereport.adapters.CrimeInterface;
import com.ganesia.crimereport.adapters.SafetyRatingInterface;
import com.ganesia.crimereport.models.Crime;
import com.ganesia.crimereport.models.CrimeItem;
import com.ganesia.crimereport.models.SafetyRating;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	Crime CrimeData;
	RestAdapter restAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView textViewAPI1 = (TextView) findViewById(R.id.API1);
		textViewAPI1.setText("start fetching - API1");
		TextView textViewAPI2 = (TextView) findViewById(R.id.API2);
		textViewAPI2.setText("start fetching - API2");
		
		// Setup the adapter
		// Use Adapter from Retrofit Library
		restAdapter = new RestAdapter.Builder()
		.setEndpoint("http://crimedb.watchovermeapp.com:8080/crimereport/rs/data")
		.build();
		
		consumeReportAPI("2014-10-27");
		consumeSafetyRatingAPI(41.883522, -87.627788);
	}
	
	protected void consumeReportAPI(String date) {
		// Consume ReportAPI
		CrimeInterface service = restAdapter.create(CrimeInterface.class);
		service.getCrimeList("Chicago",date, new Callback<Crime>() {
	
			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				TextView text = (TextView) findViewById(R.id.API1);
				text.setText(arg0.toString());
				CrimeData = null;
			}
	
			@Override
			public void success(Crime arg0, Response arg1) {
				// TODO Auto-generated method stub
				TextView text = (TextView) findViewById(R.id.API1);
				text.setText("sukses");
				CrimeData = new Crime(arg0);
				
				// pick the biggest three
				Gson gson = new Gson();
				String json = gson.toJson(CrimeData.topThreeCrime(), LinkedHashMap.class);
				text.setText(json);
			}
			
		});
	}

	protected void consumeSafetyRatingAPI(Double lat, Double lng) {
		// Consume SafetyRatingAPI
		SafetyRatingInterface service = restAdapter.create(SafetyRatingInterface.class);
		service.getSafetyRating("Chicago", lat, lng, new Callback<SafetyRating>() {

			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				TextView text = (TextView) findViewById(R.id.API2);
				text.setText(arg0.toString());
			}

			@Override
			public void success(SafetyRating arg0, Response arg1) {
				// TODO Auto-generated method stub
				TextView text = (TextView) findViewById(R.id.API2);
				String result="";
				// text.setText(arg0.getNearestCrimeList().values().toString());
				Collection<ArrayList<CrimeItem>> temp = arg0.getNearestCrimeList().values();
				for (Iterator iterator = temp.iterator(); iterator.hasNext();) {
					Log.d("coba","iterator");
					ArrayList<CrimeItem> crimeList = (ArrayList<CrimeItem>) iterator.next();
					for (CrimeItem e : crimeList) {
						Log.d("coba","crimelist");
						result += "\n" + e.getCrimeType();
					}
				}
				text.setText(result);
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

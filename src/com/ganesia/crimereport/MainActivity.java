package com.ganesia.crimereport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

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
		
//		consumeReportAPI("2014-10-27");
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
				HashMap<String, ArrayList<CrimeItem>> nearestCrimeList = arg0.getNearestCrimeList();
				Set<String> keys = nearestCrimeList.keySet();
				result += "\n" + "Crimes around your location:";
				for (Iterator keyIterator = keys.iterator(); keyIterator.hasNext();) {
					// iterate to get values from every key
					String key = (String) keyIterator.next();
					ArrayList<CrimeItem> crimeList = nearestCrimeList.get(key);
					result += "\n" + key + " (" + crimeList.size() + ")";
				}
				// filter
				result += "\n ----" + Calendar.getInstance().getTime().toString();
				result += "\n" + "Trending crimes around this time " + "(" + String.valueOf(Calendar.getInstance().getTimeInMillis()) + "):";
				HashMap<String, ArrayList<CrimeItem>> trendingCrimeList = arg0.getTrendingCrimeAroundUserTimedate(7200*1000);
				Set<String> keys2 = trendingCrimeList.keySet();
				result += "\n" + "Crimes around your location:";
				for (Iterator keyIterator = keys2.iterator(); keyIterator.hasNext();) {
					// iterate to get values from every key
					String key2 = (String) keyIterator.next();
					ArrayList<CrimeItem> crimeList = nearestCrimeList.get(key2);
					result += "\n" + key2 + " (" + trendingCrimeList.size() + ")";
				}
				
//				for (Iterator iterator = temp.iterator(); iterator.hasNext();) {
//					ArrayList<CrimeItem> crimeList = (ArrayList<CrimeItem>) iterator.next();
//					
////					for (CrimeItem e : crimeList) {
////						result += "\n" + e.getCrimeType() + "--" + e.getType() + "--"  + e.isArrest();
////					}
//				}
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

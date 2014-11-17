package com.ganesia.crimereport.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SearchView.OnSuggestionListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ganesia.crimereport.Constants;
import com.ganesia.crimereport.R;
import com.ganesia.crimereport.adapters.PlaceSuggestionsCursorAdapter;
import com.ganesia.crimereport.fragments.SafetyRatingFragment;
import com.ganesia.crimereport.fragments.TopCrimeFragment;
import com.ganesia.crimereport.models.CrimeItem;
import com.ganesia.crimereport.models.CrimeQueryResult;
import com.ganesia.crimereport.models.SafetyRating;
import com.ganesia.crimereport.models.Tuple;
import com.ganesia.crimereport.webservices.CrimeInterface;
import com.ganesia.crimereport.webservices.SafetyRatingInterface;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

public class MainActivity extends FragmentActivity {

	private static final int REPORT_DAYS = -14;
	private static final int ALT_HEATMAP_RADIUS = 20;

	private static final int STATE_HOME = 1;
	private static final int STATE_SEARCH = 2;

	private static final String MY_PREFS_NAME = "prefs_timestamp";
	private static final String CACHE_CRIME_REPORT = "crimereport.json";
	private static final String CACHE_HEATMAP = "heatmapdata.json";
	
	private static final long CACHE_EXPIRATION_INTERVAL = 3600000;
	
	private RestAdapter mRestAdapter;
	private CrimeQueryResult mCrimeQueryResult;
	
	private GoogleMap mMap;
	private TileOverlay mOverlay;
	private HeatmapTileProvider mProvider;
	private SearchView mSearchView;
	private MenuItem mSearchMenuItem;
	
	private HashMap<Marker, CrimeItem> mCrimeMarkers;
	
	private int mState;

	private ArrayList<LatLng> mHeatmapData;
	private ArrayList<Tuple> mTopThreeCrime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set state
		mState = STATE_HOME;

		// Set the User Interface
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.activity_main);
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map)).getMap();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.CENTER_LATLNG, 10));
		mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
			
			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}
			
			@Override
			public View getInfoContents(Marker marker) {
				View v = null;
				CrimeItem crime = mCrimeMarkers.get(marker);
				
				if (crime != null) {
					v  = getLayoutInflater().inflate(R.layout.infowindow_crime, null);
					
					TextView markerTitle = (TextView) v.findViewById(R.id.tv_crime_title);
					TextView markerCaseID = (TextView) v.findViewById(R.id.tv_crime_id);
					TextView markerDate = (TextView) v.findViewById(R.id.tv_crime_date);
			        TextView markerAddress = (TextView) v.findViewById(R.id.tv_crime_address);
			        TextView markerNote = (TextView) v.findViewById(R.id.tv_crime_note);
			        
			        markerTitle.setText(crime.getCrimeType() + " - " + crime.getType());
			        markerCaseID.setText(crime.getCrimeCaseID());
			        markerDate.setText(new Date(crime.getCrimeDate()).toString());
			        markerAddress.setText(crime.getCrimeAddress());
			        markerNote.setText(crime.getNote());
				}
				
				return v;
			}
		});

		// Setup the adapter
		// Use Adapter from Retrofit Library
		mRestAdapter = new RestAdapter.Builder()
				.setEndpoint(Constants.CRIME_API_ENDPOINT)
				.build();
		
		// Init map markers
		mCrimeMarkers = new HashMap<Marker, CrimeItem>();

		// Read from 1-hour-old (in milis = 3600000) cache
		readCache(CACHE_EXPIRATION_INTERVAL);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		// Associate searchable configuration with the SearchView
		mSearchMenuItem = menu.findItem(R.id.action_search);
		mSearchView = (SearchView) mSearchMenuItem.getActionView();
		mSearchView.setSuggestionsAdapter(new PlaceSuggestionsCursorAdapter(this));
		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				if (mSearchMenuItem != null) {
					mSearchMenuItem.collapseActionView();
				}
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		mSearchView.setOnSuggestionListener(new OnSuggestionListener() {
			
			@Override
			public boolean onSuggestionSelect(int position) {
				return false;
			}
			
			@Override
			public boolean onSuggestionClick(int position) {
				CursorAdapter adapter = mSearchView.getSuggestionsAdapter();
				Cursor c = (Cursor) adapter.getItem(position);
				
				if (c != null) {
					String name = c.getString(2);
					double latitude = c.getDouble(4); // Latitude (in 4th column)
					double longitude = c.getDouble(5); // Longitude (in 5th column)
					
					safetyRating(name, latitude, longitude);
				}

				return true;
			}
		});

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_search) {
			onSearchRequested();
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 	Create a cache consists of top three crime and heatmap position
	 * 	The cache is saved on internal storage
	 * 	The timestamp (in milis) of cache creation is saved on Shared Preference with Key:time
	 */
	private void createCache() {
		Calendar c;
		File fTopThreeCrime;			// cache file of Top Three Crime (TopCrime)
		File fHeatmapPosition;			// cache file of Heatmap Position (ArrayList<LatLng>)
		String jTopThreeCrime; 			// json String of Top Three Crime (Top Crime)
		String jHeatmapPosition;		// json String of Heatmap Position (ArrayList<LatLng>)
		FileOutputStream outputStream1;
		FileOutputStream outputStream2;
		SharedPreferences timeStamp;	// record cache's timestamp
		// get current date
		c = Calendar.getInstance();

		// create the file
		fTopThreeCrime = new File(getApplicationContext().getCacheDir(), CACHE_CRIME_REPORT);
		fHeatmapPosition = new File(getApplicationContext().getCacheDir(), CACHE_HEATMAP);
		
		// prepare the JSON
		Gson gson = new Gson();
		jTopThreeCrime = gson.toJson(mTopThreeCrime);
		jHeatmapPosition = gson.toJson(mHeatmapData);
		
		// write to the file
		try {
			outputStream1 = openFileOutput(CACHE_CRIME_REPORT, getApplicationContext().MODE_PRIVATE);
			outputStream1.write(jTopThreeCrime.getBytes());
			outputStream1.close();

			outputStream2 = openFileOutput(CACHE_HEATMAP, getApplicationContext().MODE_PRIVATE);
			outputStream2.write(jHeatmapPosition.getBytes());
			outputStream2.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// record cache's timestamp
		Activity a = MainActivity.this;
		timeStamp = a.getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = timeStamp.edit();
		editor.putLong("CACHE_TIMESTAMP", c.getTimeInMillis());
		editor.commit();
	}
	
	private void readCache(long expiryIntervalInMilis) {
		// read a cache and load it into application as long as cache's timestamp below expiryInterval
		SharedPreferences timeStamp;
		long cacheTimeInMilis;
		long currentTimeInMilis;
		String jTopThreeCrime	=""; 		// json String of Top Three Crime (Top Crime)
		String jHeatmapPosition	="";		// json String of Heatmap Position (ArrayList<LatLng>)
		// get current time
		Calendar c = Calendar.getInstance();
		currentTimeInMilis = c.getTimeInMillis();
		
		Activity a = MainActivity.this;
		timeStamp = a.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
		// default time is 0 if the cache is not present
		cacheTimeInMilis = timeStamp.getLong("CACHE_TIMESTAMP", 0);
		if (cacheTimeInMilis == 0) {
			// cache is failed to load or has not been created yet
			queryResult();
		}
		else if ((currentTimeInMilis - cacheTimeInMilis) < expiryIntervalInMilis) {
			// read cache
			// read Top Three Crime file
			try {
				FileInputStream fis = openFileInput(CACHE_CRIME_REPORT);
				InputStreamReader inputStreamReader = new InputStreamReader(fis);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					sb.append(line);
				}
				jTopThreeCrime = sb.toString();
				inputStreamReader.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// read Heatmap Data file
			try {
				FileInputStream fis2 = openFileInput(CACHE_HEATMAP);
				InputStreamReader inputStreamReader2 = new InputStreamReader(fis2);
				BufferedReader bufferedReader2 = new BufferedReader(inputStreamReader2);
				StringBuilder sb2 = new StringBuilder();
				String line2;
				while ((line2 = bufferedReader2.readLine()) != null) {
					sb2.append(line2);
				}
				jHeatmapPosition = sb2.toString();
				inputStreamReader2.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			loadCache(jTopThreeCrime, jHeatmapPosition);
		}
		else {
			// cache has expired
			// recall the query
			queryResult();
		}
	}
	
	private void loadCache (String jsonTopThreeCrime, String jsonHeatmapPosition) {
		// load cache from json String to Object
		Gson gson = new Gson();
		mHeatmapData = gson.fromJson(jsonHeatmapPosition, new TypeToken<ArrayList<LatLng>>(){}.getType());
		drawCrimesHeatmap(mHeatmapData);
		mTopThreeCrime = gson.fromJson(jsonTopThreeCrime, new TypeToken<ArrayList<Tuple>>(){}.getType());
		if (mState == STATE_HOME) {
			TopCrimeFragment topCrimeFrag = (TopCrimeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
			topCrimeFrag.updateTopCrime(mTopThreeCrime);
		}
	}
	
	private void consumeReportAPI(Date startDate) {
		setProgressBarVisibility(true);
		setProgressBarIndeterminate(true);
		CrimeInterface service = mRestAdapter.create(CrimeInterface.class);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		service.getCrimeList(Constants.CITY, sdf.format(startDate), new Callback<CrimeQueryResult>() {

			@Override
			public void failure(RetrofitError arg0) {
				setProgressBarIndeterminate(false);
				setProgressBarVisibility(false);
				
				Toast.makeText(getApplicationContext(), arg0.toString(), Toast.LENGTH_LONG).show();
				mCrimeQueryResult = null;
			}

			@Override
			public void success(CrimeQueryResult queryResult, Response response) {
				setProgressBarIndeterminate(false);
				setProgressBarVisibility(false);
				
				mCrimeQueryResult = new CrimeQueryResult(queryResult);
				mHeatmapData = mCrimeQueryResult.getLatLngData();
				drawCrimesHeatmap(mHeatmapData);
				
				// pick the biggest three
				mTopThreeCrime = queryResult.getTopThreeCrime();

				if (mState == STATE_HOME) {
					TopCrimeFragment topCrimeFrag = (TopCrimeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
					topCrimeFrag.updateTopCrime(mTopThreeCrime);
				}
				createCache();
			}
		});
	}

	private void consumeSafetyRatingAPI(double lat, double lng) {
		SafetyRatingInterface service = mRestAdapter.create(SafetyRatingInterface.class);
		service.getSafetyRating(Constants.CITY, lat, lng, new Callback<SafetyRating>() {
			
			@Override
			public void failure(RetrofitError error) {
				Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
			}

			@Override
			public void success(SafetyRating rating, Response response) {
				int count = 0;
				LatLngBounds.Builder bounds = LatLngBounds.builder();
				Collection<ArrayList<CrimeItem>> c = rating.getNearestCrimeList().values();
				
				cleanCrimeMarkers();
				for (ArrayList<CrimeItem> list : c) {
					for (CrimeItem item : list) {
						Marker marker = addCrimeMarker(item);
						bounds.include(marker.getPosition());
						count++;
					}
				}
				
				if (count > 0) {
					mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100));
				}
			}
		});

	}
	
	/***
	 * Create new crime marker and add to maps
	 * @param crime
	 * @return The created marker
	 */
	private Marker addCrimeMarker(CrimeItem crime) {
		LatLng location = new LatLng(crime.getLatitude(), crime.getLongitude());
		
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(location);
		markerOptions.icon(getCrimeIcon(crime));
		
		Marker marker = mMap.addMarker(markerOptions);
		mCrimeMarkers.put(marker, crime);
		
		return marker;
	}
	
	/***
	 * Remove all markers from map.
	 */
	private void cleanCrimeMarkers() {
		for (Marker marker :  mCrimeMarkers.keySet()) {
			marker.remove();
		}
		mCrimeMarkers.clear();
	}
	
	/***
	 * Get crime icon based on crime type
	 * 
	 * @param crime
	 * @return
	 */
	private BitmapDescriptor getCrimeIcon(CrimeItem crime) {
		switch (crime.getCrimeType()) {
		case "HOMICIDE":
			return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);	
		case "THEFT":
			return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);	
		case "BATTERY":
			return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);	
		case "ROBBERY":
			return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);	
		case "NARCOTICS":
			return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);	
		case "MOTOR VEHICLE THEFT":
			return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);	
		case "ASSAULT":
			return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);	
		case "CRIM SEXUAL ASSAULT":
		case "PROSTITUTION":
			return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);	
		case "PUBLIC PEACE VIOLATION":
		case "OFFENSE INVOLVING CHILDREN":
			return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);		
		default:
			return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
		}		
	}

	
	/**
	 * Generate heatmap on Google Maps
	 * 
	 * @param latLngData
	 */
	private void drawCrimesHeatmap(ArrayList<LatLng> latLngData) {
		// Check if need to instantiate (avoid setData etc twice)
		if (mProvider == null) {
			mProvider = new HeatmapTileProvider.Builder().data(latLngData).build();
			mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
			mProvider.setRadius(ALT_HEATMAP_RADIUS);
		} else {
			mProvider.setData(latLngData);
			mProvider.setRadius(ALT_HEATMAP_RADIUS);
			mOverlay.clearTileCache();
		}
	}
	
	private void queryResult() {
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.DATE, REPORT_DAYS);
		consumeReportAPI(calendar.getTime());
	}
	
	private void safetyRating(String name, double lat, double lng) {
		LatLng location = new LatLng(lat, lng);
		CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(location);
		mMap.animateCamera(cameraPosition);
		
		consumeSafetyRatingAPI(lat, lng);
		
		changeToSafetyReportFragment();
		mSearchMenuItem.collapseActionView();
		mState = STATE_SEARCH;

		Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
	}
	
	private void changeToSafetyReportFragment() {
		Fragment safetyRatingFrag = new SafetyRatingFragment();			
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_main view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.fragment_main, safetyRatingFrag);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}
}

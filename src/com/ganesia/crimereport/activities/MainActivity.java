package com.ganesia.crimereport.activities;

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
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import com.ganesia.crimereport.models.TopCrime;
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
import com.google.maps.android.heatmaps.HeatmapTileProvider;

public class MainActivity extends FragmentActivity {

	private static final int REPORT_DAYS = -14;
	private static final int ALT_HEATMAP_RADIUS = 20;
	private static final int STATE_HOME = 1;
	private static final int STATE_SEARCH = 2;

	private RestAdapter mRestAdapter;
	private CrimeQueryResult mCrimeQueryResult;
	
	private GoogleMap mMap;
	private TileOverlay mOverlay;
	private HeatmapTileProvider mProvider;
	private SearchView mSearchView;
	private MenuItem mSearchMenuItem;
	
	private HashMap<Marker, CrimeItem> mCrimeMarkers;
	
	private int mState;

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

		// Display crime in a city 
		queryResult();
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
				drawCrimesHeatmap(mCrimeQueryResult);
				
				// pick the biggest three
				ArrayList<TopCrime> topCrimes = new ArrayList<TopCrime>();
				topCrimes = queryResult.getTopThreeCrime();

				if (mState == STATE_HOME) {
					TopCrimeFragment topCrimeFrag = (TopCrimeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
					topCrimeFrag.updateTopCrime(topCrimes);
				}
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
						Log.d("SR_API", item.toString());
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

	private void drawCrimesHeatmap(CrimeQueryResult queryResult) {
		// Generate heatmap on Google Maps
		// Check if need to instantiate (avoid setData etc twice)
		if (mProvider == null) {
			mProvider = new HeatmapTileProvider.Builder().data(queryResult.getLatLngData()).build();
			mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
			mProvider.setRadius(ALT_HEATMAP_RADIUS);
		} else {
			mProvider.setData(queryResult.getLatLngData());
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
		Fragment newFragment = new SafetyRatingFragment();			
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_main view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.fragment_main, newFragment);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}
}

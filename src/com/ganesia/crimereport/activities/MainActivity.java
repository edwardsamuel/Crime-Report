package com.ganesia.crimereport.activities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SearchView.OnSuggestionListener;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

public class MainActivity extends FragmentActivity {

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
	
	private int mState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set state
		mState = STATE_HOME;

		// Set the User Interface
		setContentView(R.layout.activity_main);
		mMap = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.fragment_map)).getMap();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
				Constants.CENTER_LATLNG, 10));

		// Setup the adapter
		// Use Adapter from Retrofit Library
		mRestAdapter = new RestAdapter.Builder().setEndpoint(
				Constants.CRIME_API_ENDPOINT)
				.build();

		// Fetch data from API
		consumeReportAPI("2014-10-21");

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
			doSearch(intent.getStringExtra(SearchManager.QUERY));
		} else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
			getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
		}
	}

	private void doSearch(String query) {
		Bundle data = new Bundle();
		data.putString("query", query);
		// getSupportLoaderManager().restartLoader(0, data, this);
	}

	private void getPlace(String query) {
		Bundle data = new Bundle();
		data.putString("query", query);
		// getSupportLoaderManager().restartLoader(1, data, this);
	}

	private void consumeReportAPI(String date) {
		CrimeInterface service = mRestAdapter.create(CrimeInterface.class);
		service.getCrimeList(Constants.CITY, date, new Callback<CrimeQueryResult>() {

			@Override
			public void failure(RetrofitError arg0) {
				Toast.makeText(getApplicationContext(), arg0.toString(), Toast.LENGTH_LONG).show();
				mCrimeQueryResult = null;
			}

			@Override
			public void success(CrimeQueryResult queryResult, Response response) {
				mCrimeQueryResult = new CrimeQueryResult(queryResult);
				setHeatmap(mCrimeQueryResult);
				
				// pick the biggest three
				ArrayList<TopCrime> topCrimes = new ArrayList<TopCrime>();
				topCrimes = queryResult.getTopThreeCrime();

				if (mState == STATE_HOME) {
					TopCrimeFragment topCrimeFrag = (TopCrimeFragment) getFragmentManager().findFragmentById(R.id.fragment_main);
					topCrimeFrag.updateTopCrime(topCrimes);
				}
			}
		});
	}

	protected void consumeSafetyRatingAPI(double lat, double lng) {
		SafetyRatingInterface service = mRestAdapter.create(SafetyRatingInterface.class);
		service.getSafetyRating(Constants.CITY, lat, lng, new Callback<SafetyRating>() {
			
			@Override
			public void failure(RetrofitError arg0) {
				Toast.makeText(getApplicationContext(), arg0.toString(), Toast.LENGTH_LONG).show();
			}

			@Override
			public void success(SafetyRating arg0, Response arg1) {
				// TODO Auto-generated method stub
				// TextView text = (TextView) findViewById(R.id.API2);
				String result = "";
				// text.setText(arg0.getNearestCrimeList().values().toString());
				Collection<ArrayList<CrimeItem>> temp = arg0
						.getNearestCrimeList().values();
				for (Iterator iterator = temp.iterator(); iterator
						.hasNext();) {
					Log.d("coba", "iterator");
					ArrayList<CrimeItem> crimeList = (ArrayList<CrimeItem>) iterator
							.next();
					for (CrimeItem e : crimeList) {
						Log.d("coba", "crimelist");
						result += "\n" + e.getCrimeType();
					}
				}
				// text.setText(result);
			}
		});

	}

	private void setHeatmap(CrimeQueryResult c) {
		// Generate heatmap on Google Maps
		// Check if need to instantiate (avoid setData etc twice)
		if (mProvider == null) {
			mProvider = new HeatmapTileProvider.Builder().data(c.getLatLngData()).build();
			mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
			mProvider.setRadius(ALT_HEATMAP_RADIUS);
		} else {
			mProvider.setData(c.getLatLngData());
			mProvider.setRadius(ALT_HEATMAP_RADIUS);
			mOverlay.clearTileCache();
		}
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
				
				MarkerOptions markerOptions = null;
				LatLng geoloc = null;

				mMap.clear();
				if (c != null) {
					String name = c.getString(1);
					double latitude = c.getDouble(4); // Latitude (in 4th column)
					double longitude = c.getDouble(5); // Longitude (in 5th column)
					geoloc = new LatLng(latitude, longitude);

					markerOptions = new MarkerOptions();
					markerOptions.position(geoloc);
					markerOptions.title(name);
					mMap.addMarker(markerOptions);
				}

				if (geoloc != null) {
					CameraUpdate cameraPosition = CameraUpdateFactory
							.newLatLng(geoloc);
					mMap.animateCamera(cameraPosition);
				}
				
				// Create fragment and give it an argument specifying the article it should show
				Fragment newFragment = new SafetyRatingFragment();			
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

				// Replace whatever is in the fragment_container view with this fragment,
				// and add the transaction to the back stack so the user can navigate back
				transaction.replace(R.id.fragment_main, newFragment);
				transaction.addToBackStack(null);

				// Commit the transaction
				transaction.commit();

				if (mSearchMenuItem != null) {
					mSearchMenuItem.collapseActionView();
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
}

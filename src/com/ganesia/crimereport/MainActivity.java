package com.ganesia.crimereport;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.Callback;
import retrofit.client.Response;

import com.ganesia.crimereport.R;
import com.ganesia.crimereport.adapters.CrimeInterface;
import com.ganesia.crimereport.adapters.SafetyRatingInterface;
import com.ganesia.crimereport.models.Crime;
import com.ganesia.crimereport.models.CrimeItem;
import com.ganesia.crimereport.models.SafetyRating;
import com.ganesia.crimereport.providers.PlaceProvider;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {
	
	private SlidingUpPanelLayout mLayout;
	private GoogleMap mMap;
	private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private RestAdapter restAdapter;
    private Crime CrimeData;

    private static final int ALT_HEATMAP_RADIUS = 20;
       
	private static final LatLng CHICAGO = new LatLng(41.8337329,-87.7321555);
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Setup the adapter
		// Use Adapter from Retrofit Library
		restAdapter = new RestAdapter.Builder()
				.setEndpoint("http://crimedb.watchovermeapp.com:8080/crimereport/rs/data")
				.build();
		
		// Fetch data from API
		consumeReportAPI("2014-10-21");
		consumeSafetyRatingAPI(41.8337329, -87.7321555);
		
		// Set the User Interface
		setContentView(R.layout.activity_main);
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map)).getMap();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CHICAGO, 10));

		TopCrimeFragment topCrimeFrag = new TopCrimeFragment();
		
		// add markers
		mMap.addMarker(new MarkerOptions()
			    .position(new LatLng(-25, 143))
			    .title("Hello world").snippet("aaaaaaaaaaaaaa"));
				
		// Setting a custom info window adapter for the google map
		mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
 
            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }
 
            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {
 
                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.info_window, null);
 
                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();
 
                // Getting reference to the TextView to set crime type
                TextView crimeType = (TextView) v.findViewById(R.id.info_crime_type);
 
                // Getting reference to the TextView to set crime note
                TextView crimeNote = (TextView) v.findViewById(R.id.info_crime_note);
                
                // Getting reference to the TextView to set crime date
                TextView crimeDate = (TextView) v.findViewById(R.id.info_crime_date);
 
                // Setting the crime type
                crimeType.setText("Robbery");
 
                // Setting the crime note
                crimeNote.setText("ARMED: Handgun");

                // Setting the crime date
                crimeDate.setText("Sat, 18 Oct 2014 21:50:00 GMT");
                
                // Returning the view containing InfoWindow contents
                return v;
 
            }
        });
		
		handleIntent(getIntent());
	}	
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}
	
	private void handleIntent(Intent intent) {
		Toast.makeText(this, "LOG", Toast.LENGTH_LONG).show();
		if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
			doSearch(intent.getStringExtra(SearchManager.QUERY));
		} else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
			getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
		}
	}
	
	private void doSearch(String query) {
		Bundle data = new Bundle();
		data.putString("query", query);
		getSupportLoaderManager().restartLoader(0, data, this);
	}

	private void getPlace(String query) {
		Bundle data = new Bundle();
		data.putString("query", query);
		getSupportLoaderManager().restartLoader(1, data, this);
	}
	
	// Datasets from http://data.gov.au
    private ArrayList<LatLng> readItems(int resource) throws JSONException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            list.add(new LatLng(lat, lng));
        }
        return list;
    }

    protected void consumeReportAPI(String date) {
		// Consume ReportAPI
		CrimeInterface service = restAdapter.create(CrimeInterface.class);
		service.getCrimeList("Chicago",date, new Callback<Crime>() {
	
			@Override
			public void failure(RetrofitError arg0) {
				Context context = getApplicationContext();
				Toast t = Toast.makeText(context, arg0.toString(), 10);
				CrimeData = null;
			}
	
			@Override
			public void success(Crime arg0, Response arg1) {
				CrimeData = new Crime(arg0);
				setHeatmap(CrimeData);
				// pick the biggest three
				Gson gson = new Gson();
//				String json = gson.toJson(CrimeData.topThreeCrime(), LinkedHashMap.class);
				//text.setText(json);
			}
			
		});
	}
    
    private void setHeatmap(Crime c) {
    	// Generate heatmap on Google Maps
		// Check if need to instantiate (avoid setData etc twice)
		if (mProvider == null) {
			mProvider = new HeatmapTileProvider.Builder().data(c.getLatLngData()).build();
		    mOverlay = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map)).getMap().addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            mProvider.setRadius(ALT_HEATMAP_RADIUS);
        } else {
            mProvider.setData(c.getLatLngData());
            mProvider.setRadius(ALT_HEATMAP_RADIUS);
            mOverlay.clearTileCache();
        }
    }
	
    protected void consumeSafetyRatingAPI(Double lat, Double lng) {
		// Consume SafetyRatingAPI
		SafetyRatingInterface service = restAdapter.create(SafetyRatingInterface.class);
		service.getSafetyRating("Chicago", lat, lng, new Callback<SafetyRating>() {

			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				//TextView text = (TextView) findViewById(R.id.API2);
				//text.setText(arg0.toString());
			}

			@Override
			public void success(SafetyRating arg0, Response arg1) {
				// TODO Auto-generated method stub
				//TextView text = (TextView) findViewById(R.id.API2);
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
				//text.setText(result);
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
		if (id == R.id.action_search) {
			onSearchRequested();
		}
		return super.onOptionsItemSelected(item);
	}
	

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle bundle) {
		CursorLoader cLoader = null;
		if (arg0 == 0) {
			cLoader = new CursorLoader(getBaseContext(),
					PlaceProvider.SEARCH_URI, null, null,
					new String[] { bundle.getString("query") }, null);
		} else if (arg0 == 1) {
			cLoader = new CursorLoader(getBaseContext(),
					PlaceProvider.DETAILS_URI, null, null,
					new String[] { bundle.getString("query") }, null);
		}
		return cLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// showLocations(c);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
	}

	private void showLocations(Cursor c) {
		MarkerOptions markerOptions = null;
		LatLng position = null;
		// mGoogleMap.clear();
		while (c.moveToNext()) {
			markerOptions = new MarkerOptions();
			position = new LatLng(Double.parseDouble(c.getString(1)), Double.parseDouble(c.getString(2)));
			markerOptions.position(position);
			markerOptions.title(c.getString(0));
			// mGoogleMap.addMarker(markerOptions);
		}
		if (position != null) {
			CameraUpdate cameraPosition = CameraUpdateFactory
					.newLatLng(position);
			// mGoogleMap.animateCamera(cameraPosition);
		}
	}
}

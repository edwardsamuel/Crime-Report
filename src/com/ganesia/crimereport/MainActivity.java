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
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.FragmentActivity;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends FragmentActivity {
	
	private SlidingUpPanelLayout mLayout;
	private GoogleMap mMap;
	private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private RestAdapter restAdapter;
    private Crime CrimeData;
    private boolean flip = false;
    
    private static final int ALT_HEATMAP_RADIUS = 20;
       
	private static final LatLng CHICAGO = new LatLng(41.8337329,-87.7321555);
	
	/**
     * Maps name of data set to data (list of LatLngs)
     * Also maps to the URL of the data set for attribution
     */
    private HashMap<String, DataSet> mLists = new HashMap<String, DataSet>();
    
    /**
     * Helper class - stores data sets and sources.
     */
    private class DataSet {
        private ArrayList<LatLng> mDataset;
        private String mUrl;

        public DataSet(ArrayList<LatLng> dataSet, String url) {
            this.mDataset = dataSet;
            this.mUrl = url;
        }

        public ArrayList<LatLng> getData() {
            return mDataset;
        }

        public String getUrl() {
            return mUrl;
        }
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map)).getMap();
		// mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CHICAGO, 10));
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-25, 143), 4)); // dummy: Melbourne
		
		try {
            mLists.put(getString(R.string.crime_locations), new DataSet(readItems(R.raw.crime_locations),
                    getString(R.string.crime_locations_url))); // please change the datasets of latlng in /res/raw/crime_locations.json
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show();
        }
		
		// Check if need to instantiate (avoid setData etc twice)
        if (mProvider == null) {
            mProvider = new HeatmapTileProvider.Builder().data(
                    mLists.get(getString(R.string.crime_locations)).getData()).build();
            mOverlay = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_map)).getMap().addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            mProvider.setRadius(ALT_HEATMAP_RADIUS);
        } else {
            mProvider.setData(mLists.get(getString(R.string.crime_locations)).getData());
            mProvider.setRadius(ALT_HEATMAP_RADIUS);
            mOverlay.clearTileCache();
        }

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
		
		// Setup the adapter
		// Use Adapter from Retrofit Library
		restAdapter = new RestAdapter.Builder()
				.setEndpoint("http://crimedb.watchovermeapp.com:8080/crimereport/rs/data")
				.build();
		
		consumeReportAPI("2014-10-27");
		consumeSafetyRatingAPI(41.883522, -87.627788);
		
		
		Button b = (Button) findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!flip) {
					TopCrimeFragment topCrimeFrag = new TopCrimeFragment();
					FragmentManager fm = getFragmentManager();
					FragmentTransaction transaction = fm.beginTransaction();
					transaction.replace(R.id.fragment_main, topCrimeFrag);
					transaction.addToBackStack(null);
					transaction.commit();
					Toast toast = Toast.makeText(getApplicationContext(), "true", Toast.LENGTH_LONG);
					toast.show();
					flip = true;
				} else {
					SafetyRatingFragment safetyRatingFrag = new SafetyRatingFragment();
					FragmentManager fm = getFragmentManager();
					FragmentTransaction transaction = fm.beginTransaction();
					transaction.replace(R.id.fragment_main, safetyRatingFrag);
					transaction.addToBackStack(null);
					transaction.commit();
					flip = false;
					
				}
			}
		});
		
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
				// TODO Auto-generated method stub
				//TextView text = (TextView) findViewById(R.id.API1);
				//text.setText(arg0.toString());
				CrimeData = null;
			}
	
			@Override
			public void success(Crime arg0, Response arg1) {
				// TODO Auto-generated method stub
				//TextView text = (TextView) findViewById(R.id.API1);
				//text.setText("sukses");
				CrimeData = new Crime(arg0);
				
				// pick the biggest three
				Gson gson = new Gson();
				String json = gson.toJson(CrimeData.topThreeCrime(), LinkedHashMap.class);
				//text.setText(json);
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
}

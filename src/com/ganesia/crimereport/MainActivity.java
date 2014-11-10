package com.ganesia.crimereport;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
				
		//consumeReportAPI();
		
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
    
//	protected void consumeReportAPI() {
//		// Consume ReportAPI
//		// Use Adapter from Retrofit Library
//		RestAdapter restAdapter = new RestAdapter.Builder()
//		.setEndpoint("http://crimedb.watchovermeapp.com:8080/crimereport/rs/data")
//		.build();
//	
//		CrimeInterface service = restAdapter.create(CrimeInterface.class);
//		service.getCrimeList("Chicago","2014-10-27", new Callback<Crime>() {
//	
//			@Override
//			public void failure(RetrofitError arg0) {
//				// TODO Auto-generated method stub
//				TextView text = (TextView) findViewById(R.id.content);
//				text.setText(arg0.toString());
//				CrimeData = null;
//			}
//	
//			@Override
//			public void success(Crime arg0, Response arg1) {
//				// TODO Auto-generated method stub
//				TextView text = (TextView) findViewById(R.id.content);
//				text.setText("sukses");
//				CrimeData = new Crime(arg0);
//				
//				// pick the biggest three
//				Gson gson = new Gson();
//				String json = gson.toJson(CrimeData.topThreeCrime(), LinkedHashMap.class);
//				text.setText(json);
//			}
//			
//		});
//	}

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

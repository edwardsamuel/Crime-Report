package com.ganesia.crimereport.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ganesia.crimereport.R;
import com.ganesia.crimereport.providers.PlaceProvider;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GeocodingActivity extends FragmentActivity implements
		LoaderCallbacks<Cursor> {

	public static final int MAX_RESULTS = 30; // Max number of results returned
	private static final String TAG = "GeocoderDemo";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_geocoding);
		handleIntent(getIntent());

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
			case R.id.action_search:	
				onSearchRequested();
				break;
		}	
		return super.onMenuItemSelected(featureId, item);
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

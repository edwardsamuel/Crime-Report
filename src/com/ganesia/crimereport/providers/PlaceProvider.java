package com.ganesia.crimereport.providers;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;

import com.ganesia.crimereport.Constants;
import com.ganesia.crimereport.R;
import com.google.gson.Gson;

/***
 * Content provider for reverse geocoding.
 * 
 * @author Edward Samuel
 * 
 */
public class PlaceProvider extends ContentProvider {

	public static final String AUTHORITY = "com.ganesia.crimereport.providers.PlaceProvider";

	public static final Uri SEARCH_URI = Uri.parse("content://" + AUTHORITY
			+ "/search");

	public static final Uri DETAILS_URI = Uri.parse("content://" + AUTHORITY
			+ "/details");

	private static final int SEARCH = 1;
	private static final int SUGGESTIONS = 2;
	private static final int DETAILS = 3;

	private static final int MAX_RESULTS = 10;

	// Defines a set of uris allowed with this content provider
	private static final UriMatcher mUriMatcher = buildUriMatcher();

	private static UriMatcher buildUriMatcher() {
		UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		// URI for "Go" button
		uriMatcher.addURI(AUTHORITY, "search", SEARCH);

		// URI for suggestions in Search Dialog
		uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY,
				SUGGESTIONS);

		// URI for Details
		uriMatcher.addURI(AUTHORITY, "details", DETAILS);

		return uriMatcher;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		MatrixCursor cursor = null;

		switch (mUriMatcher.match(uri)) {
		case SEARCH:
			// Defining a cursor object
			cursor = new MatrixCursor(new String[] {
					"name", "address", "latitude", "longitude"
			}, MAX_RESULTS);
			
			// Fill the cursor with suggestion
			try {
				String query = selectionArgs[0];
				List<Address> suggestions = getLocation(query);

				// Creating cursor object with places
				for (int i = 0, len = suggestions.size(); i < len; i++) {
					// Adding place details to cursor
					Address address = suggestions.get(i);
					cursor.addRow(new Object[] {
							address.getFeatureName(),
							getAddressString(address),
							address.getLatitude(),
							address.getLongitude()
							});
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;
		case SUGGESTIONS:
			// Defining a cursor object with columns id, SUGGEST_COLUMN_TEXT_1,
			// SearchManager.SUGGEST_COLUMN_TEXT_2, SearchManager.SUGGEST_COLUMN_ICON_1,
			// SUGGEST_COLUMN_INTENT_EXTRA_DATA
			cursor = new MatrixCursor(new String[] { "_id",
					SearchManager.SUGGEST_COLUMN_TEXT_1,
					SearchManager.SUGGEST_COLUMN_TEXT_2,
					SearchManager.SUGGEST_COLUMN_ICON_1,
					SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA }, MAX_RESULTS);

			try {
				String query = selectionArgs[0];
				List<Address> suggestions = getLocation(query);
				
				Gson gson = new Gson();

				// Creating cursor object with places
				for (int i = 0, len = suggestions.size(); i < len; i++) {
					// Adding place details to cursor
					Address address = suggestions.get(i);
					cursor.addRow(new Object[] {
							i,
							address.getFeatureName(),
							getAddressString(address),
							Integer.toString(R.drawable.ic_action_place),
							gson.toJson(address) });
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case DETAILS:
			// Defining a cursor object
			cursor = new MatrixCursor(new String[] {
					"name", "address", "latitude", "longitude"
			}, 1);
			
			// Fill the cursor with intent entra data
			Address address = new Gson().fromJson(selectionArgs[0], Address.class);
			cursor.addRow(new Object[] {
					address.getFeatureName(),
					getAddressString(address),
					address.getLatitude(),
					address.getLongitude()
					});
					
			break;
		}

		return cursor;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Runs the geocoder
	 * 
	 * @param string
	 *            Location query
	 * @return
	 * @throws IOException
	 */
	private List<Address> getLocation(String query) throws IOException {
		Geocoder gc = new Geocoder(getContext());
		return gc.getFromLocationName(query, MAX_RESULTS,
				Constants.SE_LATITUDE, Constants.NW_LONGITUDE,
				Constants.NW_LATITUDE, Constants.SE_LONGITUDE);
	}
	
	/***
	 * Generate comma separated address line
	 * 
	 * @param address
	 * @return
	 */
	private String getAddressString(Address address) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, len = address.getMaxAddressLineIndex(); i < len; i++) {
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(address.getAddressLine(i));
		}
		return sb.toString();
	}
}
package com.ganesia.crimereport.adapters;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.widget.SimpleCursorAdapter;

import com.ganesia.crimereport.Constants;
import com.ganesia.crimereport.R;

public class PlaceSuggestionsCursorAdapter extends SimpleCursorAdapter {
	
	private static final int MAX_RESULTS = 10;
	
	private static final String[] mFields = { "_id", "icon", "title", "address", "latitude", "longitude" };
	private static final String[] mVisible = { "icon", "title", "address" };
	private static final int[] mViewIds = { R.id.ic_place, R.id.tv_suggestion_title_1, R.id.tv_suggestion_title_2 };

	private Context mContext;
	
	public PlaceSuggestionsCursorAdapter(Context context) {
		super(context, R.layout.item_place_suggestions, null, mVisible, mViewIds, 0);
		mContext = context;
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		try {
			if (!TextUtils.isEmpty(constraint)) {
				List<Address> addresses = getLocation(constraint.toString());
				return new SuggestionsCursor(addresses);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
		Geocoder gc = new Geocoder(mContext);
		return gc.getFromLocationName(query, MAX_RESULTS,
				Constants.SE_LATITUDE, Constants.NW_LONGITUDE,
				Constants.NW_LATITUDE, Constants.SE_LONGITUDE);
	}
	
	private static class SuggestionsCursor extends AbstractCursor {
		private List<Address> mResults;

		public SuggestionsCursor(List<Address> addresses) {
			mResults = addresses;
		}

		@Override
		public int getCount() {
			return mResults.size();
		}

		@Override
		public String[] getColumnNames() {
			return mFields;
		}

		@Override
		public long getLong(int column) {
			if (column == 0) {
				return mPos;
			}
			throw new UnsupportedOperationException("unimplemented");
		}

		@Override
		public String getString(int column) {
			Address address = mResults.get(mPos);
			
			switch (column) {
			case 1:
				return Integer.toString(R.drawable.ic_action_place);
			case 2:
				return address.getFeatureName();
			case 3:
				return getAddressString(address);
			case 4:
				return Double.toString(address.getLatitude());
			case 5:
				return Double.toString(address.getLongitude());
			default:
				throw new UnsupportedOperationException("unimplemented");
			}
		}

		@Override
		public short getShort(int column) {
			throw new UnsupportedOperationException("unimplemented");
		}

		@Override
		public int getInt(int column) {
			throw new UnsupportedOperationException("unimplemented");
		}

		@Override
		public float getFloat(int column) {
			throw new UnsupportedOperationException("unimplemented");
		}

		@Override
		public double getDouble(int column) {
			Address address = mResults.get(mPos);
			
			switch (column) {
			case 4:
				return address.getLatitude();
			case 5:
				return address.getLongitude();
			default:
				throw new UnsupportedOperationException("unimplemented");
			}
		}

		@Override
		public boolean isNull(int column) {
			return false;
		}
		

		/***
		 * Generate comma separated address line
		 * 
		 * @param address
		 * @return
		 */
		private static String getAddressString(Address address) {
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

}

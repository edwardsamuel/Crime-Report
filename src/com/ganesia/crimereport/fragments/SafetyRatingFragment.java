package com.ganesia.crimereport.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;

import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.ganesia.crimereport.R;
import com.ganesia.crimereport.adapters.CustomListViewAdapter;
import com.ganesia.crimereport.adapters.TotalAverageAdapter;
import com.ganesia.crimereport.models.CrimeItem;
import com.ganesia.crimereport.models.SafetyRating;
import com.ganesia.crimereport.models.SafetyRatingItem;
import com.ganesia.crimereport.models.Tuple;

public class SafetyRatingFragment extends Fragment {

	private GridView mGridView;
	private ListView mListView;
	private ArrayList<SafetyRatingItem> mSafetyRatingList = new ArrayList<SafetyRatingItem>();
	private ArrayList<Tuple> mTotalCrime = new ArrayList<Tuple>();
	private CustomListViewAdapter mSafetyRatingListAdapter;
	private TotalAverageAdapter mTotalAverageAdapter;
	private static final int STATUS_SAFE = 0;
	private static final int STATUS_MODERATE = 1;
	private static final int STATUS_DANGER = 2;
	private int mSafetyStatus;
	private String mClock;
	
	private SafetyRating mSafetyRating;
	private TextView mTextSafetyStatus;
	private TextView mTime;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d("safety", "start onCreate");
		View safetyRatingView = inflater.inflate(R.layout.fragment_safety_rating, container, false);
		mTextSafetyStatus = (TextView) safetyRatingView.findViewById(R.id.safety_status);
		mTime = (TextView) safetyRatingView.findViewById(R.id.specific_time);

		// total & average number of crimes
		mGridView = (GridView) safetyRatingView.findViewById(R.id.gridview_rating);
		mTotalAverageAdapter = new TotalAverageAdapter(getActivity(), mTotalCrime);		
		mGridView.setAdapter(mTotalAverageAdapter);
		
		// list of crimes at specific time
		mListView = (ListView) safetyRatingView.findViewById(R.id.listview);
		mSafetyRatingListAdapter = new CustomListViewAdapter(getActivity(), mSafetyRatingList);
		mListView.setAdapter(mSafetyRatingListAdapter);
		
		mTime.setText("Crimes at: "+ mClock);
		// set the safety rating state:
		if (mSafetyStatus == STATUS_MODERATE){
			mTextSafetyStatus.setBackgroundColor(Color.rgb(0xFF, 0x98, 0x00));
			mTextSafetyStatus.setText("MODERATE");
		}
		else if (mSafetyStatus == STATUS_DANGER){
			mTextSafetyStatus.setBackgroundColor(Color.rgb(0xF4, 0x43, 0x36));
			mTextSafetyStatus.setText("DANGER");
		}
		else {
			mTextSafetyStatus.setBackgroundColor(Color.rgb(0x4C,  0xAF, 0x50));
			mTextSafetyStatus.setText("SAFE");
		}

		Log.d("safety", "end onCreate");
		return safetyRatingView;
	}
	
	public SafetyRating getSafetyRating() {
		return mSafetyRating;
	}
	
	private String getStartHour(int time, int intervalHour) {
		int startHour = time - intervalHour;
		if (startHour < 0) {
			startHour = startHour + 12;
			return startHour + "PM";
		}
		else if (startHour > 12) {
			return startHour - 12 + " PM";
		}
		else if (startHour == 12) {
			return startHour + " PM";
		}
		else {
			return startHour + " AM";
		}
	}
	
	private String getEndHour(int time, int intervalHour) {
		int endHour = time + intervalHour;
		if (endHour >= 24) {
			endHour = endHour - 24;
			return endHour + " AM";
		}
		else if (endHour >= 12 && endHour < 24) {
			endHour = endHour - 12;
			return endHour + " PM";
		}
		else {
			return endHour + " AM";
		}
	}
	
	public void update(SafetyRating rating) {
		Log.d("safety","startUpdate");
		int tempNArrested = 0;
		int tempNWanted = 0; 
		SafetyRatingItem tempSafetyRatingItem;
		ArrayList<CrimeItem> tempValues = new ArrayList<CrimeItem>();
		
		String mStringStartHours;
		String mStringEndHours;
		
		mSafetyRating = rating;
		
		// get Safety Status
		mSafetyStatus = mSafetyRating.getSafetyStatus();
		
		// "Crimes at x time"
		Calendar cal = Calendar.getInstance();
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		
		// append startHours and endHours to mClock
		mClock = getStartHour(hours, 3) + " - " + getEndHour(hours, 3);
		
		
		// load CrimeList
		HashMap<String, ArrayList<CrimeItem>> hm = mSafetyRating.getTrendingCrimeAroundUserTimedate(6 * 3600 * 1000);
		mSafetyRatingList.clear();
		
		Collection<String> keys = hm.keySet();
		for (String k : keys) {
			// clear all temp variables
			tempValues.clear();
			tempNArrested = 0;
			tempNWanted = 0;
			
			tempValues = hm.get(k);
			for (CrimeItem c : tempValues) {
				if (c.isArrest()) {
					// arrest:true
					tempNArrested++;
				}
				else {
					tempNWanted++;
				}
			}
			tempSafetyRatingItem = new SafetyRatingItem(k, String.valueOf(tempNArrested), String.valueOf(tempNWanted));
			mSafetyRatingList.add(tempSafetyRatingItem);
		}
		Log.d("safety","endUpdate");
	}
}

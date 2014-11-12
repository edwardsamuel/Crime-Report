package com.ganesia.crimereport;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.ganesia.crimereport.adapters.CustomGridViewAdapter;
import com.ganesia.crimereport.models.Crime;
import com.ganesia.crimereport.models.TopCrime;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class TopCrimeFragment extends Fragment {
	Crime mCrimeData;
	GridView mGridView;
	ArrayList<TopCrime> mTopCrimeList;
	CustomGridViewAdapter mCustomGridViewAdapter;

	public TopCrimeFragment() {
		mTopCrimeList = new ArrayList<TopCrime>();
		// // initialize the mTopCrimeList
		mTopCrimeList.add(new TopCrime("---", "0"));
		mTopCrimeList.add(new TopCrime("---", "0"));
		mTopCrimeList.add(new TopCrime("---", "0"));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View vGridView = inflater.inflate(R.layout.fragment_main, container,
				false);
		mGridView = (GridView) vGridView.findViewById(R.id.gridview);
		mCustomGridViewAdapter = new CustomGridViewAdapter(getActivity(),
				mTopCrimeList);
		mGridView.setAdapter(mCustomGridViewAdapter);

		return vGridView;
	}
	
	public void updateTopCrime(ArrayList<TopCrime> crimeList) {
		mCustomGridViewAdapter.clear();
		mCustomGridViewAdapter.addAll(crimeList);
		mCustomGridViewAdapter.notifyDataSetChanged();
	}
}

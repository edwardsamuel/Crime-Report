package com.ganesia.crimereport.fragments;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.ganesia.crimereport.R;
import com.ganesia.crimereport.adapters.CustomGridViewAdapter;
import com.ganesia.crimereport.models.Tuple;

public class TopCrimeFragment extends Fragment {
	private GridView mGridView;
	private ArrayList<Tuple> mTopCrimeList;
	private CustomGridViewAdapter mCustomGridViewAdapter;

	public TopCrimeFragment() {
		mTopCrimeList = new ArrayList<Tuple>();
		// // initialize the mTopCrimeList
		mTopCrimeList.add(new Tuple("---", "0"));
		mTopCrimeList.add(new Tuple("---", "0"));
		mTopCrimeList.add(new Tuple("---", "0"));
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
	
	public void updateTopCrime(ArrayList<Tuple> crimeList) {
		mCustomGridViewAdapter.clear();
		mCustomGridViewAdapter.addAll(crimeList);
		mCustomGridViewAdapter.notifyDataSetChanged();
	}
}

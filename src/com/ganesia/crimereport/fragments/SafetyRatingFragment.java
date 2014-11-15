package com.ganesia.crimereport.fragments;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ganesia.crimereport.R;
import com.ganesia.crimereport.adapters.CustomListViewAdapter;
import com.ganesia.crimereport.models.SafetyRatingItem;

public class SafetyRatingFragment extends Fragment {

	ListView listView;
	ArrayList<SafetyRatingItem> safetyRatingList = new ArrayList<SafetyRatingItem>();
	CustomListViewAdapter customListViewAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		safetyRatingList.add(new SafetyRatingItem("Robbery", "Sat, 18 Oct 2014 21:50:00 GMT", "Status: 10/20 Arrested"));
		safetyRatingList.add(new SafetyRatingItem("Robbery", "Sat, 18 Oct 2014 21:50:00 GMT", "Status: 10/20 Arrested"));
		safetyRatingList.add(new SafetyRatingItem("Robbery", "Sat, 18 Oct 2014 21:50:00 GMT", "Status: 10/20 Arrested"));

		View vListView = inflater.inflate(R.layout.fragment_safety_rating, container, false);
		listView = (ListView) vListView.findViewById(R.id.listview);
		customListViewAdapter = new CustomListViewAdapter(getActivity(), safetyRatingList);
		listView.setAdapter(customListViewAdapter);

		return vListView;

	}

}

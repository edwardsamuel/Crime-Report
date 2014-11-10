package com.ganesia.crimereport;

import java.util.ArrayList;

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

public class TopCrimeFragment extends Fragment{

	Crime CrimeData;
	GridView gridView;
	ArrayList<TopCrime> topCrimeList = new ArrayList<TopCrime>();
	CustomGridViewAdapter customGridViewAdapter;
	
	@Override
	   public View onCreateView(LayoutInflater inflater,	
	      ViewGroup container, Bundle savedInstanceState) {
			// PANGGIL API 1
		      topCrimeList.add(new TopCrime("Theft", "1237"));
		      topCrimeList.add(new TopCrime("Battery", "726"));
		      topCrimeList.add(new TopCrime("Narcotics", "1820"));
		      // SELESAI PEMBANGGILAN API
		      View vGridView = inflater.inflate(R.layout.fragment_main, container, false);
		      gridView = (GridView) vGridView.findViewById(R.id.gridview);
		      customGridViewAdapter = new CustomGridViewAdapter(getActivity(), topCrimeList);
		      gridView.setAdapter(customGridViewAdapter);
		      
		      return vGridView;
		   }
}

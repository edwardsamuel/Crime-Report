package com.ganesia.crimereport;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SafetyRatingFragment extends Fragment{
	
	@Override
	   public View onCreateView(LayoutInflater inflater,	
	      ViewGroup container, Bundle savedInstanceState) {
		
		View vGridView = inflater.inflate(R.layout.fragment_safety_rating, container, false);
		
		return vGridView;
		
	}

}

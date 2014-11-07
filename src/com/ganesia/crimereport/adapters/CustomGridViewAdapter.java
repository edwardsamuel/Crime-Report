package com.ganesia.crimereport.adapters;

import java.util.ArrayList;

import com.ganesia.crimereport.models.TopCrime;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomGridViewAdapter extends ArrayAdapter<TopCrime> {
	Context context;
	int layoutResourceId;
	ArrayList<TopCrime> data = new ArrayList<TopCrime>();
	
	public CustomGridViewAdapter(Context context, int layoutResourceId, ArrayList<TopCrime> data){
		super(context, layoutResourceId, data);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row = convertView;
		RecordHolder holder = null;
		
		if(row == null){
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			holder = new RecordHolder();
			//holder.txtCrimeTitle = (TextView) row.findViewById(R.id.crime_title);
			//holder.txtCrimeCount = (TextView) row.findViewById(R.id.crime_count);
			row.setTag(holder);			
		}
		else{
			holder = (RecordHolder) row.getTag();			
		}
		
		TopCrime topCrime = data.get(position);
		//holder.txtCrimeTitle.setText(topCrime.getCrimeTitle());
		//holder.txtCrimeCount.setText(topCrime.getCrimeCount());
		
		return row;
	}
	
	static class RecordHolder {
		  TextView txtCrimeTitle;
		  TextView txtCrimeCount;
	}
}

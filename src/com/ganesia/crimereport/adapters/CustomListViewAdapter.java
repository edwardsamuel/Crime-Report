package com.ganesia.crimereport.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ganesia.crimereport.R;
import com.ganesia.crimereport.models.SafetyRatingItem;

public class CustomListViewAdapter extends ArrayAdapter<SafetyRatingItem>{

	private Activity context;
	private ArrayList<SafetyRatingItem> data = new ArrayList<SafetyRatingItem>();
	
	static class ViewHolder {
		  public TextView txtCrimeType;
		  public TextView txtCrimeDate;
		  public TextView txtCrimeStatus;
		  public ImageView imgCrimeType;
	}
	
	public CustomListViewAdapter(Activity context, ArrayList<SafetyRatingItem> data){
		super(context, R.layout.item_safety_row, data);
		this.context = context;
		this.data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row = convertView;
		
		if(row == null){
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.item_safety_row, null);
			
			// configure RecordHolder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtCrimeType = (TextView) row.findViewById(R.id.rating_crime_type);
			viewHolder.txtCrimeDate = (TextView) row.findViewById(R.id.rating_crime_date);
			viewHolder.txtCrimeStatus = (TextView) row.findViewById(R.id.rating_crime_status);
			viewHolder.imgCrimeType = (ImageView) row.findViewById(R.id.crime_icon);
			row.setTag(viewHolder);			
		}
		
		// fill data
		ViewHolder holder = (ViewHolder) row.getTag();
		SafetyRatingItem safetyRatingItem = data.get(position);
		holder.txtCrimeType.setText(safetyRatingItem.getCrimeType());
		holder.txtCrimeDate.setText(safetyRatingItem.getCrimeDate());
		holder.txtCrimeStatus.setText(safetyRatingItem.getCrimeStatus());
		
		switch(safetyRatingItem.getCrimeType()){
		case "Robbery":
			holder.imgCrimeType.setImageResource(R.drawable.ic_launcher);
		
		}
		
		return row;
	}
}

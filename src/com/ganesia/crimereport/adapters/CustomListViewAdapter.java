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
		  public TextView txtCrimeArrestedNum;
		  public TextView txtCrimeWantedNum;
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
			viewHolder.txtCrimeArrestedNum = (TextView) row.findViewById(R.id.rating_arrested_num);
			viewHolder.txtCrimeWantedNum = (TextView) row.findViewById(R.id.rating_wanted_num);
			viewHolder.imgCrimeType = (ImageView) row.findViewById(R.id.crime_icon);
			row.setTag(viewHolder);			
		}
		
		// fill data
		ViewHolder holder = (ViewHolder) row.getTag();
		SafetyRatingItem safetyRatingItem = data.get(position);
		holder.txtCrimeType.setText(safetyRatingItem.getCrimeType());
		holder.txtCrimeArrestedNum.setText(safetyRatingItem.getCrimeArrestedNum());
		holder.txtCrimeWantedNum.setText(safetyRatingItem.getCrimeWantedNum());
		
		switch(safetyRatingItem.getCrimeType()){
		case "Offense Involving Children":
			holder.imgCrimeType.setImageResource(R.drawable.ic_launcher);
			break;
		case "Robbery":
			holder.imgCrimeType.setImageResource(R.drawable.robbery);
			break;
		case "Theft":
			holder.imgCrimeType.setImageResource(R.drawable.theft);
			break;
		}
		
		return row;
	}
}

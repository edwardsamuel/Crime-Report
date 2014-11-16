package com.ganesia.crimereport.adapters;

import java.util.ArrayList;

import com.ganesia.crimereport.R;
import com.ganesia.crimereport.models.Tuple;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomGridViewAdapter extends ArrayAdapter<Tuple> {
	private Activity context;
	private ArrayList<Tuple> data = new ArrayList<Tuple>();
	
	static class ViewHolder {
		  TextView txtCrimeTitle;
		  TextView txtCrimeCount;
	}
	
	public CustomGridViewAdapter(Activity context, ArrayList<Tuple> data){
		super(context, R.layout.item_top_three, data);
		this.context = context;
		this.data = data;
	}	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row = convertView;
		
		if(row == null){
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.item_top_three, null);
			
			// configure RecordHolder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtCrimeTitle = (TextView) row.findViewById(R.id.crime_title);
			viewHolder.txtCrimeCount = (TextView) row.findViewById(R.id.crime_count);
			row.setTag(viewHolder);
		}
		
		// fill data
		ViewHolder holder = (ViewHolder) row.getTag();
		Tuple topCrime = data.get(position);
		holder.txtCrimeTitle.setText(topCrime.getValue1());
		holder.txtCrimeCount.setText(topCrime.getValue2());
		
		return row;
	}
	
	@Override
	public boolean areAllItemsEnabled()
	{
	    return false;
	}

	@Override
	public boolean isEnabled(int position)
	{
	    return false;
	}
		
}

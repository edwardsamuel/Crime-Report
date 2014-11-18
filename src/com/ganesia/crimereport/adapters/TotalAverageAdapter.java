package com.ganesia.crimereport.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ganesia.crimereport.R;
import com.ganesia.crimereport.models.Tuple;

public class TotalAverageAdapter extends ArrayAdapter<Tuple>{

	private Activity context;
	private ArrayList<Tuple> data = new ArrayList<Tuple>();
	
	static class ViewHolder {
		  TextView txtTitle;
		  TextView txtValue;
	}
	
	public TotalAverageAdapter(Activity context, ArrayList<Tuple> data){
		super(context, R.layout.item_total_average, data);
		this.context = context;
		this.data = data;
	}	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row = convertView;
		
		if(row == null){
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.item_total_average, null);
			
			// configure RecordHolder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.txtTitle = (TextView) row.findViewById(R.id.total_average_title);
			viewHolder.txtValue = (TextView) row.findViewById(R.id.total_average_value);
			row.setTag(viewHolder);			
		}
		
		// fill data
		ViewHolder holder = (ViewHolder) row.getTag();
		Tuple values = data.get(position);
		holder.txtTitle.setText(values.getValue1());
		holder.txtValue.setText(values.getValue2());
		
		return row;
	}
}

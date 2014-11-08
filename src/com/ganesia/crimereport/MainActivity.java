package com.ganesia.crimereport;

import java.util.LinkedHashMap;
import java.util.List;

import com.ganesia.crimereport.R;
import com.ganesia.crimereport.adapters.CrimeInterface;
import com.ganesia.crimereport.models.Crime;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	Crime CrimeData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView text = (TextView) findViewById(R.id.content);
		text.setText("mulai");
		
		consumeReportAPI();
	}
	
	protected void consumeReportAPI() {
		// Consume ReportAPI
		// Use Adapter from Retrofit Library
		RestAdapter restAdapter = new RestAdapter.Builder()
		.setEndpoint("http://crimedb.watchovermeapp.com:8080/crimereport/rs/data")
		.build();
	
		CrimeInterface service = restAdapter.create(CrimeInterface.class);
		service.getCrimeList("Chicago","2014-10-27", new Callback<Crime>() {
	
			@Override
			public void failure(RetrofitError arg0) {
				// TODO Auto-generated method stub
				TextView text = (TextView) findViewById(R.id.content);
				text.setText(arg0.toString());
				CrimeData = null;
			}
	
			@Override
			public void success(Crime arg0, Response arg1) {
				// TODO Auto-generated method stub
				TextView text = (TextView) findViewById(R.id.content);
				text.setText("sukses");
				CrimeData = new Crime(arg0);
				
				// pick the biggest three
				Gson gson = new Gson();
				String json = gson.toJson(CrimeData.topThreeCrime(), LinkedHashMap.class);
				text.setText(json);
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_search) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

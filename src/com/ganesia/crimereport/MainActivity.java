package com.ganesia.crimereport;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends FragmentActivity {
	
	private SlidingUpPanelLayout mLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TopCrimeFragment topCrimeFrag = new TopCrimeFragment();
		
		Log.d("coba", "oncreate main activity");
		//consumeReportAPI();
		
	}
	
//	protected void consumeReportAPI() {
//		// Consume ReportAPI
//		// Use Adapter from Retrofit Library
//		RestAdapter restAdapter = new RestAdapter.Builder()
//		.setEndpoint("http://crimedb.watchovermeapp.com:8080/crimereport/rs/data")
//		.build();
//	
//		CrimeInterface service = restAdapter.create(CrimeInterface.class);
//		service.getCrimeList("Chicago","2014-10-27", new Callback<Crime>() {
//	
//			@Override
//			public void failure(RetrofitError arg0) {
//				// TODO Auto-generated method stub
//				TextView text = (TextView) findViewById(R.id.content);
//				text.setText(arg0.toString());
//				CrimeData = null;
//			}
//	
//			@Override
//			public void success(Crime arg0, Response arg1) {
//				// TODO Auto-generated method stub
//				TextView text = (TextView) findViewById(R.id.content);
//				text.setText("sukses");
//				CrimeData = new Crime(arg0);
//				
//				// pick the biggest three
//				Gson gson = new Gson();
//				String json = gson.toJson(CrimeData.topThreeCrime(), LinkedHashMap.class);
//				text.setText(json);
//			}
//			
//		});
//	}

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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

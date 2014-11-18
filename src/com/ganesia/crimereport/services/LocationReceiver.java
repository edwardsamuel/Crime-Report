package com.ganesia.crimereport.services;

import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.ganesia.crimereport.Constants;
import com.ganesia.crimereport.activities.MainActivity;
import com.ganesia.crimereport.models.SafetyRating;
import com.ganesia.crimereport.webservices.SafetyRatingInterface;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class LocationReceiver extends BroadcastReceiver {
	
	private RestAdapter mRestAdapter;
	private LatLng lastNotifiedLocation;
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	// Setup the adapter
    	// Use Adapter from Retrofit Library
    	mRestAdapter = new RestAdapter.Builder().setEndpoint(
    			"http://crimedb.watchovermeapp.com:8080/crimereport/rs/data")
    			.build();
    	Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();

		SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences("currentLocation", Context.MODE_PRIVATE);
		lastNotifiedLocation = new LatLng(sharedPref.getFloat("latitute", 0), sharedPref.getFloat("longitude", 0));
		Toast.makeText(context, "Location service", Toast.LENGTH_LONG).show();
		LatLng currentPosition = new LatLng(latitude, longitude);
		double distance = HaversineDistance.calculateDistance(lastNotifiedLocation, currentPosition);
		if( distance > 500){
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putFloat("latitute", (float)latitude);
			editor.putFloat("longitude", (float)longitude);
			editor.commit();
	    	consumeSafetyRatingAPI(context, latitude, longitude);
	        Log.d("Raymond", "More than 500" + String.valueOf(location.getLatitude()).concat(", ").concat(String.valueOf(location.getLongitude())));
		}
    }
    
    private void consumeSafetyRatingAPI(final Context context, double lat, double lng) {
		SafetyRatingInterface service = mRestAdapter.create(SafetyRatingInterface.class);
		service.getSafetyRating(Constants.CITY, lat, lng, "My location", new Callback<SafetyRating>() {

			@Override
			public void failure(RetrofitError arg0) {
				Toast.makeText(context, arg0.toString(), Toast.LENGTH_LONG).show();
			}

			@Override
			public void success(SafetyRating arg0, Response arg1) {
				String result = arg0.getSafetyRating();
				if(result.toLowerCase(Locale.ENGLISH).equals("danger")){
					Intent resultIntent = new Intent(context, MainActivity.class);
				       
			        // Because clicking the notification opens a new ("special") activity, there's
			        // no need to create an artificial back stack.
			        PendingIntent notificationPendingIntent =
			            PendingIntent.getActivity(
			            context,
			            0,
			            resultIntent,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );
			        NotificationCompat.Builder mBuilder = 
			        	new NotificationCompat.Builder(context)
			        		.setSmallIcon(com.ganesia.crimereport.R.drawable.ic_launcher)
			        		.setContentTitle("Warning!")
			        		.setContentText("You are in danger area.")
			        		.setContentIntent(notificationPendingIntent)
			        		.setAutoCancel(true);
			        // Sets an ID for the notification
			        int mNotificationId = 001;
			        
			        // Gets an instance of the NotificationManager service
			        NotificationManager mNotifyMgr = 
			                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			        
			        // Builds the notification and issues it.
			        mNotifyMgr.notify(mNotificationId, mBuilder.build());
				}
			}
		});
	}
}

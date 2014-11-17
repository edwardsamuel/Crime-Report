package com.ganesia.crimereport.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.ganesia.crimereport.Constants;
import com.ganesia.crimereport.activities.MainActivity;
import com.ganesia.crimereport.models.CrimeItem;
import com.ganesia.crimereport.models.SafetyRating;
import com.ganesia.crimereport.webservices.SafetyRatingInterface;
import com.google.android.gms.location.LocationClient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class LocationReceiver extends BroadcastReceiver {
	
	private RestAdapter mRestAdapter;
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	// Setup the adapter
    	// Use Adapter from Retrofit Library
    	mRestAdapter = new RestAdapter.Builder().setEndpoint(
    			"http://crimedb.watchovermeapp.com:8080/crimereport/rs/data")
    			.build();
    	Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);
		double latitude = location.getLatitude();
		double longitude = location.getLatitude();
		
    	consumeSafetyRatingAPI(context, latitude, longitude);
        Log.d("Raymond", String.valueOf(location.getLatitude()).concat("lala , ").concat(String.valueOf(location.getLongitude())));
        
        
    }
    
    private void consumeSafetyRatingAPI(final Context context, double lat, double lng) {
		SafetyRatingInterface service = mRestAdapter.create(SafetyRatingInterface.class);
		service.getSafetyRating(Constants.CITY, lat, lng, new Callback<SafetyRating>() {

			@Override
			public void failure(RetrofitError arg0) {
				Toast.makeText(context, arg0.toString(), Toast.LENGTH_LONG).show();
			}

			@Override
			public void success(SafetyRating arg0, Response arg1) {
				String result = arg0.getSafetyRating();
				if(!result.toLowerCase(Locale.ENGLISH).equals("danger")){
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
			        		.setContentIntent(notificationPendingIntent);
			        
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

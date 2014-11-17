package com.ganesia.crimereport.services;

import com.ganesia.crimereport.activities.MainActivity;
import com.google.android.gms.location.LocationClient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {
	 
    @Override
    public void onReceive(Context context, Intent intent) {
 
        Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);
        Log.d("Raymond", String.valueOf(location.getLatitude()).concat("lala , ").concat(String.valueOf(location.getLongitude())));
        
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
        		.setSmallIcon(com.ganesia.crimereport.R.drawable.ic_action_search)
        		.setContentTitle("test")
        		.setContentText(String.valueOf(location.getLatitude()).concat(", ").concat(String.valueOf(location.getLongitude())))
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

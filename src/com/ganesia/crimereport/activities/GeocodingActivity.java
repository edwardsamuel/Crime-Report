package com.ganesia.crimereport.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ganesia.crimereport.R;
import com.ganesia.crimereport.models.CustomAddress;

public class GeocodingActivity extends Activity {
	
	public static final int MAX_RESULTS = 30;  // Max number of results returned
    private static final String TAG = "GeocoderDemo";

    Context mContext;
    Button mRunGeocoderWithBoundingBox;
    Button mRunGeocoder;
    TextView mResults;
    EditText mInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geocoding);

        mContext = this;

        mRunGeocoderWithBoundingBox = (Button) findViewById(R.id.btnRunGeocoderBoundingBox);
        mRunGeocoder = (Button) findViewById(R.id.btnRunGeocoder);
        mResults = (TextView) findViewById(R.id.txtResults);
        mInput = (EditText) findViewById(R.id.editTxtInput);

        // Move cursor to end of EditText
        mInput.setSelection(mInput.getText().length());

        mRunGeocoderWithBoundingBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                runGeocoder(true);
            }
        });

        mRunGeocoder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                runGeocoder(false);
            }
        });
    }

    /**
     * Runs the geocoder
     * @param withBoundingBox true if a bounding box should be used, false if it should not
     */
    private void runGeocoder(boolean withBoundingBox) {
        Geocoder gc = new Geocoder(mContext);

        Log.d(TAG, "Gecoder.isPresent = " + gc.isPresent());

        try {
            // Clear previous results
            mResults.setText("");

            List<CustomAddress> addresses = new ArrayList<CustomAddress>();
            List<Address> androidTypeAddresses;
            StringBuilder sb = new StringBuilder();

            if (withBoundingBox) {
                androidTypeAddresses = gc.getFromLocationName(mInput.getText().toString(),
                        MAX_RESULTS,
                        27.6236434,  // Tampa Bay Area, FL
                        -82.8511308,
                        28.3251809,
                        -82.0559399);
            } else {
                androidTypeAddresses = gc.getFromLocationName(mInput.getText().toString(),
                        MAX_RESULTS);
            }

            // Copy addresses to custom address object, to help with formatting output
            for (Address androidAddress : androidTypeAddresses) {
                addresses.add(new CustomAddress(androidAddress));
            }

            // Add results to StringBuilder to be output to UI
            for (CustomAddress customAddress : addresses) {
                sb.append('\u2022' + " ");
                sb.append(customAddress.toString());
                sb.append(System.getProperty("line.separator"));
            }

            // Show results in UI
            mResults.setText(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

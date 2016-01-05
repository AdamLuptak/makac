package com.example.makac;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TrackerActivity extends Activity implements LocationListener {
    private static final String TAG = "TrackerActivity";
    private long start;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    double latitude;
    double longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 0;

    protected LocationManager locationManager;
    private Location location;


    /**
     * This method set actual time and hide start button and show pause button
     *
     * @param view
     */
    public void onStartClicked(View view) {
        /* initialized current time start */
        this.start = System.currentTimeMillis();
        TextView textView = (TextView) findViewById(R.id.duration);
        textView.setText(formatTimeMillis(0L));
        /*hide start button*/
        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setVisibility(View.INVISIBLE);
        /*show pause button*/
        Button pauseButton = (Button) findViewById(R.id.pause_button);
        pauseButton.setVisibility(View.VISIBLE);
         /*show stop button*/
        Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setVisibility(View.VISIBLE);
    }

    /**
     * Convert a millisecond duration to a string format if its more than 1 hout put there hour
     *
     * @param currentMillis A duration to convert to a string form
     * @return A string of the form "X Days Y Hours Z Minutes A Seconds".
     */
    public static String formatTimeMillis(long currentMillis) {
        long hours = TimeUnit.MILLISECONDS.toHours(currentMillis);
        currentMillis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(currentMillis);
        currentMillis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(currentMillis);
        currentMillis -= TimeUnit.SECONDS.toMillis(seconds);
        long millis = TimeUnit.MILLISECONDS.toMillis(currentMillis);

        StringBuilder sb = new StringBuilder();
        if (hours >= 1) {
            sb.append((hours < 10) ? "0" : "");
            sb.append(hours);
            sb.append(":");
        }
        sb.append((minutes < 10) ? "0" : "");
        sb.append(minutes);
        sb.append(":");
        sb.append((seconds < 10) ? "0" : "");
        sb.append(seconds);
        sb.append(":");
        if (!(hours >= 1)) {
            sb.append(millis / 100);
        }

        return (sb.toString());
    }


    /**
     * function hide pause button and show reset and resume button
     */
    public void onPauseClicked(View view) {
        /*actualized time*/
        TextView textView = (TextView) findViewById(R.id.duration);
        long pausedMillis = System.currentTimeMillis();
        Log.i(TAG, pausedMillis + " " + this.start);
        textView.setText(formatTimeMillis(pausedMillis - this.start));
         /*hide pause button*/
        Button pauseButton = (Button) findViewById(R.id.pause_button);
        pauseButton.setVisibility(View.INVISIBLE);
        /*hide stop button*/
        Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setVisibility(View.INVISIBLE);
         /*show resume and reset button*/
        Button resumeButton = (Button) findViewById(R.id.resume_button);
        resumeButton.setVisibility(View.VISIBLE);
        Button restartButton = (Button) findViewById(R.id.reset_button);
        restartButton.setVisibility(View.VISIBLE);
    }

    /**
     * show pause button hide other buttons
     */
    public void onResumeClicked(View view) {
        /*hide buttons*/
        Button resumeButton = (Button) findViewById(R.id.resume_button);
        resumeButton.setVisibility(View.INVISIBLE);
        Button restartButton = (Button) findViewById(R.id.reset_button);
        restartButton.setVisibility(View.INVISIBLE);
        /*show pause button*/
        Button pauseButton = (Button) findViewById(R.id.pause_button);
        pauseButton.setVisibility(View.VISIBLE);
         /*show stop button*/
        Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setVisibility(View.VISIBLE);
    }

    /**
     * restart aplication show start button
     */
    public void onResetClicked(View view) {
        /*reset time*/
        this.start = 0L;
        TextView textView = (TextView) findViewById(R.id.duration);
        textView.setText(formatTimeMillis(this.start));
        /*hide buttons*/
        Button resumeButton = (Button) findViewById(R.id.resume_button);
        resumeButton.setVisibility(View.INVISIBLE);
        Button restartButton = (Button) findViewById(R.id.reset_button);
        restartButton.setVisibility(View.INVISIBLE);
        /*show pause button*/
        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setVisibility(View.VISIBLE);
    }

    /**
     * stop aplication show summary activity
     */
    public void onStopClicked(View view) {
        JSONObject summaryResults = new JSONObject();
        try {
            summaryResults.put("duration", getStringFromTextViev(R.id.duration));
            summaryResults.put("distance", getStringFromTextViev(R.id.distance));
            summaryResults.put("pace", getStringFromTextViev(R.id.pace));
            summaryResults.put("calories", getStringFromTextViev(R.id.calories));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(getApplicationContext(), SummaryActivity.class);
        intent.putExtra("json", summaryResults.toString());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        location = getLocation();
        if (location != null) {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
        }
//
//        Toast.makeText(
//                getApplicationContext(),
//                "Your Location is -\nLat: " + latitude + "\nLong: "
//                        + longitude, Toast.LENGTH_LONG).show();
//        LocationManager lm = (LocationManager) getSystemService((Context.LOCATION_SERVICE));
//        LocationListener ll = new myLocationlistener();
//        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
    }

    public static boolean isLocationEnabled(Context context) {
        //...............
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tracker, menu);
        return true;
    }

    @Override
    protected void onPause() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
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

    /**
     * Find TextView and getText from it return as String
     *
     * @param resource
     * @return text from TextView
     */
    public String getStringFromTextViev(int resource) {
        TextView tx = (TextView) findViewById(resource);
        return tx.getText().toString();
    }


    @Override
    public void onLocationChanged(Location location) {


        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        float distance = 0;
        Location crntLocation = new Location("crntlocation");
        crntLocation.setLatitude(this.latitude);
        crntLocation.setLongitude(this.longitude);

        Location newLocation = new Location("newlocation");
        newLocation.setLatitude(latitude);
        newLocation.setLongitude(longitude);


        //float distance = crntLocation.distanceTo(newLocation);  in meters
        distance = location.distanceTo(newLocation) / 1000; // in km


        TextView tx = (TextView) findViewById(R.id.distance);
        tx.setText(String.valueOf(distance));



        double speed = 0;
        speed = newLocation.getSpeed() - crntLocation.getSpeed();

        tx = (TextView) findViewById(R.id.pace);
        tx.setText(String.valueOf(speed));

        Toast.makeText(
                getApplicationContext(),
                distance + "   Your Location change sdssdsddsdssd ither is now -\nLat: " + location.getLatitude() + "\nLong: "
                        + location.getLongitude(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public Location getLocation() {
        try {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                this.canGetLocation = true;

                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location != null) {

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }

                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
}

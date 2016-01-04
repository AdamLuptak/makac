package com.example.makac;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TrackerActivity extends Activity {
    private static final String TAG = "TrackerActivity";
    private long start;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tracker, menu);
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

    /**
     * Find TextView and getText from it return as String
     * @param resource
     * @return text from TextView
     */
    public String getStringFromTextViev(int resource) {
        TextView tx = (TextView) findViewById(resource);
        return tx.getText().toString();
    }
}

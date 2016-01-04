package com.example.makac;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Show summary results from tracker activity
 */
public class SummaryActivity extends Activity {

    private static final String TAG = "SummaryActivity";
    private JSONObject result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        Intent intent = getIntent();
        try {
            this.result = new JSONObject(intent.getStringExtra("json"));
            //set duration
            setStringTextViev(R.id.duration, result.getString("duration"));
            //set distance
            setStringTextViev(R.id.distance, result.getString("distance"));
            //set pace
            setStringTextViev(R.id.pace, result.getString("pace"));
            //set calories
            setStringTextViev(R.id.calories, result.getString("calories"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * take resource find TextView nad put there String text
     *
     * @param resource
     * @param text
     */
    public void setStringTextViev(int resource, String text) {
        TextView tx = (TextView) findViewById(resource);
        tx.setText(text);
    }

    /**
     * Starts TrackerActivity
     *
     * @param view
     */
    public void backToTracker(View view) {
        Intent intent = new Intent(getApplicationContext(), TrackerActivity.class);
        startActivity(intent);
    }

    /**
     * Sent email with results
     *
     * @param view
     */
    public void onShareClick(View view) {
        /* Create the Intent */
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        /* Fill it with Data */
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"to@email.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Results");
        StringBuilder sb = new StringBuilder();

        try {
            sb.append("Duration: " + this.result.getString("duration") + "\n");
            sb.append("Distance: " + this.result.getString("distance") + "\n");
            sb.append("Pace: " + this.result.getString("pace") + "\n");
            sb.append("Calories: " + this.result.getString("calories") + "\n");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, sb.toString());

        /* Send it off to the Activity-Chooser */
        this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

    }
}

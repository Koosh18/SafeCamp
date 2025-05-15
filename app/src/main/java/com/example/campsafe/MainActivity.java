package com.example.campsafe;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

/**
 * MainActivity
 * Entry point of the SafeCamp app.
 * This activity currently acts as the launcher activity.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * Used to set up the UI and initialize components.
     *
     * @param savedInstanceState - If the activity is being re-initialized after
     * previously being shut down, this Bundle contains the most recent data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the user interface layout for this Activity
        // The layout file is defined in res/layout/activity_main.xml
        setContentView(R.layout.activity_main);

        // TODO: Add initialization code here (e.g., check login status, navigate to role-based dashboard)
    }
}

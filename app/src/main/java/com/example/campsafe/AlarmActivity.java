package com.example.campsafe;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AlarmActivity extends AppCompatActivity {
    private static Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // Stop Alarm when button is clicked
        Button stopAlarmButton = findViewById(R.id.stop_alarm);
        stopAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm();
            }
        });
    }

    private void stopAlarm() {
        // Stop the AlarmService
        Intent intent = new Intent(this, AlarmService.class);
        stopService(intent);

        finish();  // Close the activity
    }

}

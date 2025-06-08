package com.example.campsafe.alarmActivities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.campsafe.R;
import com.example.campsafe.newVisitorActivities.VisitorApprovalActivity;

/**
 * Activity to handle the alarm when the app is running in the foreground.
 * Redirects to VisitorApprovalActivity to show visitor details and approval options.
 */
public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_approval);

        // Stop Alarm button
        Button stopAlarmButton = findViewById(R.id.stop_alarm_button);
        stopAlarmButton.setOnClickListener(v -> stopAlarm());

        // Get visitor details from intent (if app is running)
        Intent intent = getIntent();
        String visitorName = intent.getStringExtra("visitor_name");
        String visitReason = intent.getStringExtra("visit_reason");
        String documentId = intent.getStringExtra("document_id");

        // Immediately redirect to VisitorApprovalActivity with visitor details
        Intent approvalIntent = new Intent(this, VisitorApprovalActivity.class);
        approvalIntent.putExtra("visitor_name", visitorName);
        approvalIntent.putExtra("visit_reason", visitReason);
        approvalIntent.putExtra("document_id", documentId);
        startActivity(approvalIntent);
        finish(); // Close AlarmActivity
    }

    private void stopAlarm() {
        // Stop the AlarmService
        Intent intent = new Intent(this, AlarmService.class);
        stopService(intent);
    }
}
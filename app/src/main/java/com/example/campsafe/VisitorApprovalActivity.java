package com.example.campsafe;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity to display visitor details and allow the user to stop the alarm and approve/reject the visitor.
 */
public class VisitorApprovalActivity extends AppCompatActivity {

    private TextView visitorNameText, visitReasonText;
    private Button stopAlarmButton, approveButton, rejectButton;
    private String documentId;
    private FirebaseFirestore db;
    private MediaPlayer mediaPlayer;  // MediaPlayer to handle ringtone playback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_approval);

        // Initialize UI elements
        visitorNameText = findViewById(R.id.visitor_name_text);
        visitReasonText = findViewById(R.id.visit_reason_text);
        stopAlarmButton = findViewById(R.id.stop_alarm_button);
        approveButton = findViewById(R.id.approve_button);
        rejectButton = findViewById(R.id.reject_button);

        db = FirebaseFirestore.getInstance();

        // Get visitor details from intent
        Intent intent = getIntent();
        String visitorName = intent.getStringExtra("visitor_name");
        String visitReason = intent.getStringExtra("visit_reason");
        documentId = intent.getStringExtra("document_id");

        // Display visitor details
        visitorNameText.setText("Visitor: " + (visitorName != null ? visitorName : "Unknown"));
        visitReasonText.setText("Reason: " + (visitReason != null ? visitReason : "Not specified"));

        // Initially hide approve/reject buttons
        approveButton.setVisibility(View.GONE);
        rejectButton.setVisibility(View.GONE);

        // Start playing ringtone
        startRingtone();

        // Stop alarm button click listener
        stopAlarmButton.setOnClickListener(v -> stopRingtone());

        // Approve button click listener
        approveButton.setOnClickListener(v -> updateVisitorStatus(true));

        // Reject button click listener
        rejectButton.setOnClickListener(v -> updateVisitorStatus(false));
    }

    /**
     * Starts playing the default ringtone when the activity appears on the screen.
     */
    private void startRingtone() {
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mediaPlayer = MediaPlayer.create(this, ringtoneUri);
        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true); // Make it loop until stopped
            mediaPlayer.start();
        }
    }

    /**
     * Stops the ringtone when the "Stop Alarm" button is clicked.
     */
    private void stopRingtone() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Hide stop alarm button and show approve/reject buttons
        stopAlarmButton.setVisibility(View.GONE);
        approveButton.setVisibility(View.VISIBLE);
        rejectButton.setVisibility(View.VISIBLE);
    }

    /**
     * Updates the visitor's approval status in Firestore.
     * @param approved True to approve, false to reject.
     */
    private void updateVisitorStatus(boolean approved) {
        if (documentId != null) {
            db.collection("new_visitor").document(documentId)
                    .update("approved", approved)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, approved ? "Visitor approved" : "Visitor rejected", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error updating status", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRingtone(); // Ensure ringtone stops if activity is closed
    }
}

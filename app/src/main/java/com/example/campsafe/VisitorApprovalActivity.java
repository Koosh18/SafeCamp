package com.example.campsafe;

import android.content.Intent;
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

        // Stop alarm button click listener
        stopAlarmButton.setOnClickListener(v -> {
            // Stop the alarm
            MyFirebaseMessagingService.stopAlarm();
            Intent stopServiceIntent = new Intent(this, AlarmService.class);
            stopService(stopServiceIntent);

            // Show approve/reject buttons
            stopAlarmButton.setVisibility(View.GONE);
            approveButton.setVisibility(View.VISIBLE);
            rejectButton.setVisibility(View.VISIBLE);
        });

        // Approve button click listener
        approveButton.setOnClickListener(v -> updateVisitorStatus(true));

        // Reject button click listener
        rejectButton.setOnClickListener(v -> updateVisitorStatus(false));
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
}
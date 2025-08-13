package com.example.campsafe.studentDashboard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.campsafe.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class VisitorApprovalActivity extends AppCompatActivity {
    private TextView visitorNameText, visitReasonText;
    private Button approveButton, rejectButton;
    private String documentId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_approval);

        visitorNameText = findViewById(R.id.visitor_name_text);
        visitReasonText = findViewById(R.id.visit_reason_text);
        approveButton = findViewById(R.id.approve_button);
        rejectButton = findViewById(R.id.reject_button);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String visitorName = intent.getStringExtra("visitor_name");
        String visitReason = intent.getStringExtra("visit_reason");
        documentId = intent.getStringExtra("document_id");

        if (documentId == null || documentId.isEmpty()) {
            Toast.makeText(this, "Invalid visitor data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        visitorNameText.setText("Visitor: " + (visitorName != null ? visitorName : "Unknown"));
        visitReasonText.setText("Reason: " + (visitReason != null ? visitReason : "Not specified"));

        approveButton.setOnClickListener(v -> updateVisitorStatus(true));
        rejectButton.setOnClickListener(v -> updateVisitorStatus(false));
    }

    private void updateVisitorStatus(boolean approved) {
        db.collection("new_visitor").document(documentId)
                .update("approved", approved)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, approved ? "Visitor approved" : "Visitor rejected", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error updating status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
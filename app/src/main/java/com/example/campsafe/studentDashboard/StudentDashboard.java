package com.example.campsafe.studentDashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.campsafe.logins.LogOutHelper;
import com.example.campsafe.newVisitorActivities.DefaultMessage;
import com.example.campsafe.logins.LoginPage;
import com.example.campsafe.R;
import com.example.campsafe.newVisitorActivities.VisitorApprovalActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Collections;

public class StudentDashboard extends AppCompatActivity {
    // Selected date and time for pre-booking visitor

    // User info received from Intent
    private String studentName;
    private int studentId;

    private FirebaseFirestore db; // Firestore database instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentdashboard);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // UI elements
        Button logoutBtn = findViewById(R.id.logout);
        Button preBookBtn = findViewById(R.id.button_pre_book);
        TextView welcomeMsg = findViewById(R.id.welcome_message);

        // Load a default message fragment into the fragment container
        DefaultMessage msgFragment = new DefaultMessage();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, msgFragment);
        ft.commit();

        // Get user info passed from previous activity (Splash or Login)
        Intent intent = getIntent();
        studentName = intent.getStringExtra("name");
        studentId = intent.getIntExtra("id", -1); // -1 means invalid id

        // Defensive check for invalid ID
        if (studentId == -1 || studentName == null) {
            Log.e("StudentDashboard", "Invalid user info received; redirecting to login");
            // Redirect to login page if user data missing
            Intent loginIntent = new Intent(StudentDashboard.this, LoginPage.class);
            startActivity(loginIntent);
            finish();
            return; // Stop execution here
        }

        // Log the received user info for debugging
        Log.i("StudentDashboard", "Logged in user: " + studentName + ", ID: " + studentId);

        // Show welcome message with student's name
        welcomeMsg.setText("Welcome "+ studentName);

        // Update FCM token for this student in Firestore to enable notifications
        updateFcmToken(String.valueOf(studentId));

        // Setup logout button listener
        logoutBtn.setOnClickListener(v -> new LogOutHelper(this).showLogoutConfirmation());

        // Setup pre-book button listener to show booking dialog
        preBookBtn.setOnClickListener(v -> new PreBookForm(this,studentName,studentId).showPreBookFormDialog());

        // Listen to Firestore for any new visitor requests directed to this student that are not yet approved
        db.collection("NewVisitor")
                .whereEqualTo("person_id", String.valueOf(studentId))
                .whereEqualTo("approved", null)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("StudentDashboard", "Error fetching visitor requests", error);
                        return;
                    }
                    if (snapshots != null && !snapshots.isEmpty()) {
                        Log.i("StudentDashboard", "New visitor request(s) found");

                        // Update UI fragment with a notification message
                        msgFragment.updateText("You have a visitor");

                        // Redirect to VisitorApprovalActivity for each visitor request
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Intent intent1= new Intent(StudentDashboard.this, VisitorApprovalActivity.class);
                            intent1.putExtra("visitor_name", doc.getString("visitor_name"));
                            intent1.putExtra("visit_reason", doc.getString("visit_reason"));
                            intent1.putExtra("document_id", doc.getId());
                            startActivity(intent1);
                        }
                    }
                });

    }
    private void updateFcmToken(String userId) {
        if (userId == null || userId.isEmpty()) {
            Log.e("StudentDashboard", "Cannot update FCM token: invalid user ID");
            return;
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("StudentDashboard", "Fetching FCM token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    // Merge the FCM token into the existing student document in Firestore
                    db.collection("Students").document(userId)
                            .set(Collections.singletonMap("fcm_token", token), com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener(aVoid -> Log.i("StudentDashboard", "FCM token updated successfully"))
                            .addOnFailureListener(e -> Log.e("StudentDashboard", "Failed to update FCM token", e));
                });
    }


}

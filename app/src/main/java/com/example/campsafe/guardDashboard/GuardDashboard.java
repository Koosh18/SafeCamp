package com.example.campsafe.guardDashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.campsafe.logins.LogOutHelper;
import com.example.campsafe.logins.LoginPage;
import com.example.campsafe.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Collections;

public class GuardDashboard extends AppCompatActivity {
    private String guardName;
    private int guardId;
    private FirebaseFirestore db;
    private LinearLayout dashboardContainer;
    private View fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guarddashboard);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // UI elements
        dashboardContainer = findViewById(R.id.dashboard_container);
        fragmentContainer = findViewById(R.id.fragment_container);

        Button newVisitorBtn = findViewById(R.id.button_new_visitor);
        Button notifiedBtn = findViewById(R.id.button_notified);
        Button preBookingsBtn = findViewById(R.id.button_pre_bookings);
        Button campusInOutBtn = findViewById(R.id.button_campus_inout);
        Button logoutBtn = findViewById(R.id.logout);
        TextView welcomeMsg = findViewById(R.id.welcome_message);

        // Get guard info from intent
        Intent intent = getIntent();
        guardName = intent.getStringExtra("name");
        guardId = intent.getIntExtra("id", -1);

        // Defensive check for invalid ID
        if (guardId == -1 || guardName == null) {
            Log.e("GuardDashboard", "Invalid guard info; redirecting to login");
            Intent loginIntent = new Intent(this, LoginPage.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Set welcome message
        welcomeMsg.setText("Welcome " + guardName);

        // Update FCM token
        updateFcmToken(String.valueOf(guardId));

        // Fragment Manager
        FragmentManager fm = getSupportFragmentManager();

        // Button listeners
        newVisitorBtn.setOnClickListener(v -> {
            new NewVisitorForm(this).showNewVisitorFormDialog();
            // Ensure dashboard remains visible; no fragment transaction needed
        });

        preBookingsBtn.setOnClickListener(v -> {
            new PreBookingsList(this).showPreBookingsListDialog();
            // Ensure dashboard remains visible; no fragment transaction needed
        });
        notifiedBtn.setOnClickListener(v -> {
            new ApprovalNotificationList(this).showApprovalNotificationsList();
            // Ensure dashboard remains visible; no fragment transaction needed
        });
        campusInOutBtn.setOnClickListener(v -> {
            new CampusInOutList(this).showCampusInOutListDialog();
            // Ensure dashboard remains visible; no fragment transaction needed
        });

        logoutBtn.setOnClickListener(v -> new LogOutHelper(this).showLogoutConfirmation());
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            dashboardContainer.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void updateFcmToken(String userId) {
        if (userId == null || userId.isEmpty()) {
            Log.e("GuardDashboard", "Cannot update FCM token: invalid user ID");
            return;
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("GuardDashboard", "Fetching FCM token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    db.collection("Guards").document(userId)
                            .set(Collections.singletonMap("fcm_token", token), com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener(aVoid -> Log.i("GuardDashboard", "FCM token updated successfully"))
                            .addOnFailureListener(e -> Log.e("GuardDashboard", "Failed to update FCM token", e));
                });
    }
}
package com.example.campsafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class studentdashboard extends AppCompatActivity {
    // Selected date and time for pre-booking visitor
    private String selectedDate;
    private String selectedTime;

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
        default_msg msgFragment = new default_msg();
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
            Intent loginIntent = new Intent(studentdashboard.this, Loginpage.class);
            startActivity(loginIntent);
            finish();
            return; // Stop execution here
        }

        // Log the received user info for debugging
        Log.i("StudentDashboard", "Logged in user: " + studentName + ", ID: " + studentId);

        // Show welcome message with student's name
        welcomeMsg.setText("Welcome " + studentName);

        // Update FCM token for this student in Firestore to enable notifications
        updateFcmToken(String.valueOf(studentId));

        // Setup logout button listener
        logoutBtn.setOnClickListener(v -> showLogoutConfirmation());

        // Setup pre-book button listener to show booking dialog
        preBookBtn.setOnClickListener(v -> showPreBookDialog());

        // Listen to Firestore for any new visitor requests directed to this student that are not yet approved
        db.collection("new_visitor")
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
                            Intent intent1= new Intent(studentdashboard.this, VisitorApprovalActivity.class);
                            intent1.putExtra("visitor_name", doc.getString("visitor_name"));
                            intent1.putExtra("visit_reason", doc.getString("visit_reason"));
                            intent1.putExtra("document_id", doc.getId());
                            startActivity(intent1);
                        }
                    }
                });

    }

    /**
     * Updates the FCM token for the student in Firestore.
     * This is important for sending push notifications to the device.
     *
     * @param userId The student ID as a string
     */
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

    /**
     * Shows a confirmation dialog before logging out.
     * On confirmation, clears all login flags in SharedPreferences and redirects to Loginpage.
     */
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(studentdashboard.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    clearLoginFlags();
                    Intent loginIntent = new Intent(studentdashboard.this, Loginpage.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Clears the 'isLoggedIn' flag in all SharedPreferences related to user roles.
     * This is to ensure complete logout across all possible user types.
     */
    private void clearLoginFlags() {
        String[] prefNames = {"StudentPrefs", "FacultyPrefs", "GuardPrefs"};
        for (String prefName : prefNames) {
            SharedPreferences sp = getSharedPreferences(prefName, MODE_PRIVATE);
            if (sp.getBoolean("isLoggedIn", false)) {
                sp.edit().putBoolean("isLoggedIn", false).apply();
                Log.i("StudentDashboard", "Cleared isLoggedIn for " + prefName);
            }
        }
    }

    /**
     * Shows the pre-booking dialog allowing student to select date, time,
     * enter visitor details, and submit the booking request.
     */
    private void showPreBookDialog() {
        Dialog dialog = new Dialog(studentdashboard.this);
        dialog.setContentView(R.layout.pre_book);
        if (dialog.getWindow() != null) {
            // Set dialog size to 90% width and height of the screen
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    (int) (getResources().getDisplayMetrics().heightPixels * 0.9)
            );
        }
        dialog.show();

        // Find dialog UI elements
        Button selectDateBtn = dialog.findViewById(R.id.date_picker);
        Button selectTimeBtn = dialog.findViewById(R.id.time_picker);
        Button submitBtn = dialog.findViewById(R.id.btn_submit);
        Button cancelBtn = dialog.findViewById(R.id.btn_cancel);
        TextView visitorNameInput = dialog.findViewById(R.id.visitor_name);
        TextView numVisitorsInput = dialog.findViewById(R.id.num_visitors);

        // Date picker dialog
        selectDateBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(studentdashboard.this, (view, year, month, day) -> {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.set(year, month, day);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDate = dateFormat.format(selectedCal.getTime());
                selectDateBtn.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Time picker dialog
        selectTimeBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(studentdashboard.this, (view, hour, minute) -> {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.set(Calendar.HOUR_OF_DAY, hour);
                selectedCal.set(Calendar.MINUTE, minute);
                selectedCal.set(Calendar.SECOND, 0);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                selectedTime = timeFormat.format(selectedCal.getTime());
                selectTimeBtn.setText(selectedTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        // Submit button click
        submitBtn.setOnClickListener(v -> {
            String visitorName = visitorNameInput.getText().toString().trim();
            String numVisitorsStr = numVisitorsInput.getText().toString().trim();

            // Validate inputs
            if (visitorName.isEmpty() || numVisitorsStr.isEmpty() || selectedDate == null || selectedTime == null) {
                Toast.makeText(studentdashboard.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int numVisitors;
            try {
                numVisitors = Integer.parseInt(numVisitorsStr);
                if (numVisitors <= 0) {
                    Toast.makeText(studentdashboard.this, "Number of visitors must be positive", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(studentdashboard.this, "Invalid number of visitors", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert booking data into Firestore using your helper class
            prebook_db prebook = new prebook_db();
            prebook.insertData(visitorName, numVisitorsStr, selectedDate, selectedTime, studentName, studentId, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(studentdashboard.this, "Pre-booking saved successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(studentdashboard.this, "Failed to save pre-booking", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Cancel button click dismisses dialog
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * Shows a dialog to accept or reject a visitor request.
     *
     * @param doc Firestore document snapshot representing the visitor request
     */
    private void showVisitorDialog(DocumentSnapshot doc) {
        if (isFinishing() || isDestroyed()) {
            // Don't show dialog if activity is finishing
            return;
        }

        String visitorName = doc.getString("visitor_name");
        String docId = doc.getId();

        new AlertDialog.Builder(studentdashboard.this)
                .setTitle("Visitor Request")
                .setMessage(visitorName + " wants to visit you.")
                .setPositiveButton("Accept", (dialog, which) -> {
                    // Mark visitor request as approved in Firestore
                    db.collection("new_visitor").document(docId)
                            .update("approved", true)
                            .addOnSuccessListener(aVoid -> Log.i("StudentDashboard", "Visitor approved: " + visitorName))
                            .addOnFailureListener(e -> Log.e("StudentDashboard", "Failed to approve visitor", e));
                })
                .setNegativeButton("Reject", (dialog, which) -> {
                    // Mark visitor request as rejected in Firestore
                    db.collection("new_visitor").document(docId)
                            .update("approved", false)
                            .addOnSuccessListener(aVoid -> Log.i("StudentDashboard", "Visitor rejected: " + visitorName))
                            .addOnFailureListener(e -> Log.e("StudentDashboard", "Failed to reject visitor", e));
                })
                .show();
    }
}

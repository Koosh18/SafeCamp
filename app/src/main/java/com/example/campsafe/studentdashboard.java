package com.example.campsafe;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class studentdashboard extends AppCompatActivity {
    String selectedDate;
    String selectedTime;
    String text, id; // Define text and id properly

    FirebaseFirestore db; // Firestore instance

    int idd ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentdashboard);

        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        Button logout = findViewById(R.id.logout);
        Button pre_book = findViewById(R.id.button_pre_book);
        TextView welcome = findViewById(R.id.welcome_message);
        default_msg msg = new default_msg();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, msg);
        ft.commit();

        Intent intent = getIntent();

        text = intent.getStringExtra("name");
         idd = intent.getIntExtra("id", -1); // Use -1 as default to check if it's missing
        String id = String.valueOf(idd);


        Log.e("IntentCheck", "Received studentName: " + text + ", studentID: " + idd);

        SharedPreferences prefs = getSharedPreferences("StudentPrefs", MODE_PRIVATE);
        String userId = id ;

        if (userId == null) {
            Log.e("FCM", "User ID is not available in SharedPreferences");
            // Optionally, handle this case by redirecting the user to login.
        } else {
            // Now update the fcm_token field in Firestore.
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        FirebaseFirestore.getInstance().collection("Students").document(userId)
                                .set(Collections.singletonMap("fcm_token", token), SetOptions.merge());
                    });
        }







        welcome.setText("Welcome " + text);


        logout.setOnClickListener(v -> new AlertDialog.Builder(studentdashboard.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("StudentPrefs", MODE_PRIVATE);
                    boolean val = sharedPreferences.getBoolean("isLoggedIn", false);

                    if (val) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", false);
                        editor.apply();
                    } else {
                        SharedPreferences.Editor editor1 = getSharedPreferences("FacultyPrefs", MODE_PRIVATE).edit();
                        editor1.putBoolean("isLoggedIn", false);
                        editor1.apply();
                    }

                    SharedPreferences sharedPreferences2 = getSharedPreferences("GuardPrefs", MODE_PRIVATE);
                    boolean val2 = sharedPreferences2.getBoolean("isLoggedIn", false);

                    if (val2) {
                        SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                        editor2.putBoolean("isLoggedIn", false);
                        editor2.apply();
                    }

                    // Redirect to Login Screen
                    Intent loginIntent = new Intent(studentdashboard.this, Loginpage.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show());

        pre_book.setOnClickListener(v -> showPreBookDialog());

        // Load Default Message Fragment


        // Firestore Listener for Visitor Requests
        db.collection("new_visitor")
                .whereEqualTo("person_id",id )
                .whereEqualTo("approved", null)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("Firestore Error", "Failed to get visitor requests", error);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        Log.e("add","now") ;
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            msg.updateText("You have a visitor");
                            showVisitorDialog(doc);
                        }
                    }
                });
    }

    private void showPreBookDialog() {
        Dialog dialog = new Dialog(studentdashboard.this);
        dialog.setContentView(R.layout.pre_book);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    (int) (getResources().getDisplayMetrics().heightPixels * 0.9)
            );
        }
        dialog.show();

        Button select_date = dialog.findViewById(R.id.date_picker);
        Button select_time = dialog.findViewById(R.id.time_picker);
        Button submit = dialog.findViewById(R.id.btn_submit);
        Button cancel = dialog.findViewById(R.id.btn_cancel);
        TextView name = dialog.findViewById(R.id.visitor_name);
        TextView num = dialog.findViewById(R.id.num_visitors);

        select_date.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(studentdashboard.this, (view, year, month, day) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, day);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDate = dateFormat.format(selectedCalendar.getTime());
                select_date.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        select_time.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(studentdashboard.this, (view, hour, minute) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hour);
                selectedCalendar.set(Calendar.MINUTE, minute);
                selectedCalendar.set(Calendar.SECOND, 0);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                selectedTime = timeFormat.format(selectedCalendar.getTime());
                select_time.setText(selectedTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        submit.setOnClickListener(v -> {
            String visitorName = name.getText().toString().trim();
            String numVisitors = num.getText().toString().trim();

            if (visitorName.isEmpty() || numVisitors.isEmpty() || selectedDate == null || selectedTime == null) {
                Toast.makeText(studentdashboard.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            prebook_db prebook = new prebook_db();
            prebook.insertData(visitorName, numVisitors, selectedDate, selectedTime, text, idd, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(studentdashboard.this, "Pre-booking saved successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(studentdashboard.this, "Failed to save pre-booking", Toast.LENGTH_SHORT).show();
                }
            });
        });

        cancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void updateVisitorStatus(String docId, Boolean status, DialogInterface dialog) {
        db.collection("new_visitor").document(docId)
                .update("approved", status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Visitor " + (status ? "Accepted" : "Rejected"), Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); // Dismiss the dialog after updating status
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Failed to update status!", Toast.LENGTH_SHORT).show());
    }


    private void showVisitorDialog(DocumentSnapshot doc) {
        if (this.isFinishing() || this.isDestroyed()) {
            return; // Don't show dialog if the activity is finishing or destroyed
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(studentdashboard.this); // Fixed context
        builder.setTitle("Visitor Approval")
                .setMessage("Visitor: " + doc.getString("visitor_name") + "\nReason: " + doc.getString("reason"))
                .setPositiveButton("Accept", (dialog, which) -> updateVisitorStatus(doc.getId(), true,dialog))
                .setNegativeButton("Reject", (dialog, which) -> updateVisitorStatus(doc.getId(), false,dialog))
                .show();
    }
}

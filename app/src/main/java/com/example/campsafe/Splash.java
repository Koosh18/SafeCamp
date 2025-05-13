package com.example.campsafe;

import static com.google.firebase.auth.FirebaseAuth.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Splash extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this) ;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            // Check StudentPrefs first
            SharedPreferences studentPrefs = getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
            if (studentPrefs.getBoolean("isLoggedIn", false)) {
                Intent studentDashboard = new Intent(Splash.this, studentdashboard.class);
                studentDashboard.putExtra("name", studentPrefs.getString("studentName", "Student"));
                studentDashboard.putExtra("id",studentPrefs.getInt("studentID",10)) ;
                startActivity(studentDashboard);
                finish();
                return;
            }

            // Check GuardPrefs next
            SharedPreferences guardPrefs = getSharedPreferences("GuardPrefs", Context.MODE_PRIVATE);
            if (guardPrefs.getBoolean("isLoggedIn", false)) {
                Intent guardDashboard = new Intent(Splash.this, guarddashboard.class);
                guardDashboard.putExtra("name", guardPrefs.getString("guardName", "Guard"));


                startActivity(guardDashboard);
                finish();
                return;
            }

            // Check FacultyPrefs last
            SharedPreferences facultyPrefs = getSharedPreferences("FacultyPrefs", Context.MODE_PRIVATE);
            if (facultyPrefs.getBoolean("isLoggedIn", false)) {
                Intent facultyDashboard = new Intent(Splash.this, studentdashboard.class);
                facultyDashboard.putExtra("name", facultyPrefs.getString("facultyName", "Faculty"));
                facultyDashboard.putExtra("id",facultyPrefs.getInt("facultyID",10)) ;
                startActivity(facultyDashboard);
                finish();
                return;
            }

            // If no one is logged in, redirect to login
            Intent loginIntent = new Intent(Splash.this, Loginpage.class);
            startActivity(loginIntent);
            finish();
        }, 3000); // 3-second delay
    }
}






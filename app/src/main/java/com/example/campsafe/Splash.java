package com.example.campsafe;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.FirebaseApp;

/**
 * Splash activity
 * Displays a 3-second splash screen and redirects user based on saved login state.
 * User roles: Student, Faculty, Guard
 */
public class Splash extends AppCompatActivity {
    private boolean isLoggedIn(String role, String nameKey, String idKey, Class<?> dashboard) {
        SharedPreferences prefs = getSharedPreferences(role + "Prefs", Context.MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            Intent i = new Intent(Splash.this, dashboard);
            i.putExtra("name", prefs.getString(nameKey, role));
            i.putExtra("id", prefs.getInt(idKey, 10));
            startActivity(i);
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize Firebase before anything else
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Delay for splash screen (3 seconds)
        new Handler().postDelayed(() -> {

            if (isLoggedIn("Student", "studentName", "studentID", studentdashboard.class)) return;
            if (isLoggedIn("Guard", "guardName", "guardID", guarddashboard.class)) return;
            if (isLoggedIn("Faculty", "facultyName", "facultyID", studentdashboard.class)) return;


            // === 4. Default: No one is logged in, go to Login page ===
            Intent loginIntent = new Intent(Splash.this, Loginpage.class);
            startActivity(loginIntent);
            finish();

        }, 3000); // 3 seconds
    }
}

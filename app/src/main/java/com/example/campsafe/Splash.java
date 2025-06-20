package com.example.campsafe;

import com.example.campsafe.logins.LoginHelper;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.example.campsafe.guardDashboard.GuardDashboard;
import com.example.campsafe.studentDashboard.StudentDashboard;
import com.example.campsafe.logins.LoginPage;
import com.google.firebase.FirebaseApp;

/**
 * Splash activity
 * Displays a 3-second splash screen and redirects user based on saved login state.
 * User roles: Student, Faculty, Guard
 */
public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize Firebase before anything else
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Delay for splash screen (3 seconds)
        new Handler().postDelayed(() -> {

            if (LoginHelper.isLoggedIn(this,"Student", "studentName", "studentID", StudentDashboard.class)) return;
            if (LoginHelper.isLoggedIn(this,"Guard", "guardName", "guardID", GuardDashboard.class)) return;
            if (LoginHelper.isLoggedIn(this,"Faculty", "facultyName", "facultyID", StudentDashboard.class)) return;


            // === 4. Default: No one is logged in, go to Login page ===
            Intent loginIntent = new Intent(Splash.this, LoginPage.class);
            startActivity(loginIntent);
            finish();

        }, 3000); // 3 seconds
    }
}

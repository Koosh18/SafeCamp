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
import com.example.campsafe.logins.LoginPage;
import com.example.campsafe.R;
import com.example.campsafe.VisitorNotificationService;

public class StudentDashboard extends AppCompatActivity {
    private String studentName;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentdashboard);

        // UI elements
        Button logoutBtn = findViewById(R.id.logout);
        Button preBookBtn = findViewById(R.id.button_pre_book);
        Button checkVisitorBtn = findViewById(R.id.button_check_visitors);
        Button checkNewVisitorBtn = findViewById(R.id.button_check_new_visitors);
        Button campusInOutBtn = findViewById(R.id.button_campus_inout);
        Button alertAllGaurdsBtn = findViewById(R.id.alert_all_gaurds);
        TextView welcomeMsg = findViewById(R.id.welcome_message);

        // Load default message fragment
        DefaultMessage msgFragment = new DefaultMessage();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, msgFragment);
        ft.commit();

        // Get user info from intent
        Intent intent = getIntent();
        studentName = intent.getStringExtra("name");
        studentId = intent.getIntExtra("id", -1);

        // Check for invalid user data
        if (studentId == -1 || studentName == null) {
            Log.e("StudentDashboard", "Invalid user info received; redirecting to login");
            Intent loginIntent = new Intent(this, LoginPage.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Log user info
        Log.i("StudentDashboard", "Logged in user: " + studentName + ", ID: " + studentId);

        // Show welcome message
        welcomeMsg.setText("Welcome " + studentName);

        // Initialize notification service
        VisitorNotificationService notificationService = new VisitorNotificationService(this, studentId);

        // Button listeners
        logoutBtn.setOnClickListener(v -> new LogOutHelper(this).showLogoutConfirmation());
        preBookBtn.setOnClickListener(v -> new PreBookForm(this, studentName, studentId).showPreBookFormDialog());
        checkVisitorBtn.setOnClickListener(v -> new CheckPreBookedVisitorsList(this, studentId).showCheckPreBookedVisitorsListDialog());
        checkNewVisitorBtn.setOnClickListener(v -> new CheckNotifiedVisitorsList(this, studentId).showCheckNotifiedVisitorsListDialog());
        campusInOutBtn.setOnClickListener(v -> new CampusInOutForm(this, studentName, studentId).showCampusInOutDialog());
        // alertAllGaurdsBtn.setOnClickListener(v -> new AlertAllGaurds(this, studentId).showAlertAllGaurdsDialog());
    }
}
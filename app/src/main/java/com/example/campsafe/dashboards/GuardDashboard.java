package com.example.campsafe.dashboards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.campsafe.R;
import com.example.campsafe.newVisitorActivities.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class GuardDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guarddashboard); // Load layout for guard dashboard

        // Get references to UI elements
        TabLayout tab = findViewById(R.id.tab);              // Tab layout (top row of tabs)
        ViewPager2 page = findViewById(R.id.viewp);          // ViewPager2 (swipable pages below tabs)
        Button logout = findViewById(R.id.logout);           // Logout button

        // Setup ViewPager2 with a custom adapter (ViewPagerAdapter.java controls what each page contains)
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        page.setAdapter(adapter);

        // Connect TabLayout and ViewPager2
        new TabLayoutMediator(tab, page, (tabItem, position) -> {
            tabItem.setText(adapter.getTitlePage(position)); // Set tab title based on adapter
        }).attach();

        // Set up logout button behavior
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show confirmation dialog before logging out
                new AlertDialog.Builder(GuardDashboard.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Clear login state from SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("GuardPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", false); // Mark as logged out
                            editor.apply();

                            // Close dashboard (could be redirected to login screen if needed)
                            finish();
                        })
                        .setNegativeButton("No", null) // Do nothing if "No" is clicked
                        .show();
            }
        });
    }
}

package com.example.campsafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class guarddashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guarddashboard);
        TabLayout tab = findViewById(R.id.tab);
        ViewPager2 page = findViewById(R.id.viewp);
        viewpageradapter adapter = new viewpageradapter(this);
        page.setAdapter(adapter);

        // Attach TabLayout and ViewPager2 using TabLayoutMediator
        new TabLayoutMediator(tab, page, (t, position) -> {
            // Set the title of the tab based on the position
            t.setText(adapter.getTitlePage(position));
        }).attach();

        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          new AlertDialog.Builder(guarddashboard.this)
                                                  .setTitle("Logout")
                                                  .setMessage("Are you sure you want to log out?")
                                                  .setPositiveButton("Yes", (dialog, which) -> {
                                                      // Handle the logout process
                                                      SharedPreferences sharedPreferences = getSharedPreferences("GuardPrefs", MODE_PRIVATE);
                                                      SharedPreferences.Editor editor = sharedPreferences.edit();
                                                      editor.putBoolean("isLoggedIn", false); // Set login state to false
                                                      editor.apply();
                                                      // Optionally, you can finish the activity or redirect to a login screen
                                                      finish();
                                                  })
                                                  .setNegativeButton("No", null) // Dismisses the dialog if "No" is clicked
                                                  .show(); // Don't forget to call show() to display the dialog



                                      }
                                  }
        );



    }
}




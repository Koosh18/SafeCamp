package com.example.campsafe.logins;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class LogOutHelper {
    private final Context context; // Add context field

    // Constructor to initialize context
    public LogOutHelper(Context context) {
        this.context = context;
    }

    public void showLogoutConfirmation() { // Make public to be callable
        new AlertDialog.Builder(context)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    clearLoginFlags();
                    Intent loginIntent = new Intent(context, LoginPage.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(loginIntent); // Use context.startActivity
                    // Remove finish() as this is not an Activity
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void clearLoginFlags() {
        String[] prefNames = {"StudentPrefs", "FacultyPrefs", "GuardPrefs"};
        for (String prefName : prefNames) {
            SharedPreferences sp = context.getSharedPreferences(prefName, Context.MODE_PRIVATE); // Use context
            if (sp.getBoolean("isLoggedIn", false)) {
                sp.edit().putBoolean("isLoggedIn", false).apply();
                Log.i("LogOutHelper", "Cleared isLoggedIn for " + prefName); // Update log tag
            }
        }
    }
}
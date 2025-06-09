package com.example.campsafe.logins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.example.campsafe.dashboards.GuardDashboard;
import com.example.campsafe.dashboards.StudentDashboard;
import com.example.campsafe.dbModels.GuardDB;
import com.example.campsafe.dbModels.StudentDB;

public class LoginHelper {

    public interface LoginCallback {
        void onSuccess(int id, String name);
        void onFailure();
        void onRegister(int id, String name);
        void onError(Exception e);
    }

    public static void validateUser(
            Context context,
            String role, // "student" or "guard"
            int id,
            String name,
            String password,
            LoginCallback callback
    ) {
        if (role.equals("student")) {
            StudentDB db = new StudentDB(context);
            db.validateStudent(id, name, password, new StudentDB.StudentValidationCallback() {
                @Override
                public void onValidationResult(int result) {
                    switch (result) {
                        case 1:
                            callback.onSuccess(id, name);
                            break;
                        case 0:
                            callback.onFailure();
                            break;
                        case 2:
                            callback.onRegister(id, name);
                            break;
                    }


                }

                @Override
                public void onValidationError(Exception e) {
                    callback.onError(e);
                }
            });
        } else if (role.equals("guard")) {
            GuardDB db = new GuardDB(context);
            db.validateGuard(id, name, password, new GuardDB.GuardValidationCallback() {
                @Override
                public void onValidationResult(int result) {
                    switch (result) {
                        case 1:
                            callback.onSuccess(id, name);
                            break;
                        case 0:
                            callback.onFailure();
                            break;
                        case 2:
                            callback.onRegister(id, name);
                            break;
                    }

                }

                @Override
                public void onValidationError(Exception e) {
                    callback.onError(e);
                }
            });
        }
    }

    public static void saveLoginState(Context context, String role, int id, String name) {
        SharedPreferences prefs = context.getSharedPreferences(
                role.equals("student") ? "StudentPrefs" : "GuardPrefs", Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putInt(role + "ID", id);
        editor.putString(role + "Name", name);
        editor.apply();
    }

    public static void goToDashboard(Context context, String role, int id, String name) {
        Intent intent = new Intent(context,
                role.equals("student") ? StudentDashboard.class : GuardDashboard.class);
        intent.putExtra("name", name);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }
}

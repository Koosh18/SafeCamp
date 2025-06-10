package com.example.campsafe.logins;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.campsafe.dashboards.GuardDashboard;
import com.example.campsafe.dashboards.StudentDashboard;
import com.example.campsafe.dbModels.FacultyDB;
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
            String role,
            int id,
            String name,
            String password,
            LoginCallback callback
    ) {
        if (role.equals("Student")) {
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
        } else if (role.equals("Guard")) {
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
                role.equals("Student") ? "StudentPrefs" : "GuardPrefs", Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putInt(role + "ID", id);
        editor.putString(role + "Name", name);
        editor.apply();
    }

    public static void goToDashboard(Context context, String role, int id, String name) {
        Intent intent = new Intent(context,
                role.equals("Student") ? StudentDashboard.class : GuardDashboard.class);
        intent.putExtra("name", name);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    public static boolean isLoggedIn(Context context, String role, String nameKey, String idKey, Class<?> dashboard) {
        SharedPreferences prefs = context.getSharedPreferences(role + "Prefs", Context.MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            Intent i = new Intent(context, dashboard);
            i.putExtra("name", prefs.getString(nameKey, role));
            i.putExtra("id", prefs.getInt(idKey, 10));
            context.startActivity(i);
            if(context instanceof Activity){
                ((Activity) context).finish();
            }
            return true;
        }
        return false;
    }

    public static void handleLogin(Context context,String role, String name, String idStr, String pass) {


        if (name.isEmpty() || idStr.isEmpty() || pass.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            Toast.makeText(context, "Invalid ID format", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginHelper.validateUser(context, role, id, name, pass, new LoginHelper.LoginCallback() {
            @Override
            public void onSuccess(int id, String name) {
                LoginHelper.saveLoginState(context, role, id, name);
                LoginHelper.goToDashboard(context, role, id, name);
            }

            @Override
            public void onFailure() {
                Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRegister(int id, String name) {

                if (role.equals("Faculty"))
                    new FacultyDB(context).add(id, name, pass);
                else if (role.equals("Guard"))
                    new GuardDB(context).add(id, name, pass);
                else
                    new StudentDB(context).add(id, name, pass);

                Toast.makeText(context, "New "+role+" Registered", Toast.LENGTH_SHORT).show();
                LoginHelper.saveLoginState(context, role, id, name);
                LoginHelper.goToDashboard(context, role, id, name);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

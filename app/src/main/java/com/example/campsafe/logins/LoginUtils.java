package com.example.campsafe.logins;
import static java.security.AccessController.getContext;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.example.campsafe.dbModels.StudentDB;
import com.example.campsafe.dbModels.GuardDB;
import com.example.campsafe.dbModels.FacultyDB;

public class LoginUtils {
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

                if (role.equals("faculty"))
                    new FacultyDB(context).add(id, name, pass);
                else if (role.equals("guard"))
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

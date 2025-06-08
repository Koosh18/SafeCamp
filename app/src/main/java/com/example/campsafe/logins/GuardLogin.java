package com.example.campsafe.logins;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.campsafe.R;
import com.example.campsafe.dbModels.GuardDB;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GuardLogin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuardLogin extends Fragment {

    private EditText nameInput, idInput, passInput;

    public GuardLogin() {}

    public static GuardLogin newInstance(String param1, String param2) {
        return new GuardLogin();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_guard_login, container, false);

        nameInput = rootview.findViewById(R.id.edit_name);
        idInput = rootview.findViewById(R.id.edit_id);
        passInput = rootview.findViewById(R.id.password);
        Button submit = rootview.findViewById(R.id.button_login);

        submit.setOnClickListener(v -> handleLogin("guard"));

        return rootview;
    }

    private void handleLogin(String role) {
        String name = nameInput.getText().toString().trim();
        String idStr = idInput.getText().toString().trim();
        String pass = passInput.getText().toString().trim();

        if (name.isEmpty() || idStr.isEmpty() || pass.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid ID format", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginHelper.validateUser(getContext(), role, id, name, pass, new LoginHelper.LoginCallback() {
            @Override
            public void onSuccess(int id, String name) {
                LoginHelper.saveLoginState(getContext(), role, id, name);
                LoginHelper.goToDashboard(getContext(), role, id, name);
            }

            @Override
            public void onFailure() {
                Toast.makeText(getContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRegister(int id, String name) {
                new GuardDB(getContext()).add(id, name, pass);
                Toast.makeText(getContext(), "New Guard Registered", Toast.LENGTH_SHORT).show();
                LoginHelper.saveLoginState(getContext(), role, id, name);
                LoginHelper.goToDashboard(getContext(), role, id, name);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
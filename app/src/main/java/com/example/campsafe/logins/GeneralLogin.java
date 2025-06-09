package com.example.campsafe.logins;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.campsafe.R;

public class GeneralLogin extends Fragment {

    private EditText nameInput, idInput, passwordInput;
    private static final String ARG_ROLE = "role";
    private String role;

    public GeneralLogin() {}

    public static GeneralLogin newInstance(String role) {
        GeneralLogin fragment = new GeneralLogin();
        Bundle args = new Bundle();
        args.putString(ARG_ROLE, role);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            role = getArguments().getString(ARG_ROLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_general_login, container, false);

        nameInput = rootview.findViewById(R.id.edit_name);
        idInput = rootview.findViewById(R.id.edit_id);
        passwordInput = rootview.findViewById(R.id.password);

        TextView roleText = rootview.findViewById(R.id.role_text);
        String headingMessage= role + " Login";
        roleText.setText(headingMessage);

        Button submit = rootview.findViewById(R.id.button_login);

        submit.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String idStr = idInput.getText().toString().trim();
            String pass = passwordInput.getText().toString().trim();
            LoginUtils.handleLogin(requireContext(), role, name, idStr, pass);
        });

        return rootview;
    }
}

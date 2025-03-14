package com.example.campsafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link student_login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class student_login extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public student_login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment guard_login.
     */
    // TODO: Rename and change types and number of parameters
    public static guard_login newInstance(String param1, String param2) {
        guard_login fragment = new guard_login();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void saveLoginState(int id, String name) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("StudentPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true); // User is logged in
        editor.putInt("studentID", id);         // Save guard ID
        editor.putString("studentName", name); // Save guard name
        editor.apply(); // Commit changes
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }






    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =  inflater.inflate(R.layout.fragment_student_login, container, false);
        EditText nameInput = rootview.findViewById(R.id.edit_name);
        EditText idInput = rootview.findViewById(R.id.edit_id);
        EditText passinp = rootview.findViewById(R.id.password) ;

        Button submit = rootview.findViewById(R.id.button_login) ;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameInput.getText().toString().trim();
                String id = (idInput.getText().toString());
                String pass = passinp.getText().toString().trim();

                if (name.isEmpty() || id.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    int idd = Integer.parseInt(id);
                    studentdb dbhelp = new studentdb(getContext());
                    dbhelp.validateStudent(idd, name, pass, new studentdb.StudentValidationCallback() {
                        @Override
                        public void onValidationResult(int result) {
                            if (result==1) {
                                saveLoginState(idd,name);
                                Intent istudent = new Intent(getContext(), studentdashboard.class);
                                istudent.putExtra("name",name);
                                istudent.putExtra("id",idd) ;
                                startActivity(istudent) ;
                            }
                            else if (result==0) {
                                Toast.makeText(getContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                dbhelp.add(idd, name, pass);
                                Toast.makeText(getContext(), "New Student registered", Toast.LENGTH_SHORT).show();
                                saveLoginState(idd,name);
                                Intent istudent = new Intent(getContext(), studentdashboard.class);
                                istudent.putExtra("name",name);
                                istudent.putExtra("id",idd) ;
                                startActivity(istudent);
                            }
                        }

                        @Override
                        public void onValidationError(Exception e) {

                        }
                    });
                }
            }
        });

        return rootview ;

    }
}


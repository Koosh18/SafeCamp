package com.example.campsafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link faculty_login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class faculty_login extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public faculty_login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment faculty_login.
     */
    // TODO: Rename and change types and number of parameters
    public static faculty_login newInstance(String param1, String param2) {
        faculty_login fragment = new faculty_login();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }






    }
    private void saveLoginState(int id, String name) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("FacultyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true); // User is logged in
        editor.putInt("facultyID", id);         // Save guard ID
        editor.putString("facultyName", name); // Save guard name
        editor.apply(); // Commit changes
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =  inflater.inflate(R.layout.fragment_faculty_login, container, false);
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
                    facultydb dbhelp = new facultydb(getContext());
                    dbhelp.validateFaculty(idd, name, pass, new facultydb.FacultyValidationCallback() {
                        @Override
                        public void onValidationResult(int result) {
                            if (result==1) {
                                saveLoginState(idd,name);
                                Intent ifaculty = new Intent(getContext(), facultydashboard.class);
                                ifaculty.putExtra("name",name) ;
                                startActivity(ifaculty) ;
                            }
                            else if (result==0) {
                                Toast.makeText(getContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                dbhelp.add(idd, name, pass);
                                Toast.makeText(getContext(), "New Faculty registered", Toast.LENGTH_SHORT).show();
                                saveLoginState(idd,name);
                                Intent ifaculty = new Intent(getContext(), studentdashboard.class);
                                ifaculty.putExtra("name",name) ;
                                startActivity(ifaculty);
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
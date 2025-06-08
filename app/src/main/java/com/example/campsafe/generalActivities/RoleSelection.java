package com.example.campsafe.generalActivities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.campsafe.R;
import com.example.campsafe.logins.FacultyLogin;
import com.example.campsafe.logins.GuardLogin;
import com.example.campsafe.logins.StudentLogin;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoleSelection#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoleSelection extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RoleSelection() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RoleSelection.
     */
    // TODO: Rename and change types and number of parameters
    public static RoleSelection newInstance(String param1, String param2) {
        RoleSelection fragment = new RoleSelection();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_role_selection, container, false);
        Button btn_faculty = view.findViewById(R.id.button_faculty);
        Button btn_student = view.findViewById(R.id.button_student) ;
        Button btn_guard = view.findViewById(R.id.button_guard) ;
        btn_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadFragment(new FacultyLogin());

            }

        });

        btn_guard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new GuardLogin());
            }
        });

        btn_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new StudentLogin());
            }
        });

        return view ;

    }
    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getActivity().getSupportFragmentManager() ;
        FragmentTransaction ft = fm.beginTransaction() ;
        ft.replace(R.id.frag,fragment) ;
        ft.commit() ;



    }
}
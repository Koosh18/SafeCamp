package com.example.campsafe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link pre_bookings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class pre_bookings extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView notifiedList;
    private VisitorAdapter adapter;
    private ArrayList<Map<String, Object>> visitorsList = new ArrayList<>();
    private prebook_db prebookDb;

    public pre_bookings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment pre_bookings.
     */
    // TODO: Rename and change types and number of parameters
    public static pre_bookings newInstance(String param1, String param2) {
        pre_bookings fragment = new pre_bookings();
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
        View Rootview = inflater.inflate(R.layout.fragment_notified, container, false);
        notifiedList = Rootview.findViewById(R.id.notified_list);

        prebookDb = new prebook_db();
        loadRecentVisitors();
        adapter = new VisitorAdapter(Rootview.getContext(), visitorsList);
        notifiedList.setAdapter(adapter);
        return  Rootview ;
    }

    private void loadRecentVisitors() {
        prebookDb.getPrebookedVisitors(new prebook_db.VisitorCallback() {
            @Override
            public void onSuccess(ArrayList<Map<String, Object>> visitors) {
                visitorsList.clear();
                visitorsList.addAll(visitors);
                adapter.notifyDataSetChanged();
                Log.d("Firestore Success", "Prebooked visitors list updated with " + visitors.size() + " entries.");
            }




        @Override
            public void onFailure(Exception e) {
                Log.e("fail","fail ho gya ") ;

                e.printStackTrace();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        loadRecentVisitors();  // Refresh list when fragment is resumed
    }



}
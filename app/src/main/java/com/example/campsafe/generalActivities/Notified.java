package com.example.campsafe.generalActivities;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.campsafe.R;
import com.example.campsafe.dbModels.NewVisitorDB;
import com.example.campsafe.newVisitorActivities.VisitorAdapter;

import java.util.ArrayList;
import java.util.Map;

public class Notified extends Fragment {

    private ListView notifiedList;
    private VisitorAdapter adapter;
    private ArrayList<Map<String, Object>> visitorsList = new ArrayList<>();
    private NewVisitorDB visitorDb;

    public Notified() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notified, container, false);
        notifiedList = rootView.findViewById(R.id.notified_list);

        visitorDb = new NewVisitorDB();
        loadRecentVisitors();
        adapter = new VisitorAdapter(rootView.getContext(), visitorsList);
        notifiedList.setAdapter(adapter);



        return rootView;
    }

    private void loadRecentVisitors() {
        visitorDb.getRecentVisitors(new NewVisitorDB.VisitorCallback() {
            @Override
            public void onSuccess(ArrayList<Map<String, Object>> visitors) {
                visitorsList.clear();
                visitorsList.addAll(visitors);
                adapter.notifyDataSetChanged();
                Log.e("notifiedd", "onSuccess: hello");
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

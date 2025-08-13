package com.example.campsafe.guardDashboard;
import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.campsafe.R;
import com.example.campsafe.dbModels.CampusInOutDB;

import java.util.ArrayList;
import java.util.Map;

public class CampusInOutList extends Fragment {
    private ListView notifiedList;
    private CampusInOutAdapter adapter;
    private ArrayList<Map<String, Object>> visitorsList = new ArrayList<>();
    private CampusInOutDB campusInOutDB;

    private final Context context;
    public CampusInOutList(Context context) {
        this.context = context;
    }

    public void showCampusInOutListDialog(){

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.fragment_guard_dashboard_lists);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9),
                    (int) (context.getResources().getDisplayMetrics().heightPixels * 0.9)
            );
        }
        dialog.show();

        notifiedList = dialog.findViewById(R.id.notified_list);
        Button cancel = dialog.findViewById(R.id.cancelVisitorButton);
        TextView heading = dialog.findViewById(R.id.fragment_heading);
        heading.setText("CampusInOut Records List");

        campusInOutDB = new CampusInOutDB();
        adapter = new CampusInOutAdapter(dialog.getContext(), visitorsList);
        notifiedList.setAdapter(adapter);
        loadRecentVisitors();

        cancel.setOnClickListener(v -> dialog.dismiss());
    }
    private void loadRecentVisitors() {
        campusInOutDB.getCampusInOutRecords(new CampusInOutDB.VisitorCallback() {
            @Override
            public void onSuccess(ArrayList<Map<String, Object>> visitors) {
                visitorsList.clear();
                visitorsList.addAll(visitors);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Error loading CampusInOut records", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecentVisitors();
    }
}
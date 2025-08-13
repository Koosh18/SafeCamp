package com.example.campsafe.studentDashboard;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.campsafe.R;
import com.example.campsafe.dbModels.PreBookDB;
import com.example.campsafe.guardDashboard.VisitorAdapter;

import java.util.ArrayList;
import java.util.Map;

public class CheckPreBookedVisitorsList extends Fragment {
    private ListView notifiedList;
    private VisitorAdapter adapter;
    private ArrayList<Map<String, Object>> visitorsList = new ArrayList<>();
    private PreBookDB prebookDb;

    private Context context;
    private int studentId;
    public CheckPreBookedVisitorsList(Context context, int studentId) {
        this.context = context;
        this.studentId=studentId;
    }

    public void showCheckPreBookedVisitorsListDialog(){

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
        heading.setText("Your Pre-Bookings History");

        prebookDb = new PreBookDB();
        adapter = new VisitorAdapter(dialog.getContext(), visitorsList);
        notifiedList.setAdapter(adapter);
        loadRecentVisitors();

        cancel.setOnClickListener(v -> dialog.dismiss());
    }
    private void loadRecentVisitors() {
        prebookDb.getPrebookedVisitorsOfStudent(studentId,new PreBookDB.VisitorCallback() {
            @Override
            public void onSuccess(ArrayList<Map<String, Object>> visitors) {
                visitorsList.clear();
                visitorsList.addAll(visitors);
                adapter.notifyDataSetChanged();
                Log.d("PreBookingsFragment", "Prebooked visitors updated with " + visitors.size() + " entries");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("PreBookingsFragment", "Failed to load prebooked visitors", e);
                Toast.makeText(getContext(), "Error loading prebooked visitors", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecentVisitors();
    }
}
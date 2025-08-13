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
import com.example.campsafe.dbModels.NewVisitorDB;
import com.example.campsafe.guardDashboard.VisitorAdapter;
import java.util.ArrayList;
import java.util.Map;

public class CheckNotifiedVisitorsList extends Fragment {
    private ListView notifiedList;
    private VisitorAdapter adapter;
    private ArrayList<Map<String, Object>> visitorsList = new ArrayList<>();
    private NewVisitorDB newVisitorDB;
    private int studentId;

    private final Context context;
    public CheckNotifiedVisitorsList(Context context, int studentId) {
        this.context = context;
        this.studentId=studentId;
    }

    public void showCheckNotifiedVisitorsListDialog(){
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
        heading.setText("Your New Visitors History");

        newVisitorDB=new NewVisitorDB();

        adapter = new VisitorAdapter(dialog.getContext(), visitorsList);
        notifiedList.setAdapter(adapter);

        loadRecentVisitors();

        cancel.setOnClickListener(v -> dialog.dismiss());
    }
    private void loadRecentVisitors() {
        if ( newVisitorDB == null) {
            Log.e("RecentVisitorsFragment", "RecentVisitorsDB is not initialized");
            Toast.makeText(context, "Error: Database not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        if (studentId <= 0) { // Assuming valid studentId is positive
            Log.e("RecentVisitorsFragment", "Invalid student ID: " + studentId);
            Toast.makeText(context, "Error: Invalid student ID", Toast.LENGTH_SHORT).show();
            return;
        }

        newVisitorDB.getRecentVisitorsOfStudent(studentId,new NewVisitorDB.VisitorCallback() {
            @Override
            public void onSuccess(ArrayList<Map<String, Object>> visitors) {
                visitorsList.clear();
                visitorsList.addAll(visitors);
                adapter.notifyDataSetChanged();
                Log.d("RecentVisitorsFragment", "RecentVisitors updated with " + visitors.size() + " entries");
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("PreBookingsFragment", "Failed to load prebooked visitors", e);
                Toast.makeText(context, "Error loading prebooked visitors", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRecentVisitors();
    }
}
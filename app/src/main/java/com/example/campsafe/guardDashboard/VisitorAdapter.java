package com.example.campsafe.guardDashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.example.campsafe.R;
import java.util.ArrayList;
import java.util.Map;

public class VisitorAdapter extends ArrayAdapter<Map<String, Object>> {
    private final Context context;
    private final ArrayList<Map<String, Object>> visitors;

    public VisitorAdapter(Context context, ArrayList<Map<String, Object>> visitors) {
        super(context, 0, visitors);
        this.context = context;
        this.visitors = visitors;
    }
    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.notified_box, parent, false);
        }

        Map<String, Object> visitor = visitors.get(position);

        TextView visitorName = convertView.findViewById(R.id.visitor_name);
        TextView personName = convertView.findViewById(R.id.person_name);
        TextView visitorId = convertView.findViewById(R.id.visitor_id);
        TextView visitDatetime = convertView.findViewById(R.id.visit_datetime);
        TextView approvalStatus = convertView.findViewById(R.id.approval_status);
        CardView cardView = (CardView) convertView;

        // Set data with null checks
        visitorName.setText("Visitor: " + (visitor.get("visitor_name") != null ? visitor.get("visitor_name") : "Unknown"));
        personName.setText("Student/Faculty: " + (visitor.get("person_name") != null ? visitor.get("person_name") : "Unknown"));
        visitorId.setText("ID: " + (visitor.get("person_id") != null ? visitor.get("person_id") : "N/A"));
        String date = visitor.get("visit_date") != null ? visitor.get("visit_date").toString() : "";
        String time = visitor.get("visit_time") != null ? visitor.get("visit_time").toString() : "";
        visitDatetime.setText("Visit: " + date + " " + time);

        Boolean approved = (Boolean) visitor.get("approved");
        if (approved != null) {
            if (approved) {
                approvalStatus.setText("Approved ✅");
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green));
            } else {
                approvalStatus.setText("Rejected ❌");
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red));
            }
        } else {
            approvalStatus.setText("Pending ⏳");
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.grey));
        }

        return convertView;
    }
}
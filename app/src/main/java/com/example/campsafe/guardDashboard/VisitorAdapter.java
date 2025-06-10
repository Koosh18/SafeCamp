package com.example.campsafe.guardDashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.widget.ArrayAdapter;

import com.example.campsafe.R;

import java.util.ArrayList;
import java.util.Map;

public class VisitorAdapter extends ArrayAdapter<Map<String, Object>> {

    private Context context;
    private ArrayList<Map<String, Object>> visitors;

    public VisitorAdapter(Context context, ArrayList<Map<String, Object>> visitors) {
        super(context, 0, visitors);
        this.context = context;
        this.visitors = visitors;
    }

    @SuppressLint("ResourceAsColor")
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

        // Set data
        visitorName.setText("Visitor: " + visitor.get("visitor_name"));
        personName.setText("Student/Faculty: " + visitor.get("person_name"));
        visitorId.setText("ID: " + visitor.get("person_id"));
        visitDatetime.setText("Visit: " + visitor.get("visit_date") + " " + visitor.get("visit_time"));

        // Check approval status
        Boolean approved = (Boolean) visitor.get("approved");
        if (approved != null) {
            if (approved) {
                approvalStatus.setText("Approved ✅");
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green));

                cardView.setCardBackgroundColor(R.color.green);
            } else  {

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

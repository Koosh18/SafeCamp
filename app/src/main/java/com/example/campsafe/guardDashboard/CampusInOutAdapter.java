package com.example.campsafe.guardDashboard;

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

public class CampusInOutAdapter extends ArrayAdapter<Map<String, Object>> {
    private final Context context;
    private final ArrayList<Map<String, Object>> records;

    public CampusInOutAdapter(Context context, ArrayList<Map<String, Object>> records) {
        super(context, 0, records);
        this.context = context;
        this.records = records;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_campusinout_notificationbox, parent, false);
        }

        Map<String, Object> record = records.get(position);

        TextView studentName = convertView.findViewById(R.id.student_name);
        TextView studentId = convertView.findViewById(R.id.student_id);
        TextView purpose = convertView.findViewById(R.id.purpose);
        TextView outDatetime = convertView.findViewById(R.id.out_datetime);
        TextView inDatetime = convertView.findViewById(R.id.in_datetime);
        CardView cardView = (CardView) convertView;

        // Set data with null checks
        studentName.setText("Student: " + (record.get("student_name") != null ? record.get("student_name") : "Unknown"));
        studentId.setText("ID: " + (record.get("student_id") != null ? record.get("student_id") : "N/A"));
        purpose.setText("Purpose: " + (record.get("purpose") != null ? record.get("purpose") : "N/A"));
        String outDate = record.get("out_date") != null ? record.get("out_date").toString() : "";
        String outTime = record.get("out_time") != null ? record.get("out_time").toString() : "";
        outDatetime.setText("Out: " + outDate + " " + outTime);
        String inDate = record.get("in_date") != null ? record.get("in_date").toString() : "";
        String inTime = record.get("in_time") != null ? record.get("in_time").toString() : "";
        inDatetime.setText("In: " + inDate + " " + inTime);

        // Set default background (no approval status in CampusInOut)
        cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.grey));

        return convertView;
    }
}
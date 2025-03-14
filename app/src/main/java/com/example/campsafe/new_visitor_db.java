package com.example.campsafe;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class new_visitor_db {
    FirebaseFirestore db ;

    public new_visitor_db() {
        db = FirebaseFirestore.getInstance() ;

    }

    public void insertData(String visitorName, String numVisitors, String date, String time, String personName, String personId, String reason, OnCompleteListener<DocumentReference> listener) {
        Map<String, Object> visitorData = new HashMap<>();
        visitorData.put("visitor_name", visitorName);
        visitorData.put("num_visitors", numVisitors);
        visitorData.put("visit_date", date);
        visitorData.put("visit_time", time);
        visitorData.put("person_name", personName);
        visitorData.put("person_id", personId);
        visitorData.put("reason", reason);
        visitorData.put("approved", null);

        db.collection("new_visitor")
                .add(visitorData)
                .addOnCompleteListener(listener);

    }
    public void getRecentVisitors(VisitorCallback callback) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        String thirtyDaysAgo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        db.collection("new_visitor")
                .whereGreaterThanOrEqualTo("visit_date", thirtyDaysAgo) // Query by date
                .orderBy("visit_date", Query.Direction.DESCENDING) // Sort by date (newest first)
                .orderBy("visit_time", Query.Direction.DESCENDING) // Sort by time (newest first)
                .get()

                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Map<String, Object>> visitorList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String dateStr = document.getString("visit_date");
                            String timeStr = document.getString("visit_time");

                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                Date visitDate = sdf.parse(dateStr + " " + timeStr);

                                if (visitDate != null) {
                                    Log.e("debug","adding") ;

                                    Map<String, Object> visitorData = new HashMap<>();
                                    visitorData.put("visitor_name", document.getString("visitor_name"));
                                    visitorData.put("person_name", document.getString("person_name"));
                                    visitorData.put("person_id", document.getString("person_id"));
                                    visitorData.put("visit_date", dateStr);
                                    visitorData.put("visit_time", timeStr);
                                    visitorData.put("approved", document.getBoolean("approved"));
                                    visitorList.add(visitorData);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        callback.onSuccess(visitorList);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }


    public interface VisitorCallback {
            void onSuccess(ArrayList<Map<String, Object>> visitors);
            void onFailure(Exception e);
        }



}

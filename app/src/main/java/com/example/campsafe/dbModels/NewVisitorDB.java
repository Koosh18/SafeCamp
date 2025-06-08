package com.example.campsafe.dbModels;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewVisitorDB {
    FirebaseFirestore db ;

    public NewVisitorDB() {
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

        db.collection("NewVisitor")
                .add(visitorData)
                .addOnCompleteListener(listener);

    }
    public void getRecentVisitors(VisitorCallback callback) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        String thirtyDaysAgo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        db.collection("NewVisitor")
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

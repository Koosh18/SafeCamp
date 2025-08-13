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


public class CampusInOutDB  {
    FirebaseFirestore db ;

    public CampusInOutDB () {
        db = FirebaseFirestore.getInstance() ;
    }
    public void insertData(Integer stud_id, String stud_name, String purpose, String out_date, String out_time, String in_date, String in_time, OnCompleteListener<DocumentReference> listener) {
        // Create a map for the data
        Map<String, Object> CampusInOutData = new HashMap<>();
        CampusInOutData.put("student_id", stud_id);
        CampusInOutData.put("student_name", stud_name);
        CampusInOutData.put("purpose", purpose);
        CampusInOutData.put("out_date", out_date);
        CampusInOutData.put("out_time", out_time);
        CampusInOutData.put("in_date", in_date);
        CampusInOutData.put("in_time", in_time);
        // Add the data to the Firestore collection
        db.collection("CampusInOut")
                .add(CampusInOutData)
                .addOnCompleteListener(listener);
    }
    public void getCampusInOutRecords(VisitorCallback callback) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        String thirtyDaysAgo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        db.collection("CampusInOut")
                .whereGreaterThanOrEqualTo("out_date", thirtyDaysAgo)
                .orderBy("out_date", Query.Direction.DESCENDING)
                .orderBy("out_time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Map<String, Object>> visitorList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String outDateStr = document.getString("out_date");
                            String outTimeStr = document.getString("out_time");

                            if (outDateStr == null || outTimeStr == null) {
                                Log.e("Firestore Error", "Missing out_date/out_time for document ID: " + document.getId());
                                continue;
                            }

                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                Date outDate = sdf.parse(outDateStr + " " + outTimeStr);

                                if (outDate != null) {
                                    Log.d("Firestore Debug", "Adding campus in/out record: " + document.getString("student_name"));

                                    Map<String, Object> visitorData = new HashMap<>();
                                    visitorData.put("student_id", document.get("student_id"));
                                    visitorData.put("student_name", document.getString("student_name"));
                                    visitorData.put("purpose", document.getString("purpose"));
                                    visitorData.put("out_date", outDateStr);
                                    visitorData.put("out_time", outTimeStr);
                                    visitorData.put("in_date", document.getString("in_date"));
                                    visitorData.put("in_time", document.getString("in_time"));

                                    visitorList.add(visitorData);
                                }
                            } catch (Exception e) {
                                Log.e("Parse Error", "Failed to parse out_date/out_time: " + outDateStr + " " + outTimeStr, e);
                            }
                        }
                        callback.onSuccess(visitorList);
                    } else {
                        Log.e("Firestore Error", "Failed to fetch campus in/out records: " + task.getException().getMessage(), task.getException());
                        callback.onFailure(task.getException());
                    }
                });
    }

    public interface VisitorCallback {
        void onSuccess(ArrayList<Map<String, Object>> visitors);
        void onFailure(Exception e);
    }
}



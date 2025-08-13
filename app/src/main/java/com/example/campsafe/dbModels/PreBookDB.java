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

public class PreBookDB {
    FirebaseFirestore db ;

    public PreBookDB() {
        db = FirebaseFirestore.getInstance() ;
    }
    public void insertData(String name, String num, String date, String time, String stud_name, Integer stud_id,  OnCompleteListener<DocumentReference> listener) {
        // Create a map for the data
        Map<String, Object> prebookData = new HashMap<>();
        prebookData.put("visitor_name", name);
        prebookData.put("num_visitors", num);
        prebookData.put("visit_date", date);
        prebookData.put("visit_time", time);
        prebookData.put("student_name",stud_name) ;
        prebookData.put("student_id",stud_id) ;
        // Add the data to the Firestore collection
        db.collection("prebook")
                .add(prebookData)
                .addOnCompleteListener(listener);
    }
    public void getPrebookedVisitors(VisitorCallback callback) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        String thirtyDaysAgo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        db.collection("prebook")
                .whereGreaterThanOrEqualTo("visit_date", thirtyDaysAgo)
                .orderBy("visit_date", Query.Direction.DESCENDING)
                .orderBy("visit_time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Map<String, Object>> visitorList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String dateStr = document.getString("visit_date");
                            String timeStr = document.getString("visit_time");

                            if (dateStr == null || timeStr == null) {
                                Log.e("Firestore Error", "Missing date/time for document ID: " + document.getId());
                                continue;
                            }

                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                Date visitDate = sdf.parse(dateStr + " " + timeStr);



                                if (visitDate != null) {
                                    Log.d("Firestore Debug", "Adding prebooked visitor: " + document.getString("visitor_name"));

                                    Map<String, Object> visitorData = new HashMap<>();
                                    visitorData.put("visitor_name", document.getString("visitor_name"));
                                    visitorData.put("person_name", document.getString("student_name"));
                                    Log.e("stud",document.getString("student_name")) ;
                                    visitorData.put("person_id",document.get("student_id")) ;
                                    visitorData.put("num_visitors", document.getString("num_visitors"));
                                    visitorData.put("visit_date", dateStr);
                                    visitorData.put("visit_time", timeStr);
                                    visitorData.put("approved", true);

                                    visitorList.add(visitorData);
                                }
                            } catch (Exception e) {
                                Log.e("Parse Error", "Failed to parse visit date/time: " + dateStr + " " + timeStr, e);
                            }
                        }
                        callback.onSuccess(visitorList);
                    } else {
                        Log.e("Firestore Error", "Failed to fetch prebooked visitors", task.getException());
                        callback.onFailure(task.getException());
                    }
                });
    }
    public void getPrebookedVisitorsOfStudent(int studentId, VisitorCallback callback) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        String thirtyDaysAgo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

        db.collection("prebook")
                .whereGreaterThanOrEqualTo("visit_date", thirtyDaysAgo)
                .whereEqualTo("student_id", studentId) // Use int directly
                .orderBy("visit_date", Query.Direction.DESCENDING)
                .orderBy("visit_time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Map<String, Object>> visitorList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String dateStr = document.getString("visit_date");
                            String timeStr = document.getString("visit_time");

                            if (dateStr == null || timeStr == null) {
                                Log.e("Firestore Error", "Missing date/time for document ID: " + document.getId());
                                continue;
                            }

                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                Date visitDate = sdf.parse(dateStr + " " + timeStr);
                                if (visitDate != null) {
                                    Log.d("Firestore Debug", "Adding prebooked visitor: " + document.getString("visitor_name"));
                                    Map<String, Object> visitorData = new HashMap<>();
                                    visitorData.put("visitor_name", document.getString("visitor_name"));
                                    visitorData.put("person_name", document.getString("student_name"));
                                    visitorData.put("person_id", document.get("student_id"));
                                    visitorData.put("num_visitors", document.getString("num_visitors"));
                                    visitorData.put("visit_date", dateStr);
                                    visitorData.put("visit_time", timeStr);
                                    visitorData.put("approved", true);
                                    visitorList.add(visitorData);
                                }
                            } catch (Exception e) {
                                Log.e("Parse Error", "Failed to parse visit date/time: " + dateStr + " " + timeStr, e);
                            }
                        }
                        callback.onSuccess(visitorList);
                    } else {
                        Log.e("Firestore Error", "Failed to fetch prebooked visitors", task.getException());
                        callback.onFailure(task.getException());
                    }
                });
    }


    public interface VisitorCallback {
        void onSuccess(ArrayList<Map<String, Object>> visitors);
        void onFailure(Exception e);
    }



}



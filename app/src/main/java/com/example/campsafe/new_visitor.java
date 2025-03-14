package com.example.campsafe;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.provider.DocumentsContract;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.Attributes;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link new_visitor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class new_visitor extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public new_visitor() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment new_visitor.
     */
    // TODO: Rename and change types and number of parameters

    private void sendNotification(String fcmToken, String visitorName, String visitReason) {
        String channelId = "visitor_alert_channel";
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Visitor Alerts", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Intent to stop the alarm
        Intent stopIntent = new Intent(getContext(), StopAlarmReceiver.class);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(
                getContext(), 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Visitor Alert")
                .setContentText(visitorName + " is here for " + visitReason)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_launcher_background, "Stop Alarm", stopPendingIntent);  // Stop button

        notificationManager.notify(1, builder.build());
    }


    public static new_visitor newInstance(String param1, String param2) {
        new_visitor fragment = new new_visitor();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View Rootview = inflater.inflate(R.layout.fragment_new_visitorfrag, container, false);
        EditText Name = Rootview.findViewById(R.id.visitorName) ;
        EditText numvisitors = Rootview.findViewById(R.id.numberOfVisitors) ;
        EditText reason = Rootview.findViewById(R.id.reasonForVisit) ;
        RadioGroup rad = Rootview.findViewById(R.id.visitorTypeGroup);
        AutoCompleteTextView auto = Rootview.findViewById(R.id.idNumber) ;
        Button submit = Rootview.findViewById(R.id.submitVisitorButton) ;

        RadioButton stud = Rootview.findViewById(R.id.radioStudent) ;
        RadioButton fac= Rootview.findViewById(R.id.radioFaculty) ;
        studentdb dbst = new studentdb(Rootview.getContext()) ;
        facultydb dbfa = new facultydb(Rootview.getContext()) ;



        ArrayAdapter<String> faculty_data = new ArrayAdapter<>(Rootview.getContext(), android.R.layout.simple_dropdown_item_1line) ;
        ArrayAdapter<String> student_data = new ArrayAdapter<>(Rootview.getContext(), android.R.layout.simple_dropdown_item_1line) ;
        ArrayAdapter<String> joint_data = new ArrayAdapter<>(Rootview.getContext(), android.R.layout.simple_dropdown_item_1line) ;


        dbst.getAllStudents(new studentdb.StudentListCallback() {
            @Override
            public void onStudentListReceived(List<studentdb.Student> studentList) {
                for (studentdb.Student stud : studentList) {
                    String name = stud.getName() ;
                    Integer id =  stud.getId() ;

                    String result = name + " - " + id ;
                    student_data.add(result) ;
                    joint_data.add(result);

                 }
            }

            @Override
            public void onStudentListError(Exception e) {

            }

        });

        dbfa.getAllFaculties(new facultydb.FacultyListCallback() {
            @Override
            public void onFacultyListReceived(List<facultydb.Faculty> facultyList) {
                for (facultydb.Faculty fac : facultyList) {
                    String name = fac.getName() ;
                    Integer id = fac.getId() ;

                    String result = name + " - " + id ;
                    faculty_data.add(result) ;
                    joint_data.add(result);


                }
            }

            @Override
            public void onFacultyListError(Exception e) {

            }
        });



auto.setAdapter(joint_data);
rad.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId==R.id.radioStudent) {
            auto.setAdapter(student_data);
        }
        else if (checkedId==R.id.radioFaculty) {
            auto.setAdapter(faculty_data);
        }
    }
});

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String visitorName = Name.getText().toString().trim();
                String numVisitors = numvisitors.getText().toString().trim();
                String visitReason = reason.getText().toString().trim();
                int selectedVisitorTypeId = rad.getCheckedRadioButtonId();
                String selectedPerson = auto.getText().toString().trim();

                // Validate input fields
                if (visitorName.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter visitor's name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (numVisitors.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter number of visitors", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (visitReason.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter reason for visit", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedPerson.isEmpty()) {
                    Toast.makeText(getContext(), "Please select a faculty or student", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Extract Name and ID from AutoCompleteTextView selection
                String selectedName = "";
                String selectedId = "";
                if (selectedPerson.contains(" - ")) {
                    String[] parts = selectedPerson.split(" - ");
                    selectedName = parts[0]; // Name
                    selectedId = parts[1];   // ID
                }

                // Get current date and time
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String currentDate = dateFormat.format(calendar.getTime());
                String currentTime = timeFormat.format(calendar.getTime());

                // Determine visitor type
                String visitorType = (selectedVisitorTypeId == R.id.radioStudent) ? "Student" : "Faculty";

                // Prepare data for database
                new_visitor_db newVisitorDb = new new_visitor_db();
                newVisitorDb.insertData(visitorName, numVisitors, currentDate, currentTime, selectedName, selectedId, visitReason,
                        new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Visitor entry recorded successfully!", Toast.LENGTH_SHORT).show();
                                    Name.setText(" " );
                                    numvisitors.setText(" ");
                                    reason.setText(" ");
                                    rad.clearCheck();
                                    auto.setText(" ");
                                    newVisitorDb.getRecentVisitors(new new_visitor_db.VisitorCallback() {
                                        @Override
                                        public void onSuccess(ArrayList<Map<String, Object>> visitors) {

                                        }

                                        @Override
                                        public void onFailure(Exception e) {

                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), "Error saving visitor entry!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String collection = (selectedVisitorTypeId == R.id.radioStudent) ? "Students" : "Faculty";

// Fetch FCM Token
                db.collection(collection).document(selectedId).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fcmToken = documentSnapshot.getString("fcm_token");
                        if (fcmToken != null && !fcmToken.isEmpty()) {
                            Log.e("notif","trying to send"+ fcmToken) ;
                            getContext().startService(new Intent(getContext(), AlarmService.class));

                            sendNotification(fcmToken, visitorName, visitReason);
                        } else {
                            Toast.makeText(getContext(), "No FCM token found for selected person", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "No matching student/faculty found", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error fetching recipient details", Toast.LENGTH_SHORT).show();
                });


            }


        });






        return Rootview ;

    }
}
package com.example.campsafe.guardDashboard;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

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

import com.example.campsafe.R;
import com.example.campsafe.dbModels.FacultyDB;
import com.example.campsafe.dbModels.NewVisitorDB;
import com.example.campsafe.dbModels.StudentDB;
import com.example.campsafe.newVisitorActivities.VisitorApprovalActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Fragment for entering new visitor details and sending notifications to the selected student or faculty.
 */
public class NewVisitor extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public NewVisitor() {
        // Required empty public constructor
    }

    /**
     * Sends a notification to the selected student or faculty via FCM.
     * @param fcmToken The FCM token of the recipient.
     * @param visitorName The name of the visitor.
     * @param visitReason The reason for the visit.
     * @param documentId The Firestore document ID of the visitor entry.
     */
    private void sendNotification(String fcmToken, String visitorName, String visitReason, String documentId) {
        String channelId = "visitor_alert_channel";
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Visitor Alerts", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Intent to open VisitorApprovalActivity when notification is tapped
        Intent intent = new Intent(getContext(), VisitorApprovalActivity.class);
        intent.putExtra("visitor_name", visitorName);
        intent.putExtra("visit_reason", visitReason);
        intent.putExtra("document_id", documentId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Visitor Alert")
                .setContentText(visitorName + " is here for " + visitReason)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(null); // No sound, as alarm is handled separately

        notificationManager.notify((int) System.currentTimeMillis(), builder.build()); // Unique ID for each notification
    }

    public static NewVisitor newInstance(String param1, String param2) {
        NewVisitor fragment = new NewVisitor();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View Rootview = inflater.inflate(R.layout.fragment_new_visitorfrag, container, false);
        EditText Name = Rootview.findViewById(R.id.visitorName);
        EditText numvisitors = Rootview.findViewById(R.id.numberOfVisitors);
        EditText reason = Rootview.findViewById(R.id.reasonForVisit);
        RadioGroup rad = Rootview.findViewById(R.id.visitorTypeGroup);
        AutoCompleteTextView auto = Rootview.findViewById(R.id.idNumber);
        Button submit = Rootview.findViewById(R.id.submitVisitorButton);

        RadioButton stud = Rootview.findViewById(R.id.radioStudent);
        RadioButton fac = Rootview.findViewById(R.id.radioFaculty);
        StudentDB dbst = new StudentDB(Rootview.getContext());
        FacultyDB dbfa = new FacultyDB(Rootview.getContext());

        ArrayAdapter<String> faculty_data = new ArrayAdapter<>(Rootview.getContext(), android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter<String> student_data = new ArrayAdapter<>(Rootview.getContext(), android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter<String> joint_data = new ArrayAdapter<>(Rootview.getContext(), android.R.layout.simple_dropdown_item_1line);

        dbst.getAllStudents(new StudentDB.StudentListCallback() {
            @Override
            public void onStudentListReceived(List<StudentDB.Student> studentList) {
                for (StudentDB.Student stud : studentList) {
                    String name = stud.getName();
                    Integer id = stud.getId();
                    String result = name + " - " + id;
                    student_data.add(result);
                    joint_data.add(result);
                }
            }

            @Override
            public void onStudentListError(Exception e) {
                // Handle error
            }
        });

        dbfa.getAllFaculties(new FacultyDB.FacultyListCallback() {
            @Override
            public void onFacultyListReceived(List<FacultyDB.Faculty> facultyList) {
                for (FacultyDB.Faculty fac : facultyList) {
                    String name = fac.getName();
                    Integer id = fac.getId();
                    String result = name + " - " + id;
                    faculty_data.add(result);
                    joint_data.add(result);
                }
            }

            @Override
            public void onFacultyListError(Exception e) {
                // Handle error
            }
        });

        auto.setAdapter(joint_data);
        rad.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioStudent) {
                auto.setAdapter(student_data);
            } else if (checkedId == R.id.radioFaculty) {
                auto.setAdapter(faculty_data);
            }
        });

        submit.setOnClickListener(v -> {
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
            final String selectedId; // Declare as final
            if (selectedPerson.contains(" - ")) {
                String[] parts = selectedPerson.split(" - ");
                selectedName = parts[0]; // Name
                selectedId = parts[1];   // ID
            } else {
                selectedId = ""; // Or handle invalid input appropriately
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
            NewVisitorDB newVisitorDb = new NewVisitorDB();
            newVisitorDb.insertData(visitorName, numVisitors, currentDate, currentTime, selectedName, selectedId, visitReason,
                    task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Visitor entry recorded successfully!", Toast.LENGTH_SHORT).show();
                            String documentId = task.getResult().getId(); // Get Firestore document ID
                            Name.setText("");
                            numvisitors.setText("");
                            reason.setText("");
                            rad.clearCheck();
                            auto.setText("");

                            // Fetch FCM Token and send notification
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            String collection = (selectedVisitorTypeId == R.id.radioStudent) ? "Students" : "Faculty";
                            db.collection(collection).document(selectedId).get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String fcmToken = documentSnapshot.getString("fcm_token");
                                    if (fcmToken != null && !fcmToken.isEmpty()) {
                                        sendNotification(fcmToken, visitorName, visitReason, documentId);
                                    } else {
                                        Toast.makeText(getContext(), "No FCM token found for selected person", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "No matching student/faculty found", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error fetching recipient details", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Toast.makeText(getContext(), "Error saving visitor entry!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        return Rootview;
    }
}
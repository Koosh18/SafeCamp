package com.example.campsafe;

import android.content.Context;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class facultydb {
    private  final FirebaseFirestore firestore ;
    private final CollectionReference facultyCollection;
    private final Context context;


    public facultydb(Context context) {
        this.firestore = FirebaseFirestore.getInstance();
        this.facultyCollection = firestore.collection("Faculty");
        this.context = context;
    }

    // Add a faculty to Firestore
    public void add(Integer ID, String name, String password) {
        Faculty faculty = new Faculty(ID, name, password);
        facultyCollection.document(String.valueOf(ID)) // Use ID as document key
                .set(faculty)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Faculty added successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error adding faculty: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Validatefaculty credentials
    public void validateFaculty(Integer ID, String name, String password, FacultyValidationCallback callback) {
        facultyCollection.document(String.valueOf(ID)) // Search by ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot doc = task.getResult();
                        if (doc.exists()) {
                            String dbName = doc.getString("name");
                            String dbPassword = doc.getString("password");

                            // Validate name and password
                            if (dbName != null && dbPassword != null && dbName.equals(name) && dbPassword.equals(password)) {
                                callback.onValidationResult(1); // Credentials are correct
                            } else {
                                callback.onValidationResult(0); // Incorrect name or password
                            }
                        } else {
                            callback.onValidationResult(-1); // ID not found
                        }
                    } else {
                        callback.onValidationError(task.getException());
                    }
                });
    }

    public void getAllFaculties(FacultyListCallback callback) {
        facultyCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Faculty> facultyList = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult()) {
                            Faculty faculty = doc.toObject(Faculty.class);
                            facultyList.add(faculty);
                        }
                        callback.onFacultyListReceived(facultyList);
                    } else {
                        callback.onFacultyListError(task.getException());
                    }
                });
    }

    // Callback interface for getting all faculties
    public interface FacultyListCallback {
        void onFacultyListReceived(List<Faculty> facultyList);

        void onFacultyListError(Exception e);
    }


        // GuardValidationCallback interface to handle results
    public interface FacultyValidationCallback {
        void onValidationResult(int result); // 1 for success, 0 for failure, -1 for not found
        void onValidationError(Exception e); // Handle any error during validation
    }

    // Guard model class for Firebase
    public static class Faculty {
        private Integer id;
        private String name;
        private String password;

        // Empty constructor required for Firestore
        public Faculty() {}

        // Parameterized constructor
        public Faculty(Integer id, String name, String password) {
            this.id = id;
            this.name = name;
            this.password = password;
        }

        // Getters and setters
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

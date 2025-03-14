package com.example.campsafe;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class studentdb {
    private final FirebaseFirestore firestore;
    private final CollectionReference studentCollection;
    private final Context context;

    // Constructor to initialize Firestore and reference the Guards collection
    public studentdb(Context context) {
        this.firestore = FirebaseFirestore.getInstance();
        this.studentCollection = firestore.collection("Students");
        this.context = context;
    }


    public void add(Integer ID, String name, String password) {
        Student student = new Student(ID, name, password); // Create Guard object
        studentCollection.document(String.valueOf(ID)) // Use ID as document key
                .set(student)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Guard added successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error adding guard: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Validate guard credentials
    public void validateStudent(Integer ID, String name, String password, StudentValidationCallback callback) {
        studentCollection.document(String.valueOf(ID)) // Search by ID
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

    public void getAllStudents(studentdb.StudentListCallback callback) {
        studentCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<studentdb.Student> studentList = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult()) {
                          studentdb.Student student = doc.toObject(studentdb.Student.class);
                            studentList.add(student);
                        }
                        callback.onStudentListReceived(studentList);
                    } else {
                        callback.onStudentListError(task.getException());
                    }
                });
    }

    // Callback interface for getting all faculties
    public interface StudentListCallback {
        void onStudentListReceived(List<studentdb.Student> studentList);

        void onStudentListError(Exception e);
    }


    public interface StudentValidationCallback {
        void onValidationResult(int result); // 1 for success, 0 for failure, -1 for not found
        void onValidationError(Exception e); // Handle any error during validation
    }


    public static class Student {
        private Integer id;
        private String name;
        private String password;

        // Empty constructor required for Firestore
        public Student() {}

        // Parameterized constructor
        public Student(Integer id, String name, String password) {
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

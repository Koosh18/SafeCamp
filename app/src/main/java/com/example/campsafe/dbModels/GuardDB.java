package com.example.campsafe.dbModels;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GuardDB {
    private final FirebaseFirestore firestore;
    private final CollectionReference guardCollection;
    private final Context context;

    // Constructor to initialize Firestore and reference the Guards collection
    public GuardDB(Context context) {
        this.firestore = FirebaseFirestore.getInstance();
        this.guardCollection = firestore.collection("Guards");
        this.context = context;
    }

    // Add a guard to Firestore
    public void add(Integer ID, String name, String password) {
        Guard guard = new Guard(ID, name, password); // Create Guard object
        guardCollection.document(String.valueOf(ID)) // Use ID as document key
                .set(guard)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Guard added successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Error adding guard: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Validate guard credentials
    public void validateGuard(Integer ID, String name, String password, GuardValidationCallback callback) {
        guardCollection.document(String.valueOf(ID)) // Search by ID
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

    // GuardValidationCallback interface to handle results
    public interface GuardValidationCallback {
        void onValidationResult(int result); // 1 for success, 0 for failure, -1 for not found
        void onValidationError(Exception e); // Handle any error during validation
    }

    // Guard model class for Firebase
    public static class Guard {
        private Integer id;
        private String name;
        private String password;

        // Empty constructor required for Firestore
        public Guard() {}

        // Parameterized constructor
        public Guard(Integer id, String name, String password) {
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

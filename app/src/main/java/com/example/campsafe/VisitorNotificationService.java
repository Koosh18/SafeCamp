package com.example.campsafe;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import com.example.campsafe.studentDashboard.VisitorApprovalActivity;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentChange;

public class VisitorNotificationService {
    private static final String CHANNEL_ID = "visitor_notifications";
    private final Context context;
    private final int studentId;

    public VisitorNotificationService(Context context, int studentId) {
        this.context = context;
        this.studentId = studentId;
        setupFirestoreListener();
    }

    private void setupFirestoreListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("new_visitor")
                .whereEqualTo("person_id", studentId)
                .whereEqualTo("approved", null)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        if (value != null) {
                            for (DocumentChange change : value.getDocumentChanges()) {
                                if (change.getType() == DocumentChange.Type.ADDED) {
                                    String visitorName = change.getDocument().getString("visitor_name");
                                    String visitReason = change.getDocument().getString("reason");
                                    String documentId = change.getDocument().getId();
                                    sendNotification(visitorName, visitReason, documentId);
                                }
                            }
                        }
                    }
                });
    }

    private void sendNotification(String visitorName, String visitReason, String documentId) {
        Intent intent = new Intent(context, VisitorApprovalActivity.class);
        intent.putExtra("visitor_name", visitorName);
        intent.putExtra("visit_reason", visitReason);
        intent.putExtra("document_id", documentId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Visitor Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notifications for new visitors");
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("New Visitor: " + visitorName)
                .setContentText("Reason: " + visitReason)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
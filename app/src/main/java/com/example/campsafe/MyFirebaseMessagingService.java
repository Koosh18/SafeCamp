package com.example.campsafe;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Handles incoming FCM messages for visitor notifications.
 * Shows a notification bar and triggers an alarm if the user is logged in and the request is pending.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static Ringtone ringtone;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            String visitorName = remoteMessage.getData().get("visitor_name");
            String visitReason = remoteMessage.getData().get("visit_reason");
            String documentId = remoteMessage.getData().get("document_id");

            // Check if the visitor request is still pending
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("new_visitor").document(documentId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Boolean approved = documentSnapshot.getBoolean("approved");
                    if (approved == null) { // Pending request
                        // Check if user is logged in (assumes a method to check login status)
                        if (isUserLoggedIn()) {
                            startAlarm();
                        }
                        showNotification(visitorName, visitReason, documentId);
                    }
                }
            });
        }
    }

    /**
     * Starts the alarm ringtone if not already playing.
     */
    private void startAlarm() {
        if (ringtone == null) {
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
            if (ringtone != null) {
                ringtone.play();
            }
        }
    }

    /**
     * Stops the alarm ringtone.
     */
    public static void stopAlarm() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            ringtone = null;
        }
    }

    /**
     * Shows a notification bar with visitor details.
     * @param visitorName The name of the visitor.
     * @param visitReason The reason for the visit.
     * @param documentId The Firestore document ID of the visitor entry.
     */
    private void showNotification(String visitorName, String visitReason, String documentId) {
        String channelId = "visitor_alert_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Visitor Alerts", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Intent to open VisitorApprovalActivity when notification is tapped
        Intent intent = new Intent(this, VisitorApprovalActivity.class);
        intent.putExtra("visitor_name", visitorName);
        intent.putExtra("visit_reason", visitReason);
        intent.putExtra("document_id", documentId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Visitor Alert")
                .setContentText(visitorName + " is here for " + visitReason)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(null); // No separate notification sound

        notificationManager.notify((int) System.currentTimeMillis(), builder.build()); // Unique ID for each notification
    }

    /**
     * Placeholder method to check if the user is logged in.
     * Replace with actual login status check logic.
     */
    private boolean isUserLoggedIn() {
        // TODO: Implement actual login status check (e.g., check SharedPreferences or Firebase Auth)
        return true; // Temporarily return true for testing
    }
}
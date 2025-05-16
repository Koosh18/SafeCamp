package com.example.campsafe;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

import java.security.Provider;

public  class AlarmService extends Service {
    private Ringtone ringtone;

    @Override
    public void onCreate() {
        super.onCreate();

        // Get the default ringtone
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);

        // Start playing
        if (ringtone != null) {
            ringtone.play();
        }
    }

    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "STOP_ALARM".equals(intent.getAction())) {
            stopAlarm();
            stopSelf();
            return START_NOT_STICKY;  // Prevents restart
        }

        if (ringtone == null) {  // Start alarm only if it's not already playing
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), ringtoneUri);
            if (ringtone != null) {
                ringtone.play();
            }
        }
        return START_STICKY;
    }


    private void stopAlarm() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            ringtone = null;
        }
    }

    @Override
    public void onDestroy() {
        stopAlarm();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

package com.example.campsafe.alarmActivities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent stopIntent = new Intent(context, AlarmService.class);
        stopIntent.setAction("STOP_ALARM");  // Send stop signal
        context.startService(stopIntent);
    }
}

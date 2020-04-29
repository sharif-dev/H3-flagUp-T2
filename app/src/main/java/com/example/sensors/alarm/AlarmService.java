package com.example.sensors.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class AlarmService  extends Service{

    private static final String TAG = "AlarmService";

    private int hour, minute;

    private AlarmRunnable alarmRunnable;
    private Thread alarmThread;                 //might be used for canceling the alarm
    private AlarmManager alarmManager ;
    private PendingIntent pendingIntent;
    private int pendingIntentRequestCode = 0;

    private static final int SNOOZING_INTERVAL_IN_MINUTES = 10;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: service started, fetching data from intent");
        hour = intent.getIntExtra("hour", 8);
        minute = intent.getIntExtra("minute", 30);

        alarmRunnable = new AlarmRunnable();
        alarmThread = new Thread(alarmRunnable);
        alarmThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    private class AlarmRunnable implements Runnable {
        @Override
        public void run() {
            Log.i(TAG, "run: new thread is started");

            Log.i(TAG, "run: initializing alarm manager");
            alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            Intent alarmIntent = new Intent(getApplicationContext(), AlarmTriggerActivity.class);
            pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(), pendingIntentRequestCode, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            int snoozeIntervalInMillis = SNOOZING_INTERVAL_IN_MINUTES * 60 * 1000;
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                    snoozeIntervalInMillis, pendingIntent);

            showAlarmSetNotification();
        }
    }

    private void showAlarmSetNotification() {
        Log.i(TAG, "showAlarmSetNotification: function called");
        // TODO: 4/29/20 remove toast and add notification
        Toast.makeText(this, "Alarm is Set for " + hour + ":" + minute, Toast.LENGTH_SHORT).show();

    }

    public void cancelAlarm() {
        if (alarmManager != null) {
            Log.i(TAG, "cancelAlarm");
            alarmManager.cancel(pendingIntent);
        } else {
            Log.i(TAG, "cancelAlarm: there is no alarm set");
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: service is done, destroying service ...");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

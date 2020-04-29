package com.example.sensors.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class AlarmService  extends Service{

    private static final String TAG = "AlarmService0";

    private int hour, minute;

    private AlarmManager alarmManager ;
    private PendingIntent pendingIntent;
    private int pendingIntentRequestCode = 0;



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.hour = intent.getIntExtra("hour");

        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(getApplicationContext(), AlarmTriggerActivity.class);
        pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), pendingIntentRequestCode, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        int snoozeIntervalInMillis = 1000 * 10 * 60;
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                snoozeIntervalInMillis, pendingIntent);


        Toast.makeText(this, "Alarm is Set for " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
        return super.onStartCommand(alarmIntent, flags, startId);
    }

    public void cancelAlarm() {
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

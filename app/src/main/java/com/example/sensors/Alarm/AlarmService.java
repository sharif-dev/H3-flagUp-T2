package com.example.sensors.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.sensors.AlarmTriggerActivity;

import java.util.Calendar;

public class AlarmService  extends Service{

    private static final String TAG = "AlarmService0";

    private int hour, minute;

    private AlarmManager alarmManager ;
    private PendingIntent pendingIntent;
    private int pendingIntentRequestCode = 0;


    public AlarmService(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;

        alarmManager = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), AlarmTriggerActivity.class);
        pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), pendingIntentRequestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        int snoozeIntervalInMillis = 1000 * 10 * 60;
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                snoozeIntervalInMillis, pendingIntent);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Toast.makeText(this, "Alarm is Set for " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
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

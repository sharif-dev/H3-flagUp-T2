package com.example.sensors.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.sensors.R;

import java.util.Calendar;

public class AlarmService extends Service {

    private static final String TAG = "AlarmService";

    private int hour, minute;

    private AlarmManager alarmManager;
    private int pendingIntentRequestCode = 0;
    private int notificationId = 0;         //unique notification ids used for notifying the alarm is set

    // TODO: 4/29/20 move these to res package
    private static final int SNOOZING_INTERVAL_IN_MINUTES = 10;
    private static final String alarmSetNotificationChannelId = "SetNotification";
    private static final String alarmSetNotificationTitle = "Alarm Service";


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: service started, fetching data from intent");
        hour = intent.getIntExtra("hour", 8);
        minute = intent.getIntExtra("minute", 30);

        createNotificationChannel();

        Runnable alarmRunnable = new AlarmRunnable();
        Thread alarmThread = new Thread(alarmRunnable);
        alarmThread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    private class AlarmRunnable implements Runnable {
        @Override
        public void run() {
            Log.i(TAG, "run: new thread is started, initializing alarm manager");

            alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            Intent alarmIntent = new Intent(getApplicationContext(), AlarmTriggerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(), pendingIntentRequestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            int snoozeIntervalInMillis = SNOOZING_INTERVAL_IN_MINUTES * 60 * 1000;
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    snoozeIntervalInMillis, pendingIntent);

            showAlarmNotification();
        }
    }

    private void showAlarmNotification() {
        Log.i(TAG, "showAlarmSetNotification: notification is shown");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), alarmSetNotificationChannelId)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(alarmSetNotificationTitle)
                .setContentText("An alarm is set for " + hour + ":" + minute)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        Notification notification = builder.build();
        notificationManagerCompat.notify(notificationId, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence channelName = getString(R.string.alarmNotificationChannelName);
            String channelDescription = getString(R.string.alarmNotificationChannelDescription);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(alarmSetNotificationChannelId, channelName, importance);
            channel.setDescription(channelDescription);


            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        cancelAlarm();
        removeNotification();
        super.onDestroy();
    }

    public void cancelAlarm() {
        if (alarmManager != null) {
            Log.i(TAG, "cancelAlarm: stopping alarm manager");

            Intent alarmIntent = new Intent(getApplicationContext(), AlarmTriggerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(), pendingIntentRequestCode, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);

            Toast.makeText(getApplicationContext(), "Alarm Canceled", Toast.LENGTH_SHORT).show();
        } else {
            Log.wtf(TAG, "cancelAlarm: there is no alarm set");
        }
    }

    private void removeNotification() {
        Log.i(TAG, "removeNotification: remove notification");
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

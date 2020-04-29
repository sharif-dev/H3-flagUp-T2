package com.example.sensors.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.sensors.R;

import java.util.Calendar;

public class AlarmService  extends Service{

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
            Log.i(TAG, "run: new thread is started");

            Log.i(TAG, "run: initializing alarm manager");
            alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            Intent alarmIntent = new Intent(getApplicationContext(), AlarmTriggerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(), pendingIntentRequestCode, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);

            int snoozeIntervalInMillis = SNOOZING_INTERVAL_IN_MINUTES * 60 * 1000;
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                    snoozeIntervalInMillis, pendingIntent);

            showAlarmNotification();
        }
    }

    private void showAlarmNotification() {
        // TODO: 4/29/20 can't show heads up notification

        Log.i(TAG, "showAlarmSetNotification: function called");

        long[] vibratePattern = new long[]{10, 10, 10};
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), alarmSetNotificationChannelId)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(alarmSetNotificationTitle)
                .setContentText("An alarm is set for " + hour + ":" + minute)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(vibratePattern);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        Notification notification = builder.build();
        notificationManagerCompat.notify(notificationId,notification);

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            CharSequence channelName = getString(R.string.alarmNotificationChannelName);
            String channelDescription = getString(R.string.alarmNotificationChannelDescription);
            int importance = NotificationManager.IMPORTANCE_HIGH;


            NotificationChannel channel = new NotificationChannel(alarmSetNotificationChannelId, channelName, importance);
            channel.setDescription(channelDescription);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[0]);

            if (soundUri != null) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                channel.setSound(soundUri, att);
            }

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

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

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.sensors.R;

import java.util.Calendar;

public class AlarmService extends Service {

    private static final String TAG = "AlarmService";

    private int hour, minute;

    private int speedLimit;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
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
        if (intent == null)
        {
            Log.w(TAG, "onStartCommand: Null Intent!");
            return super.onStartCommand(intent, flags, startId);
        }
        hour = intent.getIntExtra("hour", 8);
        minute = intent.getIntExtra("minute", 30);

        speedLimit = intent.getIntExtra("speedLimit", 100);

        new Thread(new Runnable() {
            @Override
            public void run() {
                createNotificationChannel();
                setupAlarm();
                showAlarmNotification();
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    private void setupAlarm() {
        Log.i(TAG, "setupAlarm called");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        if(calendar.before(Calendar.getInstance()))
            calendar.add(Calendar.DATE, 1);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("speedLimit", speedLimit);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                pendingIntent);
    }


    private void showAlarmNotification() {
        Log.i(TAG, "showAlarmSetNotification: notification is pushed");

        String msg;
        if (minute < 10)
            msg = "An alarm is set for " + hour + ":" + "0" + minute;
        else
            msg = "An alarm is set for " + hour + ":" + minute;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), alarmSetNotificationChannelId)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(alarmSetNotificationTitle)
                .setContentText(msg)
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
        Log.i(TAG, "cancelAlarm");

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.cancel(pendingIntent);

    }

    private void removeNotification() {
        Log.i(TAG, "removeNotification");
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

}

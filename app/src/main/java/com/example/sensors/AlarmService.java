package com.example.sensors;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.EditText;

import androidx.annotation.Nullable;

public class AlarmService extends Service {

    private static final String TAG = "AlarmService0";
    private EditText wakeupTimeEditText;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

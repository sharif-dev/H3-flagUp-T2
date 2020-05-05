package com.example.sensors.lockscreen;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class LockScreenService extends Service implements SensorEventListener {
    private DevicePolicyManager deviceManager;
    private SensorManager sensorManager;
    private Sensor gravitySensor;
    private double angle;
    private static final double MAX_GRAVITY = 9.81;
    private static final int DEFAULT_ANGLE = 80;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        deviceManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        int intAngle = intent.getIntExtra("angle", DEFAULT_ANGLE);
        angle = Math.toRadians(intAngle);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (Math.acos(event.values[2] / MAX_GRAVITY) < Math.PI / 2 - angle)
            deviceManager.lockNow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

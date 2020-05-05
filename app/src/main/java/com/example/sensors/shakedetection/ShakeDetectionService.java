package com.example.sensors.shakedetection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

public class ShakeDetectionService extends Service implements SensorEventListener {

    SensorManager sensorManager;
    Sensor accelerometer;

    private float accVal;
    private float lastAccVal;
    private float shake;
    private float shakeSensitivity;
    private static final float DEFAULT_SHAKE_SENSITIVITY = 6;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        lastAccVal = accVal;
        accVal = (float) Math.sqrt( (double) x*x + y*y + z*z );
        float delta = accVal - lastAccVal;
        shake = shake * 0.9f + delta;

        if (shake > shakeSensitivity) {
            Log.i("shake activity", "Shake detection");
            startActivityFunc();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        shakeSensitivity = intent.getFloatExtra("shakeSensitvity", DEFAULT_SHAKE_SENSITIVITY);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    private void startActivityFunc() {

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP),
                "My:ag");
        wl.acquire(1000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        accVal = SensorManager.GRAVITY_EARTH;
        lastAccVal = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }


}

package com.example.sensors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ShakeDetectionService extends Service {

    SensorManager sm;
    Sensor accelerometer;

    private float accVal;
    private float lastAccVal;
    private float shake;

    boolean firstTime = true;

    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            lastAccVal = accVal;
            accVal = (float) Math.sqrt( (double) x*x + y*y + z*z );
            float delta = accVal - lastAccVal;
            shake = shake * 0.9f + delta;

            if (shake > 12){
//                Toast toast = Toast.makeText(getApplicationContext(), "Do Not Shake Me", Toast.LENGTH_LONG);
//                toast.show();
                Log.i("shake activity", "Shake detection");
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(sensorListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        accVal = SensorManager.GRAVITY_EARTH;
        lastAccVal = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;

    }

}

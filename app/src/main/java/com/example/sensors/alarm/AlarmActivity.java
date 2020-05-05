package com.example.sensors.alarm;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import com.example.sensors.R;

public class AlarmActivity extends Activity {

    private static final String TAG = "Alarm Receiver";

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_triggered);


        vibratePhone();
        playAlarmSound();
        stopAlarmAfterTenMinutes();
    }

    private void setupGyroscope() {
        Log.i(TAG, "setupGyroscope: initializing gyroscope sensor");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscopeSensor == null) {
            Log.wtf(TAG, "gyroscope sensor is null");
        }


    }

    private void stopAlarmAfterTenMinutes() {

    }

    private void playAlarmSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.saint_roses_remix);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
    }

    private void vibratePhone() {
//        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        vibrator.vibrate();
    }
}

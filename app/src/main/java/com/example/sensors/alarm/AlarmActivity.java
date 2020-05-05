package com.example.sensors.alarm;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sensors.R;

public class AlarmActivity extends AppCompatActivity {

    private static final String TAG = "AlarmActivity";

    private static long SECOND = 1000;
    private static long MINUTE = 1000 * 60;
    private static long alarmDuration = 10 * MINUTE;

    private SensorEventListener gyroscopeEventListener;
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private CountDownTimer countDownTimer;

    private double rotationSpeedLimit;

    private TextView remainingTimeTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_triggered);

        remainingTimeTextView = findViewById(R.id.remainingTimeTextView);

        rotationSpeedLimit = getIntent().getIntExtra("angularSpeedLimit", 100) / 40.0;

        vibratePhone();
        playAlarmSound();
        setUpTimer();
        setupGyroscope();
    }

    private void updateRemainingTime(long millisUntilFinished) {
        long minute = millisUntilFinished / MINUTE;
        long second = (millisUntilFinished - minute * MINUTE) / SECOND;
        if (second < 10) {
            remainingTimeTextView.setText(minute + ":0" + second);
            return;
        }
        remainingTimeTextView.setText(minute + ":" + second);
    }

    private void stopEverything() {
        Log.i(TAG, "stopEverything");
        vibrator.cancel();
        mediaPlayer.release();
        sensorManager.unregisterListener(gyroscopeEventListener);
        Toast.makeText(getApplicationContext(), "Alarm stopped.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void setupGyroscope() {
        Log.i(TAG, "setupGyroscope: initializing gyroscope sensor");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscopeSensor == null) {
            Log.e(TAG, "gyroscope sensor is not available");
        }

        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                if (Math.abs(event.values[2]) >= rotationSpeedLimit) {
                    Log.i(TAG, "onSensorChanged: angular velocity was more than the limit. alarm stopped");
                    stopEverything();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void setUpTimer() {
        countDownTimer = new CountDownTimer(alarmDuration, SECOND) {

            @Override
            public void onTick(long millisUntilFinished) {
                updateRemainingTime(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                stopEverything();
            }
        }.start();
    }

    private void playAlarmSound() {
        Log.i(TAG, "playAlarmSound");
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
        long vibrationPattern[] = {0, 1000, 1000};
        Log.i(TAG, "vibratePhone");
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(vibrationPattern, 0);
    }
}

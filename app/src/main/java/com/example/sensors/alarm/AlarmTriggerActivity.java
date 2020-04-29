package com.example.sensors.alarm;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.sensors.R;

public class AlarmTriggerActivity extends Activity {

    private static final String TAG = "AlarmTriggerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_triggered);

        Log.i(TAG, "onCreate: alarm!");

        vibratePhone();
        playAlarmSound();
        stopAlarmAfterTenMinutes();
    }

    private void stopAlarmAfterTenMinutes() {

    }

    private void playAlarmSound() {

    }

    private void vibratePhone() {

    }
}

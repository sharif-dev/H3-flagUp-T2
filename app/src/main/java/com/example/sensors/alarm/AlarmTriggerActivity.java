package com.example.sensors.alarm;

import android.app.Activity;
import android.os.Bundle;

import com.example.sensors.R;

public class AlarmTriggerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_triggered);
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

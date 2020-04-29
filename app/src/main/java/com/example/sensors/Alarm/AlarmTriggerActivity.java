package com.example.sensors.Alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.sensors.R;

public class AlarmTriggerActivity extends AppCompatActivity {

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

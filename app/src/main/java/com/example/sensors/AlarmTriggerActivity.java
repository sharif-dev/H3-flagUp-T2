package com.example.sensors;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.sensors.R;

public class AlarmTriggerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO: 4/28/20 R.layout.activity_alarmTrigger has an error, cannot start new activity
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

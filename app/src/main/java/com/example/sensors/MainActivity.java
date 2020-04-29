package com.example.sensors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.sensors.Alarm.AlarmService;
import com.example.sensors.Alarm.TimePicker;

public class MainActivity extends AppCompatActivity  {

    private String timePickerTag = "TimePicker";

    private Switch alarmSwitch;
    private Switch shakeDetectionSwitch;
    private Switch lockScreenSwitch;
    private TextView alarmTimeTextView;

    private AlarmService inProgressAlarm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmSwitch = findViewById(R.id.alarmServiceSwitch);
        shakeDetectionSwitch = findViewById(R.id.shakeDetectionSwitch);
        lockScreenSwitch = findViewById(R.id.lockScreenServiceSwitch);
        alarmTimeTextView = findViewById(R.id.alarmTimeTextView);

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && isTimeValid()) {
                    setUpAlarm();
                } else {
                    if (inProgressAlarm != null) {
                        inProgressAlarm.cancelAlarm();
                        inProgressAlarm = null;
                        alarmTimeTextView.setText("");
                    }
                }
            }
        });

        alarmTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
    }

    private boolean isTimeValid() {
        return !alarmTimeTextView.getText().equals("");
    }

    private void showTimePicker() {
        DialogFragment timePickerFragment = new TimePicker(this);
        timePickerFragment.show(getSupportFragmentManager(),timePickerTag);
    }

    public void setUpAlarm() {
        String[] time = alarmTimeTextView.getText().toString().split(":");
        int hour = Integer.parseInt(time[0]), minute = Integer.parseInt(time[1]);

        Intent alarmIntent = new Intent(this, AlarmService.class);
        alarmIntent.putExtra("hour", hour);
        alarmIntent.putExtra("minute", minute);
        startService(alarmIntent);
    }
}

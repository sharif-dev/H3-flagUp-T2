package com.example.sensors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sensors.alarm.AlarmService;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";
    private String timePickerTag = "TimePicker";

    private Switch alarmSwitch;
    private Switch shakeDetectionSwitch;
    private Switch lockScreenSwitch;
    private TextView alarmTimeTextView;

    private boolean isAnyAlarmActivated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmSwitch = findViewById(R.id.alarmServiceSwitch);
        shakeDetectionSwitch = findViewById(R.id.shakeDetectionSwitch);
        lockScreenSwitch = findViewById(R.id.lockScreenServiceSwitch);
        alarmTimeTextView = findViewById(R.id.alarmTimeTextView);

        lockScreenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i(TAG, "onCheckedChanged: starting lock screen service");
                    Intent intent = new Intent(getApplicationContext(), LockScreenService.class);
                    startService(intent);
                    Toast.makeText(getApplicationContext(), "Lock Screen service enabled", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "onCheckedChanged: stopping lock screen service");
                    Intent intent = new Intent(getApplicationContext(), LockScreenService.class);
                    stopService(intent);
                    Toast.makeText(getApplicationContext(), "Lock Screen service turned off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        shakeDetectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i(TAG, "onCheckedChanged: starting shake detection service");
                    Intent intent = new Intent(getApplicationContext(), ShakeDetectionService.class);
                    startService(intent);
                    Toast.makeText(getApplicationContext(), "Shake Detection service enabled", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "onCheckedChanged: stopping shake detection service");
                    Intent intent = new Intent(getApplicationContext(), ShakeDetectionService.class);
                    stopService(intent);
                    Toast.makeText(getApplicationContext(), "Shake Detection service turned off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "onCheckedChanged: alarm switch onCheckedChange called");
                if (isChecked ) {
                    if (isTimeValid()) {
                        Log.i(TAG, "onCheckedChanged: setting up alarm");
                        Toast.makeText(getApplicationContext(), "Alarm added", Toast.LENGTH_SHORT).show();
                        setUpAlarm();
                    } else {
                        Log.i(TAG, "onCheckedChanged: time wasn't set properly");
                        Toast.makeText(getApplicationContext(), "Set the time first", Toast.LENGTH_SHORT).show();
                        alarmSwitch.toggle();
                    }
                } else {
                    if (isAnyAlarmActivated) {
                        Log.i(TAG, "onCheckedChanged: canceling alarm");
                        isAnyAlarmActivated = false;
                        Intent intent = new Intent(getApplicationContext(), AlarmService.class);
                        stopService(intent);
                    } else {
                        Log.i(TAG, "onCheckedChanged: there wasn't any alarms in progress");
                        Toast.makeText(getApplicationContext(), "There's no activated alarm", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        alarmTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: alarmTimeTextView onClick method triggered");
                showTimePicker();
            }
        });
    }

    private boolean isTimeValid() {
        return !alarmTimeTextView.getText().equals("");
    }

    private void showTimePicker() {
        Log.i(TAG, "showTimePicker");
        DialogFragment timePickerFragment = new TimePicker(this);
        timePickerFragment.show(getSupportFragmentManager(),timePickerTag);
    }

    public void setUpAlarm() {
        Log.i(TAG, "setUpAlarm: getting information from alarmTimeTextView");
        String[] time = alarmTimeTextView.getText().toString().split(":");
        int hour = Integer.parseInt(time[0]), minute = Integer.parseInt(time[1]);

        Log.i(TAG, "setUpAlarm: starting alarmService");
        Intent alarmIntent = new Intent(this, AlarmService.class);
        alarmIntent.putExtra("hour", hour);
        alarmIntent.putExtra("minute", minute);
        startService(alarmIntent);

        isAnyAlarmActivated = true;
    }
}

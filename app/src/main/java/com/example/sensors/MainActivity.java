package com.example.sensors;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.sensors.alarm.AlarmService;
import com.example.sensors.lockscreen.AdminReceiver;
import com.example.sensors.lockscreen.LockScreenService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ADMIN_SUCCESS = 1;
    private String timePickerTag = "TimePicker";
    private DevicePolicyManager deviceManager;
    private ComponentName componentName;
    private Switch alarmSwitch;
    private Switch shakeDetectionSwitch;
    private Switch lockScreenSwitch;
    private TextView alarmTimeTextView;
    private SeekBar seekBar;

    private boolean isAnyAlarmActivated = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK)
            startLockScreenService();
        else
            lockScreenSwitch.setChecked(false);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        componentName = new ComponentName(this, AdminReceiver.class);
        deviceManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        alarmSwitch = findViewById(R.id.alarmServiceSwitch);
        shakeDetectionSwitch = findViewById(R.id.shakeDetectionSwitch);
        lockScreenSwitch = findViewById(R.id.lockScreenServiceSwitch);
        alarmTimeTextView = findViewById(R.id.alarmTimeTextView);
        seekBar = findViewById(R.id.seekBar);

        lockScreenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!deviceManager.isAdminActive(componentName)) {
                        Intent intent = new Intent(DevicePolicyManager
                                .ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                                componentName);
                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getResources().getString(R.string.needAdminRights));
                        startActivityForResult(intent, ADMIN_SUCCESS);
                    } else
                        startLockScreenService();
                } else
                    stopLockScreenService();
            }
        });

        shakeDetectionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(getApplicationContext(), ShakeDetectionService.class);
                    startService(intent);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.shakeDetectionEnabled), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), ShakeDetectionService.class);
                    stopService(intent);
	                Toast.makeText(getApplicationContext(), getResources().getString(R.string.shakeDetectionDisabled), Toast.LENGTH_SHORT).show();
                }
            }
        });

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "onCheckedChanged: alarm switch onCheckedChange called");
                if (isChecked) {
                    if (isTimeValid()) {
                        Log.i(TAG, "onCheckedChanged: setting up alarm");
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.alarmAdded), Toast.LENGTH_SHORT).show();
                        setUpAlarm();
                    } else {
                        Log.i(TAG, "onCheckedChanged: time wasn't set properly");
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.setTheTimeFirst), Toast.LENGTH_SHORT).show();
                        alarmSwitch.toggle();
                    }
                } else {
                    if (isAnyAlarmActivated) {
                        Log.i(TAG, "onCheckedChanged: canceling alarm");
                        isAnyAlarmActivated = false;
                        Intent intent = new Intent(getApplicationContext(), AlarmService.class);
                        stopService(intent);
                        Toast.makeText(getApplicationContext(), "Alarm disabled.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, "onCheckedChanged: there wasn't any alarms in progress");
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.noActivatedAlarm), Toast.LENGTH_SHORT).show();
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

    private void stopLockScreenService() {
        Log.i(TAG, "onCheckedChanged: stopping lock screen service");
        Intent intent = new Intent(getApplicationContext(), LockScreenService.class);
        if (stopService(intent))
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.lockScreenDisabled), Toast.LENGTH_SHORT).show();
    }

    private void startLockScreenService() {
        Log.i(TAG, "onCheckedChanged: starting lock screen service");
        Intent intent = new Intent(getApplicationContext(), LockScreenService.class);
        startService(intent);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.lockScreenEnabled), Toast.LENGTH_SHORT).show();
    }

    private boolean isTimeValid() {
        return !alarmTimeTextView.getText().equals("");
    }

    private void showTimePicker() {
        Log.i(TAG, "showTimePicker");
        DialogFragment timePickerFragment = new TimePicker(this);
        timePickerFragment.show(getSupportFragmentManager(), timePickerTag);
    }

    public void setUpAlarm() {
        Log.i(TAG, "setUpAlarm: getting information from alarmTimeTextView");
        String[] time = alarmTimeTextView.getText().toString().split(":");
        int hour = Integer.parseInt(time[0]), minute = Integer.parseInt(time[1]);

        int angularSpeedLimit = seekBar.getProgress();

        Log.i(TAG, "setUpAlarm: starting alarmService");
        Intent alarmIntent = new Intent(this, AlarmService.class);
        alarmIntent.putExtra("speedLimit", angularSpeedLimit);
        alarmIntent.putExtra("hour", hour);
        alarmIntent.putExtra("minute", minute);
        startService(alarmIntent);

        isAnyAlarmActivated = true;
    }

}

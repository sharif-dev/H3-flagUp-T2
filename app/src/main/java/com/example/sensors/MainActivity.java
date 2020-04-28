package com.example.sensors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {


    private Switch alarmSwitch;
    private Switch shakeDetectionSwitch;
    private Switch lockScreenSwitch;

    private AlarmService alarmService;

    private String timePickerTag = "TimePicker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmSwitch = findViewById(R.id.alarmServiceSwitch);
        shakeDetectionSwitch = findViewById(R.id.shakeDetectionSwitch);
        lockScreenSwitch = findViewById(R.id.lockScreenServiceSwitch);


        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showTimePicker();
                } else {
                    // TODO: 4/28/20 cancel service
                }
            }
        });
    }

    private void showTimePicker() {
        DialogFragment timePickerFragment = new TimePicker();
        timePickerFragment.show(getSupportFragmentManager(),timePickerTag);
    }
}

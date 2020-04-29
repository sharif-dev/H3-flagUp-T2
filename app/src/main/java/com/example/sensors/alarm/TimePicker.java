package com.example.sensors.alarm;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.sensors.MainActivity;
import com.example.sensors.R;

import java.util.Calendar;

public class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private TextView alarmTimeTextView;
    private MainActivity mainActivity;
    private Handler handler;

    public TimePicker(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.handler = new Handler(mainActivity.getMainLooper());
        alarmTimeTextView = mainActivity.findViewById(R.id.alarmTimeTextView);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //setting a default time for the time picker
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @Override
    public void onTimeSet(android.widget.TimePicker view, final int hourOfDay, final int minute) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                alarmTimeTextView.setText(getString(R.string.alarmTimeTextFormat, hourOfDay , minute));
            }
        });
    }
}

package com.example.sensors.alarm;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;

import com.example.sensors.R;

import java.util.Calendar;

public class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private Handler handler;
    Preference timeText;
    public TimePicker(Looper looper, Preference pref) {
        this.handler = new Handler(looper);
        timeText = pref;
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
                if (minute < 10)
                    timeText.setSummary(getString(R.string.alarmTimeTextFormatWithLeadingZero, hourOfDay , minute));
                else
                    timeText.setSummary(getString(R.string.alarmTimeTextFormat, hourOfDay , minute));
            }
        });
    }
}

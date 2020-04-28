package com.example.sensors;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //setting a default time for the time picker
        int hour = 8;
        int minute = 30;
        return new TimePickerDialog(getActivity(), this, hour, minute, true);

    }

    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        //user has chosen the time
        // TODO: 4/28/20 start service
    }
}

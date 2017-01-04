package com.scowluga.android.rcacc.Event;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import static com.scowluga.android.rcacc.Event.DatePickerFragment.EDITID;

/**
 * Created by robertlu on 2016-11-22.
 */

public class TimePickerFragment extends DialogFragment
implements TimePickerDialog.OnTimeSetListener {

    int tvID;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        int ID = args.getInt(EDITID);
        tvID = ID;

        int hour = 07; // auto sets to 7 am
        int minute = 00;

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        try {
            ((TextView) getActivity().findViewById(tvID)).setText(hourOfDay + ":" + minute);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error selecting time.", Toast.LENGTH_SHORT).show();
        }
    }
}

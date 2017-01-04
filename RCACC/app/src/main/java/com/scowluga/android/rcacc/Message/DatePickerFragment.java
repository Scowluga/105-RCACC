package com.scowluga.android.rcacc.Message;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by robertlu on 2016-11-22.
 */

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public static String EDITID = "EDITID";
    public static String YEAR = "YEAR";
    public static String MONTH = "MONTH";
    public static String DAY = "DAY";

    int editID;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker

        Bundle args = getArguments();
        editID = args.getInt(EDITID);

        int year = args.getInt(YEAR);
        int month = args.getInt(MONTH);
        int day = args.getInt(DAY);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        try {
            SimpleDateFormat dateFmt = new SimpleDateFormat("MM/dd/yyyy");
            Date date = dateFmt.parse((month + 1) + "/" + day + "/" + year);
            ((EditText)getActivity().findViewById(editID)).setText(dateFmt.format(date));
            } catch (Exception e) {
            Toast.makeText(getContext(), "Error selecting date.", Toast.LENGTH_SHORT).show();
        }
    }
}
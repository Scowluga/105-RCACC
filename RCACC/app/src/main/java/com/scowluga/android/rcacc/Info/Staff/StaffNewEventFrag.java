package com.scowluga.android.rcacc.Info.Staff;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.scowluga.android.rcacc.Event.DatePickerFragment;
import com.scowluga.android.rcacc.Event.Event;
import com.scowluga.android.rcacc.Event.EventProvider;
import com.scowluga.android.rcacc.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.scowluga.android.rcacc.Event.DatePickerFragment.DAY;
import static com.scowluga.android.rcacc.Event.DatePickerFragment.EDITID;
import static com.scowluga.android.rcacc.Event.DatePickerFragment.MONTH;
import static com.scowluga.android.rcacc.Event.DatePickerFragment.YEAR;
import static com.scowluga.android.rcacc.Event.EventProvider.format;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaffNewEventFrag extends Fragment {

    private static List<EditText> reqEntries;

    public StaffNewEventFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.staff_new_event_frag, container, false);

        final EditText start = (EditText)v.findViewById(R.id.staff_event_start_date);
        final EditText end = (EditText)v.findViewById(R.id.staff_event_end_date);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle(); // creating bundle for date picker
                args.putInt(EDITID, R.id.staff_event_start_date); // the ID for the display of date

                // This event will not have a set date (since they're creating it
                Calendar cal = Calendar.getInstance(); // so just use current date
                args.putInt(YEAR, cal.get(Calendar.YEAR));
                args.putInt(MONTH, cal.get(Calendar.MONTH));
                args.putInt(DAY, cal.get(Calendar.DATE));

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle(); // creating bundle for date picker
                args.putInt(EDITID, R.id.staff_event_end_date); // the ID for the display of date

                // This event will not have a set date (since they're creating it
                Calendar cal = Calendar.getInstance(); // so just use current date
                args.putInt(YEAR, cal.get(Calendar.YEAR));
                args.putInt(MONTH, cal.get(Calendar.MONTH));
                args.putInt(DAY, cal.get(Calendar.DATE));

                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(args);
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        reqEntries = new ArrayList<>();
        reqEntries.add((EditText)v.findViewById(R.id.staff_event_title));

        reqEntries.add((EditText)v.findViewById(R.id.staff_event_audience));
        reqEntries.add((EditText)v.findViewById(R.id.staff_event_location));
        reqEntries.add((EditText)v.findViewById(R.id.staff_event_start_date));
        reqEntries.add((EditText)v.findViewById(R.id.staff_event_end_date));
        reqEntries.add((EditText)v.findViewById(R.id.staff_event_details));

        Button b = (Button)v.findViewById(R.id.newEventButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewEvent(v);
            }
        });

        return v;
    }

    public void createNewEvent(View view) {

        boolean creatable = true;
        for (EditText et : reqEntries) {
            if (et.getText().toString().equals("")) {
                et.setError("Field Required");
                creatable = false;
            } else {
                et.setError(null);
            }
        }
        if (!creatable) {
            Toast.makeText(getContext(), "One or more fields missing.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                Calendar startCal = Calendar.getInstance();
                startCal.setTime(format.parse(reqEntries.get(3).getText().toString()));

                Calendar endCal = Calendar.getInstance();
                endCal.setTime(format.parse(reqEntries.get(4).getText().toString()));

                Event event = new Event(reqEntries.get(0).getText().toString(),
                        reqEntries.get(1).getText().toString(),
                        reqEntries.get(2).getText().toString(),
                        startCal,
                        endCal,
                        reqEntries.get(5).getText().toString()
                );
                EventProvider.addEvent(getContext(), event);
                Toast.makeText(getContext(), "Event Created!", Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                Toast.makeText(getContext(), "Error parsing dates. Please reselect and try again.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}


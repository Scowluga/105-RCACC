package com.scowluga.android.rcacc.Info.Staff;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.scowluga.android.rcacc.Main.FirstRun;
import com.scowluga.android.rcacc.Message.DatePickerFragment;
import com.scowluga.android.rcacc.Message.Message;
import com.scowluga.android.rcacc.Message.MessageDisplay;
import com.scowluga.android.rcacc.Message.MessageProvider;
import com.scowluga.android.rcacc.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.scowluga.android.rcacc.Main.MainActivity.TAGFRAGMENT;
import static com.scowluga.android.rcacc.Main.MainActivity.toolbar;
import static com.scowluga.android.rcacc.Message.DatePickerFragment.DAY;
import static com.scowluga.android.rcacc.Message.DatePickerFragment.EDITID;
import static com.scowluga.android.rcacc.Message.DatePickerFragment.MONTH;
import static com.scowluga.android.rcacc.Message.DatePickerFragment.YEAR;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaffNewMessageFrag extends Fragment {

    private static List<EditText> reqEntries;

    public static SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy"); // format for events/**/

    public StaffNewMessageFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.staff_new_message_frag, container, false);


        final EditText dateText = (EditText) v.findViewById(R.id.staff_message_date);
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle(); // creating bundle for date picker
                args.putInt(EDITID, R.id.staff_message_date); // the ID for the display of date

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
        reqEntries.add((EditText)v.findViewById(R.id.staff_message_title));
        reqEntries.add((EditText)v.findViewById(R.id.staff_message_date));
        reqEntries.add((EditText)v.findViewById(R.id.staff_message_audience));
        reqEntries.add((EditText)v.findViewById(R.id.staff_message_notes));

        Button b = (Button)v.findViewById(R.id.newMessageButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewMessage(v);
            }
        });
        return v;
    }

    public void createNewMessage(View view) {

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
            SimpleDateFormat messageFmt = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); // format for events/**/
            try {
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int min = cal.get(Calendar.MINUTE);
                int second = cal.get(Calendar.SECOND);

                Date date = messageFmt.parse(reqEntries.get(1).getText().toString() + " " + hour
                    + ":" + min + ":" + second);

                Message message = new Message(reqEntries.get(0).getText().toString(),
                        date,
                        reqEntries.get(2).getText().toString(),
                        reqEntries.get(3).getText().toString()
                );
                // Attempt to add message to list
                MessageProvider.addMessage(getContext(), message);
                if (FirstRun.wifiOn(getContext())) {
                    Toast.makeText(getContext(), "Message Created!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Message will be uploaded when Wifi becomes available", Toast.LENGTH_SHORT).show();
                }
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frag_layout, new MessageDisplay(), TAGFRAGMENT)
                        .addToBackStack(TAGFRAGMENT)
                        .commit();
                toolbar.setTitle("News");

            } catch (ParseException e) {
                Toast.makeText(getContext(), "Error with dates. Please select again.", Toast.LENGTH_SHORT).show();
            }

        }

    }

}

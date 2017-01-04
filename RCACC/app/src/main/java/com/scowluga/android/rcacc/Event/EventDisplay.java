package com.scowluga.android.rcacc.Event;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.scowluga.android.rcacc.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.scowluga.android.rcacc.Event.DatePickerFragment.DAY;
import static com.scowluga.android.rcacc.Event.DatePickerFragment.EDITID;
import static com.scowluga.android.rcacc.Event.DatePickerFragment.MONTH;
import static com.scowluga.android.rcacc.Event.DatePickerFragment.YEAR;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventDisplay extends Fragment {

    //sorted from current date -> later t'ill end of time
    // This way can just read (no need for .reverse())
    // adding one. insert at correct time, then rewrite ENTIRE FILE and sync
    public static List<Event> events;

    // for reminder setting

    public static EditText dateEt;
    public static EditText timeEt;

    public EventDisplay() {
        // Required empty public constructor
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_fragment, container, false);

        try {
            events = EventProvider.getEvents(getContext());
        } catch (ParseException e) {
            events = null;
            Toast.makeText(getContext(), "Parse Exception", Toast.LENGTH_SHORT).show();
        }

        final ListView eventList = (ListView) view.findViewById(R.id.event_list_view);
        EventAdapter adapter = new EventAdapter(getContext(), events);
        eventList.setAdapter(adapter);

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                LinearLayout linear = (LinearLayout) view.findViewById(R.id.invisLinear);

                if (linear.getVisibility() == View.GONE) {
                    expand(linear);

//                    TextView dates = (TextView) view.findViewById(R.id.event_date_text);
//                    dates.setMaxLines(2); // SO WE CAN SHOW END DATE OF EVENT


                    // GETTING INFO FROM THE CURRENT EVENT
                    Event currentE = events.get(position);
                    final Calendar startCal = currentE.getStartDate();
                    final Calendar endCal = currentE.getEndDate();
                    final String eName = currentE.getName();
                    final String eLocation = currentE.getLocation();
                    final String eDescription = currentE.getDetails();

                    // CALENDAR
                    Button calB = (Button) view.findViewById(R.id.event_calendar_button);

                    final boolean finalHasEnd;
                    if (endCal == null) {
                        finalHasEnd = false;
                    } else {finalHasEnd = true;}


                    calB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_INSERT);
                            intent.setType("vnd.android.cursor.item/event");
                            intent.putExtra(CalendarContract.Events.TITLE, eName);
                            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, eLocation);
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, eDescription);
                            intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                            GregorianCalendar sGCal = new GregorianCalendar(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH), startCal.get(Calendar.DATE));

                            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, sGCal.getTimeInMillis());
                            if (finalHasEnd) {
                                GregorianCalendar eGCal = new GregorianCalendar(endCal.get(Calendar.YEAR), endCal.get(Calendar.MONTH), endCal.get(Calendar.DATE));

                                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eGCal.getTimeInMillis());

                            } else {
                                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, sGCal.getTimeInMillis());

                            }
                            startActivity(intent);

                        }
                    });


                    // REMINDERS
                    Button remB = (Button) view.findViewById(R.id.event_reminder_button);

                    remB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            // Get the layout inflater
                            LayoutInflater inflater = getActivity().getLayoutInflater();
                            // Inflate and set the layout for the dialog
                            // Pass null as the parent view because its going in the
                            // dialog layout

                            View view = inflater.inflate(R.layout.event_reminder_alert_layout, null);

                            builder.setTitle("Set Reminder");
                            builder.setIcon(R.drawable.exclamation);
                            builder.setView(view);

                            //FIND THE EDIT TEXTS
                            dateEt = (EditText) view.findViewById(R.id.reminderDateSelect);
                            dateEt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle args = new Bundle(); // creating bundle for date picker
                                    args.putInt(EDITID, 0); // the ID for the display of date

                                    args.putInt(YEAR, startCal.get(Calendar.YEAR));
                                    args.putInt(MONTH, startCal.get(Calendar.MONTH));
                                    args.putInt(DAY, startCal.get(Calendar.DATE));

                                    DialogFragment newFragment = new DatePickerRemindFrag();
                                    newFragment.setArguments(args);
                                    newFragment.show(getFragmentManager(), "datePicker");

                                }
                            });

                            timeEt = (EditText) view.findViewById(R.id.reminderTimeSelect);
                            timeEt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle args = new Bundle(); // creating bundle for time picker
                                    args.putInt(EDITID, R.id.reminderDateSelect); // the ID for the display of time
                                    DialogFragment newFragment = new TimePickerRemindFrag();
                                    newFragment.setArguments(args);
                                    newFragment.show(getFragmentManager(), "timePicker");
                                }
                            });

                            // Add action buttons
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        String time = timeEt.getText().toString();
                                        List<String> times = new ArrayList<>(Arrays.asList(time.split(":")));

                                        String date = dateEt.getText().toString();
                                        List<String> dates = new ArrayList<>(Arrays.asList(date.split("/")));

                                        SimpleDateFormat alarmFmt = new SimpleDateFormat("MM/dd/yyyy HH/mm"); // the days in the year + h + m

                                        Date alarmDate; //just set to 7 in morning
                                        alarmDate = alarmFmt.parse((dates.get(0))
                                                + "/" + dates.get(1)
                                                + "/" + dates.get(2)
                                                + " " + times.get(0)
                                                + "/" + times.get(1)); //set to whatever time you want.

                                        GregorianCalendar sGCal = new GregorianCalendar();
                                        sGCal.setTime(alarmDate);

                                        long alertTime = sGCal.getTimeInMillis();

                                        setAlarm(eName, alertTime, getContext());

                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), "Please select time and date of alarm.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                            Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            if (b != null) {
                                b.setTextColor(getResources().getColor(R.color.colorAccent));
                            }
                            Button bu = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            if (bu != null) {
                                bu.setTextColor(getResources().getColor(R.color.colorAccent));
                            }

                        }
                    });
                } else {
//                    TextView dates = (TextView) view.findViewById(R.id.event_date_text);
//                    dates.setMaxLines(1);
                    collapse(linear);
                }
            }
        });

        return view;
    }

    public static class DatePickerRemindFrag extends DialogFragment
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
            SimpleDateFormat dateFmt = new SimpleDateFormat("MM/dd/yyyy");
            try {
                Date date = dateFmt.parse((month + 1) + "/" + day + "/" + year);
                dateEt.setText(dateFmt.format(date));
            } catch (Exception e) {
                Toast.makeText(getContext(), "Date could not be selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class TimePickerRemindFrag extends DialogFragment
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
            SimpleDateFormat timeFmt = new SimpleDateFormat("kk:mm");
            try {
                Date date = timeFmt.parse(hourOfDay + ":" + minute);
                timeEt.setText(timeFmt.format(date));
            } catch (Exception e) {
                Toast.makeText(getContext(), "Time could not be selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void setAlarm(String eName, long alertTime, Context context) {

        Intent alertIntent = new Intent(context, AlertReceiver.class); //set the receiver
        alertIntent.putExtra("name", eName); //adding in the name of the event
        alertIntent.putExtra("color", context.getResources().getColor(R.color.colorAccent));
        alertIntent.setAction("event_broadcast_string"); // to a tag in the manifest

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, //wake up the device. using the passed in time
                PendingIntent.getBroadcast(context, 1, alertIntent, 0));

        Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public static void insertEvent(Event e) {
        boolean inserted = false;
        if (events.size() > 0) {
            for (Event event : new ArrayList<>(events)) {
                if (event.getStartDate().after(e.getStartDate())) {
                    events.add(events.indexOf(event), e);
                    inserted = true;
                    break;
                }
            }
            if (!inserted) {
                events.add(events.size(), e);
            };
        } else {
            events.add(0, e);
        }
    }
}



package com.scowluga.android.rcacc.Event;

import android.content.Context;
import android.widget.Toast;

import com.scowluga.android.rcacc.Message.MessageProvider;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_APPEND;
import static com.scowluga.android.rcacc.Event.EventDisplay.events;

/**
 * Created by robertlu on 2016-11-18.
 */

public class EventProvider {

    public static SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy"); // format for events
    public static String formatString = "mm/dd/yyyy"; // JUST TO KEEP THINGS IN THE SAME PLACE

    public static String eventFileName = "eventFileName.txt"; // file name

    public static List<Event> getEvents(Context context) throws ParseException {
        List<Event> events = new ArrayList<>();

//        String str = MessageProvider.getString(context, eventFileName); // get the string of file
        String str = "hi";
        List<String> eventStrings = new ArrayList<>(Arrays.asList(str.split(MessageProvider.grandDelimiter)));

        for (String s: eventStrings) {
            events.add(Event.decode(s)); //decode them and add
        }

        return events;
    }

    public static void addEvent(Context context, Event e) {
        EventDisplay.insertEvent(e); // add it to the list
        //Now rewrite the entire thing.
        rewriteEvents(context);
    }

    public static void deleteEvent(Context context, int index) {
        if (EventDisplay.events == null) {
            try {
                EventDisplay.events = EventProvider.getEvents(context);
            } catch (ParseException e) {
                EventDisplay.events = null;
                Toast.makeText(context, "Parse Exception", Toast.LENGTH_SHORT).show();
            }
        }
        events.remove(index);
        rewriteEvents(context);
    }

    private static void rewriteEvents(Context context) {

//        MessageProvider.clearFile(context, eventFileName); // clear file
        for (Event event: events) {
            String write = Event.encode(event); //encode it
            FileOutputStream outputStream;
            try {
                outputStream = context.openFileOutput(eventFileName, MODE_APPEND);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(write); // write it to file
                outputStreamWriter.close();
                outputStream.close();
            } catch (Exception error) {
                Toast.makeText(context, "Error writing to file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void resetEvents(Context context) throws ParseException {
//        MessageProvider.clearFile(context, eventFileName); // clear it then add 3

        events = new ArrayList<>();
        Calendar cal1 = Calendar.getInstance(); // start 1
        cal1.setTime(format.parse("12/17/2016"));
        Calendar cal2 = Calendar.getInstance(); // end 1
        cal2.setTime(format.parse("12/18/2016"));

        Calendar cal3 = Calendar.getInstance(); // start 2
        cal3.setTime(format.parse("12/11/2016")); // just one day long

        Calendar cal5 = Calendar.getInstance(); // start 3
        cal5.setTime(format.parse("04/03/2017"));
        Calendar cal6 = Calendar.getInstance(); // end 3
        cal6.setTime(format.parse("04/05/2017"));

        EventProvider.addEvent(context, new Event("Grocery Bagging", "All Cadets", "Shopping Centre", cal1, cal2, "Get Volunteer hours!"));
        EventProvider.addEvent(context, new Event("Christmas Dinner", "All Cadets", "Streetsville Legion", cal3, null, "Come celebrate this yearly tradition with great food!"));
        EventProvider.addEvent(context, new Event("Navigation/Trekking Weekend FTX", "All Cadets", "TBD", cal5, cal6, "Learn all sorts of skills, and pass star level checks."));
    }
}

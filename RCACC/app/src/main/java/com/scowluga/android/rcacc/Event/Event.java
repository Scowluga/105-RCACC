package com.scowluga.android.rcacc.Event;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.scowluga.android.rcacc.Event.EventProvider.format;
import static com.scowluga.android.rcacc.Message.MessageProvider.grandDelimiter;

/**
 * Created by robertlu on 2016-11-17.
 */

public class Event {

    private String name;
    private String audience;
    private String location;

    private Calendar startDate;
    private Calendar endDate;

    private String details;

    public Event(String name, String audience, String location, Calendar startDate, Calendar endDate, String details) {
        this.name = name;
        this.audience = audience;
        this.location = location;
        this.startDate = startDate;
        if (endDate == null) {
            this.endDate = null; 
        } else if (endDate.getTimeInMillis() == startDate.getTimeInMillis()) {
            this.endDate = null;
        } else {
            this.endDate = endDate;
        }
        this.details = details;
    }

    private static String delimiter = "-----"; // Changeable delimiter for attributes in a message

    public static Event decode(String str) throws ParseException {
        List<String> strings = Arrays.asList(str.split(delimiter)); //splitting by delimiter
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(format.parse(strings.get(3)));

        Calendar endDate;
        if (strings.get(4).equals("null")) { // if there is no end date
            endDate = null;
        } else {
            endDate = Calendar.getInstance();
            endDate.setTime(format.parse(strings.get(4)));
        }
        Event event = new Event(strings.get(0),
                strings.get(1),
                strings.get(2),
                startDate,
                endDate,
                strings.get(5));
        return event;
    }

    //Encoding
    public static String encode(Event e) {
        List<String> attributes = e.getStringList(); //all attributes in list

        String s = "";
        for (String attr : attributes) {
            s = s + attr + delimiter; //separating each attribute by delimiter
        }
        s += grandDelimiter; //adding the grandDelimiter to show end of 'Message' object
        return s;
    }

    public List<String> getStringList() {
        List<String> strings = new ArrayList<>();
        strings.add(this.name);
        strings.add(this.audience);
        strings.add(this.location);

        strings.add(format.format(this.startDate.getTime()));
        if (this.endDate == null) { // if it's null, make it null
            strings.add("null");
        } else {
            strings.add(format.format(this.endDate.getTime()));
        }
        strings.add(this.details);
        return strings;
    }

    @Override
    public String toString() {

        return "name: " + this.name;
    }

    public String getAudience() {
        return audience;
    }

    public String getDetails() {
        return details;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public Calendar getStartDate() {
        return startDate;
    }
}

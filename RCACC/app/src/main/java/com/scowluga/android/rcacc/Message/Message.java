package com.scowluga.android.rcacc.Message;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.scowluga.android.rcacc.Message.MessageProvider.grandDelimiter;

/**
 * Created by robertlu on 2016-10-31.
 */

public class Message {

    // THE ATTRIBUTES OF A 'MESSAGE'
    private String title;
    private Date date;
    private String audience;
    private String notes;
    private int _id; // internal database id


    public Message(String title, Date date, String audience, String notes) {
        this.title = title;
        this.date = date;
        this.audience = audience;
        this.notes = notes;
    }

    // Constructor
    public Message(String title, Date date, String audience, String notes, int id) {
        this.title = title;
        this.date = date;
        this.audience = audience;
        this.notes = notes;
        this._id = id;
    }

    //Getters
    public String getTitle() { return title; }

    public Date getDate() {
        return date;
    }

    public String getAudience() {
        return audience;
    }

    public String getNotes() {
        return notes;
    }

    int get_id() {
        return _id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Message)) {
            return false;
        }
        Message rhs = (Message)obj;
        return this.notes.equals(rhs.getNotes())
                && this.title.equals(rhs.getTitle())
                && this.audience.equals(rhs.getAudience());
    }

    @Override
    public int hashCode() {
        return notes.hashCode();
    }
}

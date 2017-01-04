package com.scowluga.android.rcacc.Event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scowluga.android.rcacc.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


/**
 * Created by robertlu on 2016-11-18.
 */

public class EventAdapter extends BaseAdapter {


    public static SimpleDateFormat eventFmt = new SimpleDateFormat("MMM dd, yyyy");

    private Context context;
    private List<Event> events;

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false);
        }
        Event event = events.get(position);

        TextView name = (TextView)convertView.findViewById(R.id.event_name_text);
        TextView audience = (TextView)convertView.findViewById(R.id.event_audience_text);
        TextView location = (TextView)convertView.findViewById(R.id.event_location_text);
        TextView date = (TextView)convertView.findViewById(R.id.event_date_text);
        TextView date2 = (TextView)convertView.findViewById(R.id.event_date_text2);

        TextView details = (TextView)convertView.findViewById(R.id.event_detail_details);

        name.setText(event.getName());
        audience.setText(event.getAudience());
        location.setText(event.getLocation());
        details.setText(event.getDetails());

        Calendar start = event.getStartDate();
        Calendar end = event.getEndDate();

        if (end == null) {
            date.setText(eventFmt.format(start.getTime()));
            date2.setText("");
        } else {
            date.setText(eventFmt.format(start.getTime()));
            date2.setText(eventFmt.format(end.getTime()));
        }

        return convertView;
    }
}

package com.scowluga.android.rcacc.Message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scowluga.android.rcacc.Event.EventAdapter;
import com.scowluga.android.rcacc.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.scowluga.android.rcacc.Event.EventProvider.format;

/**
 * Created by robertlu on 2016-10-31.
 */


public class MessageAdapter extends BaseAdapter {
    private Context context;
    private List<Message> messages;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.message_row, parent, false);
        }
        Message message = messages.get(position);

        TextView titleText = (TextView) convertView.findViewById(R.id.title_text);
        titleText.setText(message.getTitle());

        TextView dateText = (TextView) convertView.findViewById(R.id.date_text);
        Date date = message.getDate();
        SimpleDateFormat fmt = MessageProvider.fmt;
        dateText.setText(EventAdapter.eventFmt.format(date));

        TextView audienceText = (TextView) convertView.findViewById(R.id.audience_text);
        audienceText.setText(message.getAudience());

        TextView noteText = (TextView) convertView.findViewById(R.id.notes_text);
        noteText.setText(message.getNotes());

        return convertView;
    }
}

package com.scowluga.android.rcacc.About;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scowluga.android.rcacc.R;

import java.util.List;

/**
 * Created by robertlu on 2016-11-17.
 */
public class TeamAdapter extends BaseAdapter{

    private final int TYPE_PERSON = 0;
    private final int TYPE_HEADER = 1;

    private Context context;
    private List<Team> teams;

    public TeamAdapter(Context context, List<Team> teams) {
        this.context = context;
        this.teams = teams;
        teams.add(0, new Team());
    }
    @Override
    public int getCount() {
        return teams.size();
    }

    @Override
    public Object getItem(int position) {
        return teams.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return (getItemViewType(position) == TYPE_PERSON);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_PERSON;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);
        if (convertView == null) {
            if (type == TYPE_PERSON) {
                convertView = LayoutInflater.from(context).inflate(R.layout.team_row, parent, false);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.team_header, parent, false);
            }
        }

        if (type == TYPE_PERSON) {
            Team team = teams.get(position);

            TextView name = (TextView)convertView.findViewById(R.id.team_name);
            TextView description = (TextView)convertView.findViewById(R.id.team_description);
            TextView timing = (TextView)convertView.findViewById(R.id.team_hours);

            name.setText(team.getName());
            description.setText(team.getDescription());
            timing.setText(team.getTimings());
        }

        return convertView;
    }
}

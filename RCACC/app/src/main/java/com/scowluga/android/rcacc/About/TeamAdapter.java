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

    private Context context;
    private List<Team> teams;

    public TeamAdapter(Context context, List<Team> teams) {
        this.context = context;
        this.teams = teams;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.team_row, parent, false);
        }

        Team team = teams.get(position);

        TextView name = (TextView)convertView.findViewById(R.id.team_name);
        TextView description = (TextView)convertView.findViewById(R.id.team_description);
        TextView timing = (TextView)convertView.findViewById(R.id.team_hours);

        name.setText(team.getName());
        description.setText(team.getDescription());
        timing.setText(team.getTimings());

        return convertView;
    }
}

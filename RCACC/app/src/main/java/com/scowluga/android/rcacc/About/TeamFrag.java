package com.scowluga.android.rcacc.About;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.scowluga.android.rcacc.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamFrag extends Fragment {


    public TeamFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.team_frag, container, false);


        List<Team> teamList = TeamProvider.getTeams();

        ListView lv = (ListView)v.findViewById(R.id.team_list);

        TeamAdapter ta = new TeamAdapter(getContext(), teamList);
        lv.setAdapter(ta);




        return v;
    }

}

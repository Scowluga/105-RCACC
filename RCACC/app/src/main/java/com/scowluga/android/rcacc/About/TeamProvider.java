package com.scowluga.android.rcacc.About;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robertlu on 2016-11-17.
 */

public class TeamProvider {

    public static List<Team> getTeams() {
        List<Team> teamList = new ArrayList<>();

        teamList.add(new Team("Drill Team", "Drill, dress, and deportment. Three pillars of cadet skill, and the focus of the drill team. Make our corps proud!", "Thursday 7pm - 9pm"));
        teamList.add(new Team("Band", "A ceremonial element to the corps to keep the corps in step and to provide music for the parade. Come learn about music, and to play your own instrument!.", "Thursday 7pm - 9pm"));
        teamList.add(new Team("Shooting Team", "Develop marksmanship skills through training with the daisy air rifle. New to shooting? Come try out anyways!", "Thursday 7pm - 9pm"));
        teamList.add(new Team("Orienteering Team", "Learn to navigate using a map and compass, and find specific points in a given area. Come learn about these crucial tools.", "Thursday 7pm - 9pm and Sunday morning "));
        //teamList.add(new Team("Swim Team", "Go swimming", "TBD"));

        return teamList;



    }
}

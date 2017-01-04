package com.scowluga.android.rcacc.About;

/**
 * Created by robertlu on 2016-11-17.
 */

public class Team {

    private String name;
    private String description;
    private String timings;

    public Team (String name, String description, String timings) {
        this.name = name;
        this.description = description;
        this.timings = timings;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getTimings() {
        return timings;
    }
}

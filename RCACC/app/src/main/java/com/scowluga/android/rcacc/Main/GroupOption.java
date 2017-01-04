package com.scowluga.android.rcacc.Main;

import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Created by robertlu on 2016-10-31.
 */


public class GroupOption {
    private String name; // NAME
    private List<ChildOption> children; // List of children
    private Integer imageId; // image
    private boolean isClickable; // whether it opens frag or not
    private boolean wifi;

    private Fragment frag; // the fragment
    private Integer state; // state (up or down)
    private boolean refresh; // uses refresh button?

    public static final Integer UP = 0; // setting state.up or state.down
    public static final Integer DOWN = 1;

    public GroupOption(String name,                  // name
                       List<ChildOption> children,   // list of children
                       Integer imageId,              // image
                       boolean isClickable,          // is clickable or not (is expandable?)
                       Fragment frag,                // what fragment it opens
                       boolean refresh,              // has refresh button (message / event)
                       boolean reqWifi               // requires wifi to open
    ) {
        this.name = name;
        this.children = children;
        this.imageId = imageId;
        this.isClickable = isClickable;
        this.frag = frag;
        this.refresh = refresh;
        this.wifi = reqWifi;
        state = 0;
    }

    public String getName() {
        return name;
    }

    public List<ChildOption> getChildren() {
        return children;
    }

    public Integer getImageId() {
        return imageId;
    }

    public boolean isClickable() {
        return isClickable;
    }

    public Fragment getFrag() {
        return frag;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public boolean requiresWifi() {
        return wifi;
    }
}

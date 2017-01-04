package com.scowluga.android.rcacc.Main;

import android.support.v4.app.Fragment;

/**
 * Created by robertlu on 2016-10-31.
 */

public class ChildOption{
    private String name;
    private Integer imageId;
    private Fragment frag;
    private boolean wifi;

    private boolean refresh;


    public ChildOption(String name,     // name
                       Integer imageId, // image
                       Fragment frag,   // fragment
                       boolean wifi     // requires wifi
    ) {
        this.name = name;
        this.imageId = imageId;
        this.frag = frag;
        this.wifi = wifi;
    }

    public String getName() {
        return name;
    }

    public Integer getImageId() {
        return imageId;
    }

    public Fragment getFrag() {
        return frag;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public boolean requiresWifi() { return wifi; }
}

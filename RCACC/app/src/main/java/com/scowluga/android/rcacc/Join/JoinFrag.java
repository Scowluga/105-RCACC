package com.scowluga.android.rcacc.Join;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.scowluga.android.rcacc.Main.FirstRun;
import com.scowluga.android.rcacc.Online.Website;
import com.scowluga.android.rcacc.R;

import static com.scowluga.android.rcacc.Main.MainActivity.TAGFRAGMENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class JoinFrag extends Fragment {

    public JoinFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.join_frag, container, false);


        FloatingActionButton launch = (FloatingActionButton)v.findViewById(R.id.mapFAB);
        launch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirstRun.wifiOn(getContext())) {
                    String url = "https://www.google.com/maps/place/Streetsville+Community+Cadet+Centre/@43.582416,-79.7140662,17.5z/data=!4m13!1m7!3m6!1s0x0:0xb5c92bc9fe574de!2sStreetsville+Community+Cadet+Centre!3b1!8m2!3d43.5824526!4d-79.7135197!3m4!1s0x0:0xb5c92bc9fe574de!8m2!3d43.5824526!4d-79.7135197?hl=en-US";
                    Fragment mapFrag = Website.newInstance(url);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.frag_layout, mapFrag, TAGFRAGMENT)
                            .addToBackStack(TAGFRAGMENT)
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Section requires wifi.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

}
package com.scowluga.android.rcacc.About;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.scowluga.android.rcacc.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFrag extends Fragment {


    public HistoryFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.about_history_frag, container, false);

        Button our = (Button)v.findViewById(R.id.our_cap);
        our.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayImage dialogue = DisplayImage.newInstance(
                        R.layout.history_image_display
                );
                dialogue.show(getFragmentManager(), "IMAGE_FRAG");

            }
        });
        Button norm = (Button)v.findViewById(R.id.norm_cap);
        norm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayImage dialogue = DisplayImage.newInstance(
                        R.layout.history_image_display1
                );
                dialogue.show(getFragmentManager(), "IMAGE_FRAG");
            }
        });



        return v;
    }

}

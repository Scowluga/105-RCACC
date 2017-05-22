package com.scowluga.android.rcacc.Main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.scowluga.android.rcacc.R;
import com.scowluga.android.rcacc.sync.TestDrive;

/**
 * A simple {@link Fragment} subclass.
 */
public class DebugFrag extends Fragment {


    public DebugFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.zzz_debug_frag, container, false);

        Button reset = (Button)v.findViewById(R.id.debug_reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = "com.scowluga.android.rcacc";
                TestDrive testDrive = new TestDrive(getContext());
                testDrive.ttt(userId);
            }
        });

        return v;
    }

}

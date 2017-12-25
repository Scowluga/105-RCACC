package com.scowluga.android.rcacc.CadetResources.Starlevel;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scowluga.android.rcacc.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NameFragment extends Fragment {


    public NameFragment() {
        // Required empty public constructor
    }

    public static NameFragment newInstance(String name) {

        Bundle args = new Bundle();
        args.putString("STARLEVEL", name);
        NameFragment fragment = new NameFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.star_fragment_name, container, false);
        Bundle bundle = getArguments();
        String name = bundle.getString("STARLEVEL");

        TextView tv = (TextView)v.findViewById(R.id.name);
        tv.setText(name);

        return v;

    }

}

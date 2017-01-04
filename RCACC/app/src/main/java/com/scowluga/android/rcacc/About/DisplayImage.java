package com.scowluga.android.rcacc.About;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.scowluga.android.rcacc.R;

/**
 * Created by robertlu on 2016-11-01.
 */

public class DisplayImage extends DialogFragment {

    public static String KEYINT = "keyint";

    public static DisplayImage newInstance(int layout) {

        Bundle args = new Bundle();
        args.putInt(KEYINT, layout);
        DisplayImage fragment = new DisplayImage();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        int layout = args.getInt(KEYINT);

        View rootView = inflater.inflate(layout, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Button bt = (Button)rootView.findViewById(R.id.history_back);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return rootView;
    }
}


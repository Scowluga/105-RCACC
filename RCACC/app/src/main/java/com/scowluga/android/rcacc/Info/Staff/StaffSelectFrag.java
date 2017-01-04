package com.scowluga.android.rcacc.Info.Staff;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.scowluga.android.rcacc.R;

import static com.scowluga.android.rcacc.Main.MainActivity.TAGFRAGMENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaffSelectFrag extends Fragment {


    public StaffSelectFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.staff_select_frag, container, false);

        Button event = (Button)v.findViewById(R.id.staffSelectEvent);
        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEvent(v);
            }
        });

        Button message = (Button)v.findViewById(R.id.staffSelectMessage);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMessage(v);
            }
        });

        Button delMessage = (Button)v.findViewById(R.id.staffDeleteMessage);
        delMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delMessage();
            }
        });

        Button delEvent = (Button)v.findViewById(R.id.staffDeleteEvent);
        delEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delEvent();
            }
        });
        return v;
    }

    public void newEvent(View view) {
        Fragment frag = new StaffNewEventFrag();
        getFragmentManager().beginTransaction()
                .replace(R.id.frag_layout, frag, TAGFRAGMENT)
                .addToBackStack(TAGFRAGMENT)
                .commit();

    }

    public void newMessage(View view) {
        Fragment frag = new StaffNewMessageFrag();
        getFragmentManager().beginTransaction()
                .replace(R.id.frag_layout, frag, TAGFRAGMENT)
                .addToBackStack(TAGFRAGMENT)
                .commit();
    }

    public void delMessage() {
        Toast.makeText(getContext(), "Select message to delete", Toast.LENGTH_SHORT).show();
        Fragment frag = new StaffDeleteMessage();
        getFragmentManager().beginTransaction()
                .replace(R.id.frag_layout, frag, TAGFRAGMENT)
                .addToBackStack(TAGFRAGMENT)
                .commit();
    }

    public void delEvent() {
        Toast.makeText(getContext(), "Select event to delete", Toast.LENGTH_SHORT).show();
        Fragment frag = new StaffDeleteEvent();
        getFragmentManager().beginTransaction()
                .replace(R.id.frag_layout, frag, TAGFRAGMENT)
                .addToBackStack(TAGFRAGMENT)
                .commit();
    }

}

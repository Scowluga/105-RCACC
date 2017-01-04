package com.scowluga.android.rcacc.Info.Staff;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.scowluga.android.rcacc.Event.Event;
import com.scowluga.android.rcacc.Event.EventAdapter;
import com.scowluga.android.rcacc.Event.EventProvider;
import com.scowluga.android.rcacc.R;

import java.text.ParseException;
import java.util.List;

import static com.scowluga.android.rcacc.Main.MainActivity.TAGFRAGMENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaffDeleteEvent extends Fragment {

    List<Event> delEventList;

    public StaffDeleteEvent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.staff_delete_event, container, false);


        try {
            delEventList = EventProvider.getEvents(getContext()); // read from file to get list
            if (delEventList == null) { //if there's nothing, say error.
                Toast.makeText(getActivity(), "Error displaying events", Toast.LENGTH_SHORT).show();
            }
            ListView lv = (ListView)v.findViewById(R.id.event_delete_lv);

            EventAdapter adapter = new EventAdapter(getContext(), delEventList);

            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Event will be deleted")
                            .setMessage("Are you sure you want to delete this event?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EventProvider.deleteEvent(getContext(), position);
                                    Toast.makeText(getContext(), "Event Deleted!", Toast.LENGTH_SHORT).show();
                                    Fragment frag = getFragmentManager().findFragmentByTag(TAGFRAGMENT);
                                    getFragmentManager().beginTransaction()
                                            .detach(frag)
                                            .attach(frag)
                                            .addToBackStack(TAGFRAGMENT)
                                            .commit();
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                    }
                });
        } catch (ParseException e) {
            Toast.makeText(getContext(), "Error displaying events", Toast.LENGTH_SHORT).show();
        }

        return v;
    }

}

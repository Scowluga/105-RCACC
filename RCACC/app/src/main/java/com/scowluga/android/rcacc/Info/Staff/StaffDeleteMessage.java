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

import com.scowluga.android.rcacc.Main.FirstRun;
import com.scowluga.android.rcacc.Message.Message;
import com.scowluga.android.rcacc.Message.MessageAdapter;
import com.scowluga.android.rcacc.Message.MessageProvider;
import com.scowluga.android.rcacc.R;

import java.util.List;

import static com.scowluga.android.rcacc.Main.MainActivity.TAGFRAGMENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaffDeleteMessage extends Fragment {

    public static List<Message> delMessageList;

    public StaffDeleteMessage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.staff_delete_message, container, false);

        delMessageList = MessageProvider.getInfo(getContext()); // read from file to get list

        if (delMessageList == null) { //if there's nothing, say error.
            Toast.makeText(getActivity(), "Error displaying messages", Toast.LENGTH_SHORT).show();
        } else {
            ListView lv = (ListView)v.findViewById(R.id.messageDeleteLV);

            MessageAdapter adapter = new MessageAdapter(getContext(), delMessageList);

            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Message will be deleted")
                            .setMessage("Are you sure you want to delete this message?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Attempt to delete
                                    MessageProvider.deleteMessage(getContext(), delMessageList.get(position));
                                    Fragment frag = getFragmentManager().findFragmentByTag(TAGFRAGMENT);
                                    getFragmentManager().beginTransaction()
                                            .detach(frag)
                                            .attach(frag)
                                            .addToBackStack(TAGFRAGMENT)
                                            .commit();
                                    if (FirstRun.wifiOn(getContext())) {
                                        Toast.makeText(getContext(), "Message Deleted!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "Message will be deleted when Wifi becomes available", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });
        }
        return v;
    }

}

package com.scowluga.android.rcacc.Message;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.scowluga.android.rcacc.Main.FirstRun;
import com.scowluga.android.rcacc.Main.MainActivity;
import com.scowluga.android.rcacc.R;
import com.scowluga.android.rcacc.sync.GoogleDriveDataStore;
import com.scowluga.android.rcacc.sync.SyncUtils;

import java.util.ArrayList;
import java.util.List;

import static com.scowluga.android.rcacc.Main.MainActivity.TAGFRAGMENT;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageDisplay extends Fragment {

    public static List<Message> messageList;

    public MessageDisplay() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.message_display_layout, container, false);
        AsyncTask task = new ProgressTask(view, getActivity()).execute();
        return view;
    }

    // Setting up the list of messages
    private class ProgressTask extends AsyncTask<String, Void, List<Message>> {
        private View view;
        private ProgressDialog dialog;

        public ProgressTask(View v, Activity act) {
            view = v;
            dialog = new ProgressDialog(act);
        }
        protected void onPreExecute() {
            dialog.setMessage("Displaying Messages...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(final List<Message> messageList) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            ListView lv = (ListView) view.findViewById(R.id.message_list);

            MessageAdapter adapter = new MessageAdapter(getContext(), messageList);

            lv.setAdapter(adapter);

        }

        protected List<Message> doInBackground(final String... args) {
            messageList = MessageProvider.getInfo(getContext()); // read from file to get list
            return messageList;
        }
    }
}


package com.scowluga.android.rcacc.Info.Contact;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.scowluga.android.rcacc.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFrag extends Fragment {


    public ContactFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.contact_fragment, container, false);

        final EditText subject = (EditText)v.findViewById(R.id.contactSubject);
        final EditText message = (EditText)v.findViewById(R.id.contactMessage);

        Button send = (Button)v.findViewById(R.id.contactSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ok = true;
                if (TextUtils.isEmpty(subject.getText())) {
                    subject.setError("Field Required");
                    ok = false;
                }
                if (TextUtils.isEmpty(message.getText())) {
                    message.setError("Field Required");
                    ok = false;
                }
                if (ok) {
                    // for now opens gmail
                    // probably send info to spreadsheet
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto","david.scowluga@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());
                    emailIntent.putExtra(Intent.EXTRA_TEXT, message.getText().toString());
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));

//                    Intent i = new Intent(Intent.ACTION_SEND);
//                    i.setType("message/rfc822");
//                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"david.scowluga@gmail.com"});
//                    i.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());
//                    i.putExtra(Intent.EXTRA_TEXT   , message.getText().toString());
//                    try {
//                        startActivity(Intent.createChooser(i, "Send mail..."));
//                    } catch (android.content.ActivityNotFoundException ex) {
//                        Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//                    }
                }

            }
        });



        return v;
    }

}

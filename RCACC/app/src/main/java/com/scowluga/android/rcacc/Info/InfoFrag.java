package com.scowluga.android.rcacc.Info;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.scowluga.android.rcacc.Info.Staff.LoginFrag;
import com.scowluga.android.rcacc.Main.FirstRun;
import com.scowluga.android.rcacc.Main.MainActivity;
import com.scowluga.android.rcacc.Main.OptionProvider;
import com.scowluga.android.rcacc.Online.Website;
import com.scowluga.android.rcacc.R;

import static com.scowluga.android.rcacc.Main.MainActivity.TAGFRAGMENT;
import static com.scowluga.android.rcacc.R.id.frag_layout;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFrag extends Fragment {

    public InfoFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.info_fragment, container, false);

        PackageInfo pInfo = null;
        try {
            pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            String version = pInfo.versionName;

            TextView tv = (TextView)v.findViewById(R.id.info_display2);
            tv.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getContext(), "App Version Not Found", Toast.LENGTH_SHORT).show();
        }
        Button login = (Button)v.findViewById(R.id.infoLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.isStaff) {
                    LoginFrag.openStaff(getFragmentManager());
                } else {
                    Fragment frag = new LoginFrag();
                    getFragmentManager().beginTransaction()
                            .replace(frag_layout, frag, TAGFRAGMENT)
                            .addToBackStack(TAGFRAGMENT)
                            .commit();
                }
            }
        });

        Button contact = (Button)v.findViewById(R.id.infoContect);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FirstRun.wifiOn(getContext())) {
                    Fragment frag = Website.newInstance(OptionProvider.CONTACT_URL);
                    getFragmentManager().beginTransaction()
                            .replace(frag_layout, frag, TAGFRAGMENT)
                            .addToBackStack(TAGFRAGMENT)
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Section requires wifi.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button rate = (Button)v.findViewById(R.id.infoRate);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // don't know if this works. Stackoverflow copied
                Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
                }
            }
        });


        return v;
    }

}

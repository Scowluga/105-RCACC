package com.scowluga.android.rcacc.Main;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.scowluga.android.rcacc.Info.Staff.LoginFrag;
import com.scowluga.android.rcacc.Online.Website;
import com.scowluga.android.rcacc.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.scowluga.android.rcacc.Main.MainActivity.TAGFRAGMENT;
import static com.scowluga.android.rcacc.R.id.frag_layout;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {


    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.home_fragment, container, false);
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

        final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);

        final ImageView iv = (ImageView)v.findViewById(R.id.anchor);

        FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.refreshFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> pictures = getImages();

                int random = current;
                while (random == current) {
                    random = (int)Math.round(Math.random() * pictures.size());
                }

                iv.setImageResource(random);
            }
        });
        return v;
    }

    private static List<Integer> getImages () {
        List<Integer> pictures = new ArrayList<Integer>(Arrays.asList(
                R.drawable.featuregraphic, // has to be number 0
                R.drawable.drillteam,
                R.drawable.ss1,
                R.drawable.ss2,
                R.drawable.ss3,
                R.drawable.ss4,
                R.drawable.ss5,
                R.drawable.ss6,
                R.drawable.ss7,// dw about 8
                R.drawable.ss9,
                R.drawable.ss10
        ));
        return pictures;
    }
}

package com.scowluga.android.rcacc.Main;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.scowluga.android.rcacc.Info.Staff.LoginFrag;
import com.scowluga.android.rcacc.Online.Website;
import com.scowluga.android.rcacc.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.scowluga.android.rcacc.Main.MainActivity.TAGFRAGMENT;
import static com.scowluga.android.rcacc.R.id.frag_layout;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {

    public Handler handler;
    public ImageSwitcher imageSwitcher;
    public boolean x = true;

    public static int count = 0;

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
        final Button contact = (Button)v.findViewById(R.id.infoContect);
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

        final Button rate = (Button)v.findViewById(R.id.infoRate);
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

        final List<Integer> pictures = getImages();

        imageSwitcher = (ImageSwitcher)v.findViewById(R.id.switcher);

        Animation in = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
        Animation out = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);

        imageSwitcher.setInAnimation(in);
        imageSwitcher.setOutAnimation(out);

        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setAdjustViewBounds(true);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });
        imageSwitcher.setImageResource(pictures.get(count));

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.refreshFAB);
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        lp.anchorGravity = Gravity.TOP | GravityCompat.END;
        fab.setLayoutParams(lp);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count ++;
                if (count == pictures.size()) {
                    count = 0;
                }
                imageSwitcher.setImageResource(pictures.get(count));
            }
        });


        // Showcase Guide
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(200);
        final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), "HOMESEQ1");
        sequence.setConfig(config);

        final ImageView imageView = (ImageView)v.findViewById(R.id.cadetLogo);

        sequence.addSequenceItem(imageView, "Welcome to the 105 RCACC application", "CONTINUE");
        sequence.addSequenceItem(new MaterialShowcaseView.Builder(getActivity())
                .setTarget(getToolbarNavigationButton(MainActivity.toolbar))
                .setContentText("Open the side drawer to access various resources")
                .setDismissText("GOT IT")
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        final Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.drawer.openDrawer(Gravity.LEFT);
                            }
                        }, 150);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.drawer.closeDrawer(Gravity.LEFT);

                                ShowcaseConfig config2 = new ShowcaseConfig();
                                config2.setDelay(200);
                                final MaterialShowcaseSequence sequence2 = new MaterialShowcaseSequence(getActivity(), "HOMESEQ2");
                                sequence2.setConfig(config2);

                                sequence2.addSequenceItem(contact, "Send me a message with questions or suggestions", "OK");

                                sequence2.addSequenceItem(rate, "If you like the app, leave me a rating", "GOT IT");

                                sequence2.addSequenceItem(imageView, "Enjoy the application", "FINISH");

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sequence2.start();
                                    }
                                }, 250);
                            }
                        }, 1250);

                    }
                }).build());


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sequence.start();
            }
        }, 75);

        return v;
    }

    public ImageButton getToolbarNavigationButton(Toolbar toolbar) {
        int size = toolbar.getChildCount();
        for (int i = 0; i < size; i++) {
            View child = toolbar.getChildAt(i);
            if (child instanceof ImageButton) {
                ImageButton btn = (ImageButton) child;
                if (btn.getDrawable() == toolbar.getNavigationIcon()) {
                    return btn;
                }
            }
        }
        return null;
    }


    @Override
    public void onResume() {
        super.onResume();
        MainActivity.toolbar.setTitle("Home");
        if (imageSwitcher == null) {
            imageSwitcher = (ImageSwitcher)getActivity().findViewById(R.id.switcher);
            Animation in = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
            Animation out = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);

            imageSwitcher.setInAnimation(in);
            imageSwitcher.setOutAnimation(out);

            imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
                @Override
                public View makeView() {
                    ImageView imageView = new ImageView(getContext());
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    imageView.setAdjustViewBounds(true);
                    imageView.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    return imageView;
                }
            });
        }
        imageSwitcher.setImageResource(getImages().get(count));


        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.refreshFAB);
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        lp.anchorGravity = Gravity.TOP | GravityCompat.END;
        fab.setLayoutParams(lp);
        fab.setVisibility(View.INVISIBLE);

        // Change picture every few seconds

        handler = new Handler();

        x = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (x) {
                    x = false;
                } else {
                    count ++;
                }
                if (count == getImages().size()) {
                    count = 0;
                }
                fab.setVisibility(View.VISIBLE);
                imageSwitcher.setImageResource(getImages().get(count));

                handler.postDelayed(this, 5000);
            }
        }, 1);

    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
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

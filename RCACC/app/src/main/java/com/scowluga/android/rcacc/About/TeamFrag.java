package com.scowluga.android.rcacc.About;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.scowluga.android.rcacc.Main.MainActivity;
import com.scowluga.android.rcacc.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamFrag extends Fragment {


    public TeamFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.team_frag, container, false);


        List<Team> teamList = TeamProvider.getTeams();

        ListView lv = (ListView)v.findViewById(R.id.team_list);

        TeamAdapter ta = new TeamAdapter(getContext(), teamList);
        lv.setAdapter(ta);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout linear = (LinearLayout) view.findViewById(R.id.team_invis_layout);
                if (linear.getVisibility() == View.GONE) {
                    ImageView iv = (ImageView)view.findViewById(R.id.drop_down);
                    iv.setImageResource(R.drawable.drop_up);
                    expand(linear);
                } else {
                    ImageView iv = (ImageView)view.findViewById(R.id.drop_down);
                    iv.setImageResource(R.drawable.drop_down);
                    collapse(linear);
                }
            }
        });

        return v;
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @Override
    public void onResume() {
        MainActivity.toolbar.setTitle("Teams");
        super.onResume();
    }
}

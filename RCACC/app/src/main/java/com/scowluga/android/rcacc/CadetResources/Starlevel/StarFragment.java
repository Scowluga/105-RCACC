package com.scowluga.android.rcacc.CadetResources.Starlevel;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.scowluga.android.rcacc.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class StarFragment extends Fragment {


    String[] names = {"green", "red", "silver", "gold", "master"};
    public StarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.star_fragment, container, false);

        final TabLayout tabs = (TabLayout)v.findViewById(R.id.tabLayout);
        ViewPager pager = (ViewPager)v.findViewById(R.id.viewPager);
        TabAdapter adapter = new TabAdapter(getFragmentManager());
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);

        for (int i = 0; i < 5; i ++) {
            tabs.getTabAt(i).setIcon(adapter.icons[i]);
        }

        Button IG = (Button)v.findViewById(R.id.igbtn);
        IG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = tabs.getSelectedTabPosition();
                if (i >= 0 && i < 5) {
                    openPdf(names[i] + "ig");
                } else {
                    Toast.makeText(getActivity(), "Please select a star level", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button QSP = (Button)v.findViewById(R.id.qspbtn);
        QSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = tabs.getSelectedTabPosition();
                if (i >= 0 && i < 5) {
                    openPdf(names[i] + "qsp");
                } else {
                    Toast.makeText(getActivity(), "Please select a star level", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }



    private void openPdf(String filename) {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + filename + ".pdf");
        if (!file.exists()) {
            copyAssets(filename);
        }

        /** PDF reader code */
        file = new File(Environment.getExternalStorageDirectory() + "/" + filename + ".pdf");

        Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + "STARLEVEL", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri,"application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            getActivity().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "Please install a PDF viewer", Toast.LENGTH_SHORT).show();
        }
    }

    //method to write the PDFs file to sd card
    private void copyAssets(String filename) {
        AssetManager assetManager = getActivity().getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        }
        catch (IOException e) {
        }
        for(int i=0; i<files.length; i++) {
            String fStr = files[i];
            if(fStr.equalsIgnoreCase(filename + ".pdf")) {
                try {
                    InputStream in = assetManager.open(files[i]);
                    OutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/" + files[i]);
                    // copy in to out
                    byte[] buffer = new byte[1024];
                    int read;
                    while((read = in.read(buffer)) != -1){
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    out.flush();
                    out.close();
                    break;
                }
                catch(Exception e) {
                }
            }
        }
    }


    public class TabAdapter extends FragmentStatePagerAdapter {

        String[] names = {"Green Star", "Red Star", "Silver Star", "Gold Star", "Master Cadet"};
        int[] icons = {R.drawable.greenstar, R.drawable.redstar, R.drawable.silverstar, R.drawable.goldstar, R.drawable.mastercadet};

        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return NameFragment.newInstance(names[position]);
        }
        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public int getCount() {
            return names.length;
        }
    }

}

package com.scowluga.android.rcacc.Info.Staff;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.scowluga.android.rcacc.Main.MainActivity;
import com.scowluga.android.rcacc.Message.MessageProvider;
import com.scowluga.android.rcacc.R;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;
import static com.scowluga.android.rcacc.Main.MainActivity.TAGFRAGMENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFrag extends Fragment {

    public LoginFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.staff_login_fragment, container, false);

        Button b = (Button)v.findViewById(R.id.staffLoginButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClick(v);
            }
        });
        return v;
    }

    public void loginClick(View view) {

        boolean success = loginSuccess(view);
        if (success) {
            openStaff(getFragmentManager());

            getActivity().getSharedPreferences("STAFFLOGIN", MODE_PRIVATE).edit()
                    .putBoolean("isStaff", true).apply();
            MainActivity.isStaff = true;
        }
    }
    private boolean loginSuccess(View v) {
        // TODO: CHANGE LOGINS! NOT ACTUAL LOGINS USED (don't even try them)
        List<String> validKeys = new ArrayList<>(
                Arrays.asList("dummy1", "dummy2"));
        EditText et = (EditText)getActivity().findViewById(R.id.loginKey);
        if (et.getText().toString().equals("")) {
            et.setError("No password entered");
            return false; // no password
        } else if (validKeys.contains(et.getText().toString())) {
            return true; // Successful login
        }
        et.setError("Wrong password");
        return false;
    }

    public static void openStaff(FragmentManager fm) {
        Fragment frag = new StaffSelectFrag();
        fm.beginTransaction()
                .replace(R.id.frag_layout, frag, TAGFRAGMENT)
                .addToBackStack(TAGFRAGMENT)
                .commit();
        MainActivity.toolbar.setTitle("Staff Portal");
    }
}

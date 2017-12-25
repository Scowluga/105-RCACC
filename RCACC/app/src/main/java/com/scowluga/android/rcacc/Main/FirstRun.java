package com.scowluga.android.rcacc.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.scowluga.android.rcacc.R;

public class FirstRun extends AppCompatActivity {

    public static boolean DEBUG = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zzz_first_run);

        //first run. Default to true (so first time it runs)
        boolean first = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirst", true);


        if (first && !wifiOn(getApplicationContext())) {
            AlertDialog alertDialog = new AlertDialog.Builder(FirstRun.this).create();
            alertDialog.setTitle("Error");
            alertDialog.setIcon(R.drawable.logocadets);
            alertDialog.setMessage("Please establish wifi connection");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0); // Closes it
                        }
                    });
            alertDialog.show();

        } else {
            // Creating async task
            // Parameters (don't need) - ProgressBar (don't need) - Result
        }
    }


    public static boolean wifiOn(Context context) { // FROM NOW ON if (wifiOn) is checking if there is wifi
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if( wifiInfo.getNetworkId() == -1 ){
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        }
        else {
            return false; // Wi-Fi adapter is OFF
        }
    } // boolean for if there is wifi
}

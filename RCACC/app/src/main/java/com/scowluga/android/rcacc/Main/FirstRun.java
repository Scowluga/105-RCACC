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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.scowluga.android.rcacc.R;
import com.scowluga.android.rcacc.sync.GoogleDriveDataStore;
import com.scowluga.android.rcacc.sync.SyncUtils;

public class FirstRun extends AppCompatActivity {

    public static boolean DEBUG = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zzz_first_run);

        if (!wifiOn(getApplicationContext())) {
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
            // Paramters (don't need) - ProgressBar (don't need) - Result
            AsyncTask task = new ProgressTask(getApplicationContext(), FirstRun.this).execute();
        }
    }

    // Setting up the application
    private class ProgressTask extends AsyncTask<String, Void, GoogleDriveDataStore> {
        private Context context;
        private ProgressDialog dialog;
        public ProgressTask(Context ctx, Activity act) {
            context = ctx;
            dialog = new ProgressDialog(act);
        }
        protected void onPreExecute() {
            dialog.setMessage("Connecting to Database");
            dialog.show();
        }

        @Override
        protected void onPostExecute(final GoogleDriveDataStore googleDriveDataStore) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (googleDriveDataStore != null) {

                // Synchronize
                SyncUtils.CreateSyncAccount(getApplicationContext());
                SyncUtils.TriggerRefresh();

                // Start activity because successfuly set up
                Intent intent = new Intent(FirstRun.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(context, "Database connection error", Toast.LENGTH_LONG).show();
            }
        }

        protected GoogleDriveDataStore doInBackground(final String... args) {
            try {
                return GoogleDriveDataStore.getInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
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

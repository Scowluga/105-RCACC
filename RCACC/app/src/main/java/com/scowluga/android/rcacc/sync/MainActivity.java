//package com.scowluga.android.rcacc.sync;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//
//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.Thing;
//import com.scowluga.android.rcacc.sync.SyncUtils;
//
//public class MainActivity extends AppCompatActivity {
//
//    private final static int RESOLVE_CONNECTION_REQUEST_CODE = 3000;
//
//    private static final String TAG = "MainActivity";
//
////    private GoogleApiClient mGoogleApiClient ;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
////        setContentView(R.layout.activity_entry_list);
//
//        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
////        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
////                .addApi(Drive.API)
////                .addScope(Drive.SCOPE_FILE)
////                .addConnectionCallbacks(this)
////                .addOnConnectionFailedListener(this)
////                .addApi(AppIndex.API).build();
////        mGoogleApiClient.connect();
//
//        SyncUtils.CreateSyncAccount(getApplicationContext());
//
//    }
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
//// See https://g.co/AppIndexing/AndroidStudio for more information.
////        mGoogleApiClient.connect();
////        Toast.makeText(this, "CLICKED", Toast.LENGTH_SHORT).show();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
////        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
//    }
//
//    public void perform(View view) {
//        Log.i(TAG, "perform ...");
//        try {
//
//            int y = 2;
//
////            InputStream resourceAsStream = this.getClass().getResourceAsStream("/client_secret_phi618.json");
////            InputStream resourceAsStream1 = this.getClass().getResourceAsStream("/xml/authenticator.xml");
////            InputStream resourceAsStream2 = this.getClass().getClassLoader().getResourceAsStream("MainActivity.java");
////            InputStream resourceAsStream3 = this.getClassLoader().getResourceAsStream("client_secret_phi618.json");
//
//            SyncUtils.TriggerRefresh();
//
//            String userId = "com.scowluga.android.rcacc";
//            TestDrive tdrive = new TestDrive(getApplicationContext());
//            tdrive.ttt(userId);;
//
//            // Try to perform a Drive API request, for instance:
////             File file = service.files().insert(body, mediaContent).execute();
////        } catch (UserRecoverableAuthIOException e) {
//        } catch (Exception e) {
////            startActivityForResult(e.getIntent(), COMPLETE_AUTHORIZATION_REQUEST_CODE);
//            startActivityForResult(null, 101);
//        }
//    }
//
//
//    @Override
//    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
//        switch (requestCode) {
//            case RESOLVE_CONNECTION_REQUEST_CODE:
////                if (resultCode == RESULT_OK && mGoogleApiClient != null) {
////                  mGoogleApiClient.connect();
////                }
//                break;
//        }
//    }
//
////    @Override
////    public void onConnected(@Nullable Bundle bundle) {
////        Log.i(TAG, "connected ...");
////    }
//
////    @Override
////    public void onConnectionSuspended(int i) {
////
////    }
//
////    @Override
////    public void onConnectionFailed(ConnectionResult connectionResult) {
////        Log.e(TAG, "failed to connect ...");
////        if (connectionResult.hasResolution()) {
////            try {
////                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
////            } catch (IntentSender.SendIntentException e) {
////                // Unable to resolve, message user appropriately
////            }
////        } else {
////            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
////        }
////    }
//
//    /**
//     * ATTENTION: This was auto-generated to implement the App Indexing API.
//     * See https://g.co/AppIndexing/AndroidStudio for more information.
//     */
////    public Action getIndexApiAction() {
////        Thing object = new Thing.Builder()
////                .setName("Main Page") // TODO: Define a title for the content shown.
////                // TODO: Make sure this auto-generated URL is correct.
////                .setUrl(Uri.decode("http://[ENTER-YOUR-URL-HERE]"))
////                .build();
////        return new Action.Builder(Action.TYPE_VIEW)
////                .setObject(object)
////                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
////                .build();
////    }
//
//    @Override
//    public void onStop() {
//        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
//// See https://g.co/AppIndexing/AndroidStudio for more information.
////        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
////        AppIndex.AppIndexApi.end(client, getIndexApiAction());
////        client.disconnect();
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
////        mGoogleApiClient.disconnect();
//    }
//    }
//
//    /**
//     * ATTENTION: This was auto-generated to implement the App Indexing API.
//     * See https://g.co/AppIndexing/AndroidStudio for more information.
//     */
//    public Action getIndexApiAction() {
//        Thing object = new Thing.Builder()
//                .setName("Main Page") // TODO: Define a title for the content shown.
//                // TODO: Make sure this auto-generated URL is correct.
//                .setUrl(Uri.decode("http://[ENTER-YOUR-URL-HERE]"))
//                .build();
//        return new Action.Builder(Action.TYPE_VIEW)
//                .setObject(object)
//                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
//                .build();
//    }
//}

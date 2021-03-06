package com.scowluga.android.rcacc.Main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.scowluga.android.rcacc.Online.Website;
import com.scowluga.android.rcacc.R;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 1;
    public static final String PRIVACY_POLICY = "https://github.com/Scowluga/105-RCACC/blob/master/RCACC/PRIVACYPOLICY.md";

    public static final String TAGFRAGMENT = "Tagfragment";
    public static ExpandableListView expList;
    public static List<GroupOption> options;
    public static Toolbar toolbar;

    public static MenuItem refresh;
    public static DrawerLayout drawer;

    public static boolean debugger = false;

    public static boolean isStaff;
    public static boolean isAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //------------ initialize at the news fragment
        Fragment frag = new Home();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_layout, frag, TAGFRAGMENT)
                .commit();

        getSupportActionBar().setTitle("Home");
        // -------------

        // Staff User Checks
        isStaff = getSharedPreferences("STAFFLOGIN", MODE_PRIVATE)
                .getBoolean("isStaff", false);
        isAdmin = getSharedPreferences("STAFFLOGIN", MODE_PRIVATE)
                .getBoolean("isAdmin", false);

        expList = (ExpandableListView) findViewById(R.id.expList);
        options = OptionProvider.getList(isStaff, isAdmin);
        expList.setAdapter(new OptionAdapter(this, options));

        expList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                GroupOption opt = options.get(groupPosition);
                if (opt.isClickable()) { //it can open a fragment

                    Fragment frag;
                    frag = opt.getFrag(); // get the fragment

                    if (FirstRun.wifiOn(getApplicationContext())
                            || !opt.requiresWifi()) { // YES WIFI or don't need it

                        if (opt.getName() == OptionProvider.facebook || // IF ITS AN INTENT WEBSITE
                                opt.getName() == OptionProvider.website ||
                                opt.getName() == OptionProvider.instagram) {
                            launchWeb(opt.getName()); // just open the website
                            closeDrawer();
                        } else { // PROCEED WITH FRAGMENT CHANGE

                            if (frag instanceof Website) { //check if website, allow landscape
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                            } else {
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            }
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frag_layout, frag, TAGFRAGMENT)
                                    .addToBackStack(TAGFRAGMENT)
                                    .commit();

                            toolbar.setTitle(opt.getName()); // set title
                            closeDrawer(); // close drawer
                            if (opt.isRefresh()) { // set refresh button visible or not
                                refresh.setVisible(true);
                            } else {
                                refresh.setVisible(false);
                            }
                        }
                    } else { // NO WIFI
                        Toast.makeText(MainActivity.this, "Section requires wifi.", Toast.LENGTH_SHORT).show();
                    }
                } else { // just change the open/close state arrows
                    if (opt.getState() == GroupOption.UP) {
                        opt.setState(GroupOption.DOWN); // if up: down
                    } else {
                        opt.setState(GroupOption.UP); // if down: up
                    }
                }
                return false;
            }
        });
        expList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ChildOption opt = options.get(groupPosition).getChildren().get(childPosition);

                Fragment frag;
                frag = opt.getFrag(); // get frag

                if (FirstRun.wifiOn(getApplicationContext())
                        || !opt.requiresWifi()) { // YES WIFI or don't need it
                    if (opt.getName() == OptionProvider.facebook || // IF ITS AN INTENT WEBSITE
                            opt.getName() == OptionProvider.website ||
                            opt.getName() == OptionProvider.instagram) {
                        launchWeb(opt.getName()); // just open the website
                        closeDrawer();
                    } else { // PROCEED WITH FRAGMENT CHANGE
                        if (frag instanceof Website) { //check if website, allow landscape
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        } else {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frag_layout, frag, TAGFRAGMENT)
                                .addToBackStack(TAGFRAGMENT)
                                .commit();

                        toolbar.setTitle(opt.getName()); // set title
                        closeDrawer(); // close drawer
                        if (opt.isRefresh()) { // set refresh button visible or not
                            refresh.setVisible(true);
                        } else {
                            refresh.setVisible(false);
                        }
                    }
                } else { // NO WIFI
                    Toast.makeText(MainActivity.this, "Section requires wifi.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    public void launchWeb(String name) {
        String url = "";
        switch (name) {
            case OptionProvider.facebook:
                url = "https://www.facebook.com/groups/105RCACC/";
                break;
            case OptionProvider.instagram:
                url = "https://www.instagram.com/105army/";
                break;
            case OptionProvider.website:
                url = "http://www.105armycadets.ca/main/";
                break;
            default:
                //
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


    public static void closeDrawer() {
        drawer.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void onBackPressed() {

        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) { //CLOSER DRAWER FIRST PRIORITY
                drawer.closeDrawer(GravityCompat.START);
            } else {
                Fragment currentFragment = (Fragment)getSupportFragmentManager().findFragmentByTag(TAGFRAGMENT);
                if (currentFragment instanceof Website) { //IF ITS A WEBSITE
                    WebView wv = ((Website) currentFragment).getWebView();
                    if (wv.canGoBack()) { //AND WEBSITE CAN GO BACK
                        wv.goBack();
                    } else {
                        super.onBackPressed();
                    }
                } else {
                    super.onBackPressed(); //REGULAR BACK
                }
            }
        } catch (Exception e) {
            super.onBackPressed(); // if wv is null (maps), just go back
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        refresh = menu.findItem(R.id.refresh);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        Fragment frag = getSupportFragmentManager().findFragmentByTag(TAGFRAGMENT);
        getSupportFragmentManager().beginTransaction()
                .detach(frag)
                .attach(frag)
                .addToBackStack(TAGFRAGMENT)
                .commit();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // allowed

                } else { // not allowed
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Sorry");
                    alertDialog.setMessage("We require permission to open cadet instructional guides.");
//                    alertDialog.setIcon();
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    ActivityCompat.requestPermissions(MainActivity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            MY_PERMISSIONS_REQUEST_STORAGE);
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Why", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY));
                            startActivity(intent);
                        }
                    });
                    alertDialog.show();
                }

        }
    }
}

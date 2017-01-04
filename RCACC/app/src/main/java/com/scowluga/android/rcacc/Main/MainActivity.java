package com.scowluga.android.rcacc.Main;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.scowluga.android.rcacc.About.HistoryFrag;
import com.scowluga.android.rcacc.Message.MessageDisplay;
import com.scowluga.android.rcacc.Online.Website;
import com.scowluga.android.rcacc.R;
import com.scowluga.android.rcacc.sync.SyncUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAGFRAGMENT = "Tagfragment";
    public ExpandableListView expList;
    public List<GroupOption> options;
    public static Toolbar toolbar;

    public static MenuItem refresh;
    public static DrawerLayout drawer;

    public static boolean debugger = false;

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
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_layout, frag, TAGFRAGMENT)
                .addToBackStack(TAGFRAGMENT)
                .commit();

        getSupportActionBar().setTitle("Home");
        // -------------

        // getting reference to refresh
        expList = (ExpandableListView) findViewById(R.id.expList);
        options = OptionProvider.getList();
        if (debugger) {
            expList.setAdapter(new OptionAdapter(this, options.subList(0, options.size())));
        } else {
            expList.setAdapter(new OptionAdapter(this, options.subList(0, options.size() - 1)));
        }

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
                }

                else {
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

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

    public void refreshButton(MenuItem item) { // Refreshes current fragment.
        SyncUtils.TriggerRefresh();
        Fragment frag = getSupportFragmentManager().findFragmentByTag(TAGFRAGMENT);
        getSupportFragmentManager().beginTransaction()
                .detach(frag)
                .attach(frag)
                .addToBackStack(TAGFRAGMENT)
                .commit();
    }

}

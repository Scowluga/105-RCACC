package com.scowluga.android.rcacc.Main;

import com.scowluga.android.rcacc.About.HistoryFrag;
import com.scowluga.android.rcacc.About.TeamFrag;
import com.scowluga.android.rcacc.Info.InfoFrag;
import com.scowluga.android.rcacc.Join.JoinFrag;
import com.scowluga.android.rcacc.Message.MessageDisplay;
import com.scowluga.android.rcacc.Online.Website;
import com.scowluga.android.rcacc.R;
import com.scowluga.android.rcacc.news.NewsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robertlu on 2016-10-29.
 */

public class OptionProvider {

    // url's for google forms.
    public static final String CONTACT_URL = "https://docs.google.com/forms/d/e/1FAIpQLScqkdFSQ0vuI9p5tnpKChdRTds6VI_KcZtmGC8x7yvcDljxpg/viewform";
    public static final String ABSENCE_URL = "https://docs.google.com/forms/d/e/1FAIpQLSeiKST_rXmse0GOnX3uozcO4ldzk0CRFe3Ta4pfYFJYstbKhA/viewform";
    public static final String SUMMER_URL = "https://docs.google.com/forms/d/e/1FAIpQLSc3HulNF6BgD89fT0LWZogKjeCBeSn_8LeKa37imY94xGVRoQ/viewform";
    public static final String UNIFORM_URL = "https://docs.google.com/forms/d/e/1FAIpQLScBWRfJdvrASQ0fN_gCAH0VpE-k_xmGLU82NeZI95eZj3mNDA/viewform";

    public static final String CAL_URL = "https://calendar.google.com/calendar/embed?height=600&wkst=1&bgcolor=%23ffffff&src=artillery.co%40gmail.com&color=%232F6309&ctz=America%2FToronto";

    public static final String facebook = "Facebook"; // the 3 string titles
    public static final String website = "Website";
    public static final String instagram = "Instagram";

// ---------Constructors for Options---------
//    public ChildOption(String name,     // name
//                       Integer imageId, // image
//                       Fragment frag,   // fragment
//                       boolean wifi     // requires wifi
//    ) {
//    public GroupOption(String name,                  // name
//                       List<ChildOption> children,   // list of children
//                       Integer imageId,              // image
//                       boolean isClickable,          // is clickable or not (is expandable?)
//                       Fragment frag,                // what fragment it opens
//                       boolean refresh,              // has refresh button (message / event)
//                       boolean reqWifi               // requires wifi to open
//    ) {

    public static List<GroupOption> getList(boolean isStaff, boolean isAdmin) {
        // --------------------------------------------
        List<GroupOption> navOptions = new ArrayList<>(); // TOTAL LIST FOR OPTIONS

        // HOME
        navOptions.add(new GroupOption("Home", new ArrayList<ChildOption>(), R.drawable.homeicon, true, new Home(), false, false));

        // NEWS
//        navOptions.add(new GroupOption("News", new ArrayList<ChildOption>(), R.drawable.announcement, true, new MessageDisplay(), true, false));
//        Goodbye 7 more years of code :( good learning
        // EVENTS
        // Goodbye 7 years of code :(
        //navOptions.add(new GroupOption("Events", new ArrayList<ChildOption>(), R.drawable.events, true, new EventDisplay(), true, false));




        // ABOUT 105
        List<ChildOption> aboutList = new ArrayList<>();
        aboutList.add(new ChildOption("Calendar", R.drawable.events, Website.newInstance(CAL_URL, false), true));
        aboutList.add(new ChildOption("History", R.drawable.hourglass, new HistoryFrag(), false));
        aboutList.add(new ChildOption("Teams", R.drawable.team, new TeamFrag(), false));
        navOptions.add(new GroupOption("About 105", aboutList, R.drawable.infoblack, false, null, false, false));

        // CADET RESOURCES
        List<ChildOption> resList = new ArrayList<>();
        // implement later
        //resList.add(new ChildOption("Summer Training", R.drawable.sun));
        //resList.add(new ChildOption("Documents", R.drawable.documents));
        resList.add(new ChildOption("Remind", R.drawable.announcement, new NewsFragment(), false));
        resList.add(new ChildOption("Absence Reporting", R.drawable.phone, Website.newInstance(ABSENCE_URL), true));
        resList.add(new ChildOption("Summer Training", R.drawable.sun, Website.newInstance(SUMMER_URL), true));
        if (isStaff) {resList.add(new ChildOption("Uniform Inspections", R.drawable.uniform, Website.newInstance(UNIFORM_URL), true)); }; // uniform inspections GAS
        navOptions.add(new GroupOption("Resources", resList, R.drawable.documents, false, null, false, true));

        // ONLINE
        List<ChildOption> findList = new ArrayList<>();

        // These 3 fragments are no longer fragments, but just an intent opening their urls.
        // all Website.newInstance's can be replaced with null
        findList.add(new ChildOption(facebook, R.drawable.facebook1,
                Website.newInstance("https://www.facebook.com/groups/105RCACC/"), true));
        findList.add(new ChildOption(website, R.drawable.wifi,
                Website.newInstance("http://www.105armycadets.ca/main/"), true));
        findList.add(new ChildOption(instagram, R.drawable.camera,
                Website.newInstance("https://www.instagram.com/105army/"), true));
        navOptions.add(new GroupOption("Find Us Online", findList, R.drawable.computer, false, null, false, true));

        navOptions.add(new GroupOption("Join", new ArrayList<ChildOption>(), R.drawable.personplus, true, new JoinFrag(), false, false));

//        navOptions.add(new GroupOption("Info", new ArrayList<ChildOption>(), R.drawable.here, true, new InfoFrag(), false, false));

        if (isAdmin) {
            navOptions.add(new GroupOption("Debug", new ArrayList<ChildOption>(), R.drawable.arrow, true, new DebugFrag(), false, false));
        };

        return navOptions;
    }

}


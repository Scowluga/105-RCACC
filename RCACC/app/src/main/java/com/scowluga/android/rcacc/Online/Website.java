package com.scowluga.android.rcacc.Online;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.scowluga.android.rcacc.Main.OptionProvider;
import com.scowluga.android.rcacc.R;

import static com.scowluga.android.rcacc.Main.MainActivity.toolbar;

/**
 * A simple {@link Fragment} subclass.
 */
public class Website extends Fragment {

    public static String URL = "url"; //key for the url
    public static String JAVASCRIPT = "script";

    private static boolean landTrue = false;

    private WebView webView; // for on back pressed
    private String websiteUrl;

    public Website() {
        // Required empty public constructor
    }

    public static Website newInstance(String url) {

        Bundle args = new Bundle();
        args.putString(URL, url);
        args.putBoolean(JAVASCRIPT, true);
        Website fragment = new Website();
        fragment.setArguments(args);
        return fragment;
    }

    public static Website newInstance(String url, boolean script) {

        Bundle args = new Bundle();
        args.putString(URL, url);
        args.putBoolean(JAVASCRIPT, script);
        Website fragment = new Website();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.online_website_frag, container, false);

        Bundle args = getArguments();

        String url = args.getString(URL);
        websiteUrl = url;

        //IMPLEMENT THIS: have calendar open in browser so can go back
        final Boolean scriptTrue = args.getBoolean(JAVASCRIPT);

        WebView webView = (WebView) v.findViewById(R.id.web_display);
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getContext(), "Error:" + description, Toast.LENGTH_SHORT).show();

            }
        });
        webView.loadUrl(url);

        return v;
    }

    public WebView getWebView() {
        return webView;
    }

    @Override
    public void onResume() {
//        Toast.makeText(getContext(), websiteUrl, Toast.LENGTH_SHORT).show();

        switch (websiteUrl) {
            case OptionProvider.ABSENCE_URL:
                toolbar.setTitle("Absence Reporting");
                break;
            case OptionProvider.SUMMER_URL:
                toolbar.setTitle("Summer Training");
                break;
            case OptionProvider.UNIFORM_URL:
                toolbar.setTitle("Uniform Inspections");
                break;
            case OptionProvider.CAL_URL:
                toolbar.setTitle("Calendar");
                break;

        }

        super.onResume();
    }
}

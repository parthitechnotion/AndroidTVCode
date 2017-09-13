package com.googleandroidtv.custom_app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.googleandroidtv.R;


public class ApplicationDetailsActivity extends Activity {

    public static final String APPLICATIONS = "Applications";
    public static final String MOVIE = "Movie";
    public static final String SHARED_ELEMENT_NAME = "hero";
    public static final String NOTIFICATION_ID = "ID";
    public static final String APPMODEL = "AppMODEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_example);
        if (savedInstanceState == null) {
            ApplicationDetailsFragment fragment = new ApplicationDetailsFragment();
            getFragmentManager().beginTransaction().replace(R.id.details_fragment, fragment).commit();
        }
    }

}

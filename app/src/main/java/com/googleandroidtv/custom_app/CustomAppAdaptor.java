package com.googleandroidtv.custom_app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.googleandroidtv.MainActivity;
import com.googleandroidtv.R;
import com.googleandroidtv.apps.AppsAdapter;
import com.googleandroidtv.apps.AppsRanker;
import com.googleandroidtv.apps.LaunchPointListGenerator;
import com.googleandroidtv.widget.RowViewAdapter;

/**
 * Created by cd on 31-08-2017.
 */

public class CustomAppAdaptor extends AppsAdapter {
    public CustomAppAdaptor(Context context, int appType, LaunchPointListGenerator launchPointListGenerator, AppsRanker appsRanker, ActionOpenLaunchPointListener actionOpenLaunchPointListener) {
        super(context, appType, launchPointListGenerator, appsRanker, actionOpenLaunchPointListener);
    }
}


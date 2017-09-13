package com.googleandroidtv.custom_app;

import android.app.Activity;
import android.os.Bundle;

import com.googleandroidtv.R;

/**
 * {@link VerticalGridActivity} loads {@link VerticalCatGridFragment}
 */
public class VerticalGridActivity extends Activity {

    private static final String TAG = VerticalGridActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_grid);
    }
}

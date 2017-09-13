package com.googleandroidtv.custom_app;

import android.content.Context;

/**
 * Created by cd on 11-09-2017.
 */

public class CustomUtils {
    public static final String SERVER_IMAGE_PATH = "http://itechnotion.in/androidtv/upload/";

    public static int convertDpToPixel(Context ctx, int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}

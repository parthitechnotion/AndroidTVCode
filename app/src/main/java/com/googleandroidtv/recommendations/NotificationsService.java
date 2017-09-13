package com.googleandroidtv.recommendations;

import android.util.Log;

import com.googleandroidtv.tvrecommendations.service.BaseNotificationsService;

public class NotificationsService extends BaseNotificationsService {
    private static final String TAG = "NotificationsService";
    public NotificationsService() {
        super(false);
        Log.i(TAG, "NotificationsService constructor");
    }
}

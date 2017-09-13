package com.googleandroidtv.recommendations;

import android.util.Log;

import com.googleandroidtv.tvrecommendations.service.BaseRecommendationsService;

public class RecommendationsService extends BaseRecommendationsService {
    private static final String TAG = "RecommendationsService";
    public RecommendationsService() {
        super(false, NotificationsService.class);
        Log.i(TAG, "RecommendationsService  constructor");
    }
}

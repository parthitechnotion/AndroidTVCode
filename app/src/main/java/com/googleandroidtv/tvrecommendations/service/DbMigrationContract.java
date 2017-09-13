package com.googleandroidtv.tvrecommendations.service;

import android.net.Uri;

public interface DbMigrationContract {
    public static final Uri CONTENT_UPDATE_URI =  Uri.parse("content://com.android.google.tvrecommendations.migrationtv/migrated");
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.google.tvrecommendations.migrationtv/data");

   /* static {
        CONTENT_URI = Uri.parse("content://com.android.google.tvrecommendations.migrationtv/data");
        CONTENT_UPDATE_URI = Uri.parse("content://com.android.google.tvrecommendations.migrationtv/migrated");
    }*/
}

package com.googleandroidtv.util;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.googleandroidtv.apps.LaunchPoint;
import com.googleandroidtv.apps.LaunchPointListGenerator;
import com.googleandroidtv.data.ConstData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import momo.cn.edu.fjnu.androidutils.data.CommonValues;

/**
 * Created by GaoFei on 2017/6/6.
 */

public class NetWorkUtils {
    private static String[] sSpecialSettingsActions;

    public static int getCurrentNetWrokState() {
        ConnectivityManager connectivityManager = (ConnectivityManager) CommonValues.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null || connectivityManager.getActiveNetworkInfo() == null)
            return ConstData.NetWorkState.NO;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        int networkType = networkInfo.getType();
        if (networkType == ConnectivityManager.TYPE_WIFI)
            return ConstData.NetWorkState.WIFI;
        else if (networkType == ConnectivityManager.TYPE_ETHERNET)
            return ConstData.NetWorkState.ETHERNET;
        return ConstData.NetWorkState.NO;
    }

    public static int getWifiStrength() {
        int strength = 0;
        WifiManager wifiManager = (WifiManager) CommonValues.application.getSystemService(Context.WIFI_SERVICE); // 取得WifiManager对象
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null) {
            strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
        }
        return strength;
    }

    public static String getWifiSSID() {
        WifiManager wifiManager = (WifiManager) CommonValues.application.getSystemService(Context.WIFI_SERVICE); // 取得WifiManager对象
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null) {
            return info.getSSID();
        }
        return "";
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) CommonValues.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    static {
        sSpecialSettingsActions = new String[]{"android.settings.WIFI_SETTINGS"};
    }

    public static LaunchPoint createSettingsList() {
        //  Intent mainIntent = new Intent("android.intent.action.MAIN");
        Intent mainIntent = new Intent();
        //  mainIntent.addCategory("android.intent.category.LEANBACK_SETTINGS");
        LaunchPoint settingsItems = null;
        PackageManager pkgMan = CommonValues.application.getPackageManager();
        List<ResolveInfo> rawLaunchPoints = pkgMan.queryIntentActivities(mainIntent, 129);
        HashMap<ComponentName, Integer> specialEntries = new HashMap();
        for (int i = 0; i < sSpecialSettingsActions.length; i++) {
            specialEntries.put(getComponentNameForSettingsActivity(sSpecialSettingsActions[i]), Integer.valueOf(i));
        }
        int size = rawLaunchPoints.size();
        for (int ptr = 0; ptr < size; ptr++) {
            ResolveInfo info = (ResolveInfo) rawLaunchPoints.get(ptr);
            boolean system = (info.activityInfo.applicationInfo.flags & 1) != 0;
            ComponentName comp = getComponentName(info);
            int type = -1;
            if (specialEntries.containsKey(comp)) {
                type = ((Integer) specialEntries.get(comp)).intValue();
            }
            if (info.activityInfo != null && system) {
                if (info.activityInfo.packageName.equals("com.android.tv.settings") && info.activityInfo.name.equals("com.android.tv.settings.device.display.DisplayActivity")) {
                    LaunchPoint lp = new LaunchPoint(CommonValues.application, pkgMan, info, false, type);
                    lp.addLaunchIntentFlags(32768);
                    settingsItems = lp;
                }
            }
        }
        return settingsItems;
    }


    public static ComponentName getComponentNameForSettingsActivity(String action) {
        Intent mainIntent = new Intent(action);
        mainIntent.addCategory("android.intent.category.LEANBACK_SETTINGS");
        List<ResolveInfo> launchPoints = CommonValues.application.getPackageManager().queryIntentActivities(mainIntent, 129);
        if (launchPoints.size() > 0) {
            int size = launchPoints.size();
            for (int ptr = 0; ptr < size; ptr++) {
                ResolveInfo info = (ResolveInfo) launchPoints.get(ptr);
                boolean system = (info.activityInfo.applicationInfo.flags & 1) != 0;
                if (info.activityInfo != null && system) {
                    return getComponentName(info);
                }
            }
        }
        return null;
    }

    public static ComponentName getComponentName(ResolveInfo info) {
        if (info == null) {
            return null;
        }
        return new ComponentName(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);
    }


    public static boolean IsUsageStatePermissionGranted() {
        boolean granted = false;
        AppOpsManager appOps = (AppOpsManager) CommonValues.application.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), CommonValues.application.getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (CommonValues.application.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }
        return granted;
    }


    public static List<UsageStats> getUsageStatistics() {
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) CommonValues.application.getSystemService(Context.USAGE_STATS_SERVICE);/* Set up rows with light data. done in main thread. */
        final long timeEnd = System.currentTimeMillis();
        final long timeBegin = timeEnd - 50 * 1000; // +50 sec.
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        List<UsageStats> queryUsageStatsTemp = new ArrayList<>();
        List<UsageStats> queryUsageStats = new ArrayList<>();
        queryUsageStatsTemp.clear();
        queryUsageStats.clear();
        queryUsageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, timeBegin, timeEnd);
        Log.e("App size :", queryUsageStats.size() + "");

        for (final UsageStats usageStats : queryUsageStats) {
            PackageManager packageManager = CommonValues.application.getPackageManager();
            ApplicationInfo applicationInfo = new ApplicationInfo();
            try {
                applicationInfo = packageManager.getApplicationInfo(usageStats.getPackageName(), 0);
            } catch (final PackageManager.NameNotFoundException e) {
            }
            if (!usageStats.getPackageName().equals("com.googleandroidtv")) {
                if ((applicationInfo.flags & applicationInfo.FLAG_SYSTEM) > 0) {
                } else {
                    queryUsageStatsTemp.add(usageStats);
                }
            }
        }
        //return queryUsageStats;
        return queryUsageStatsTemp;
    }

}
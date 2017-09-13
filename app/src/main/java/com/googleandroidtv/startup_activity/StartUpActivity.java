package com.googleandroidtv.startup_activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.googleandroidtv.MainActivity;
import com.googleandroidtv.R;
import com.googleandroidtv.apps.LaunchPoint;
import com.googleandroidtv.data.ConstData;
import com.googleandroidtv.util.Util;

import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;

public class StartUpActivity extends Activity implements View.OnClickListener {
    Button btnLanguage,btnNext;
    boolean ActionStarted = false;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        sp = StartUpActivity.this.getSharedPreferences("CustomPref",0);
        String  IsFirstLoad =  sp.getString("ISFIRSTLOAD","");
        if(IsFirstLoad.equals("false")) {
            Intent intent = new Intent(StartUpActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
       //String IsFirstLoad = StorageUtils.getDataFromSharedPreference("ISFIRSTLOAD");
       // Log.e("IsFirstLoad - ",IsFirstLoad);

         // StorageUtils.saveDataToSharedPreference("ISFIRSTLOAD", "true");
     //   intent = Util.getSettingLaunchIntent("com.android.tv.settings","com.android.tv.settings.system.LanguageActivity");
        // intent = getSettingLaunchIntent("com.android.tv.settings","com.android.tv.settings.connectivity.NetworkActivity");
        // intent = getSettingLaunchIntent("com.android.tv.settings","com.android.tv.settings.system.DateTimeActivity");
        //   intent = getSettingLaunchIntent("com.android.tv.settings","com.android.tv.settings.device.display.DisplayActivity");

        //startActivityForResult(new Intent(Settings.ACTION_LOCALE_SETTINGS), LANGUAGE);
        //startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), INTERNET);
        //startActivityForResult(new Intent(Settings.ACTION_DATE_SETTINGS), TIME);
        // startActivityForResult(new Intent(Settings.ACTION_DISPLAY_SETTINGS), SCREEN);

        btnLanguage =(Button) findViewById(R.id.btnLanguage);
        btnLanguage.setOnClickListener(this);
        btnNext =(Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btnLanguage:
                ActionStarted = true;
                 intent = Util.getSettingLaunchIntent("com.android.tv.settings","com.android.tv.settings.system.LanguageActivity");
                startActivityForResult(intent,11);
                btnNext.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                btnNext.setTextColor(getResources().getColor(android.R.color.white));
                break;
            case R.id.btnNext:
                startActivity(new Intent(StartUpActivity.this, InternetActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ActionStarted){
            btnNext.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            btnNext.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 11){
            btnNext.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            btnNext.setTextColor(getResources().getColor(android.R.color.white));
        }
    }
}

package com.googleandroidtv.startup_activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.googleandroidtv.MainActivity;
import com.googleandroidtv.R;
import com.googleandroidtv.util.NetWorkUtils;

public class WebviewActivity extends Activity {
    WebView mWebview;
    Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        mWebview = findViewById(R.id.mWebview);
        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if(NetWorkUtils.IsUsageStatePermissionGranted()) {
               SharedPreferences sp = WebviewActivity.this.getSharedPreferences("CustomPref",0);
               SharedPreferences.Editor editor = sp.edit();
               editor.putString("ISFIRSTLOAD","false");
               editor.commit();

                Intent intent = new Intent(WebviewActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }else{
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
            }
        });

        if (NetWorkUtils.isNetworkAvailable()) {
            WebSettings webSettings = mWebview.getSettings();
            webSettings.setJavaScriptEnabled(true);

            // Other webview options
            webSettings.setLoadWithOverviewMode(true);

            // webSettings.setBuiltInZoomControls(true);
            webSettings.setPluginState(WebSettings.PluginState.ON);
            webSettings.setAllowFileAccess(true);
            webSettings.setSupportZoom(true);
            webSettings.setGeolocationEnabled(true);

            final ProgressDialog mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setCancelable(false);

            // mWebView.setWebChromeClient(new GeoWebChromeClient());

            mWebview.setWebViewClient(new WebViewClient() {

                @Override
                public void onLoadResource(WebView view, String url) {
                    super.onLoadResource(view, url);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    if (!mProgressDialog.isShowing()) {
                        mProgressDialog.show();
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }
            });

            mWebview.loadUrl("http://google.co.in");

        } else {
            Toast.makeText(WebviewActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }

    }
}

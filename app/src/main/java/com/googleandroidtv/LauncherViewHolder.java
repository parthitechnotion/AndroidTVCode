package com.googleandroidtv;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.googleandroidtv.R;
import com.googleandroidtv.apps.BannerView;
import com.googleandroidtv.core.LaunchException;
import com.googleandroidtv.settings.FullScreenSettingsActivity;
import com.googleandroidtv.util.Util;

public abstract class LauncherViewHolder extends ViewHolder implements OnClickListener {
    private static final String TAG = "LauncherViewHolder";
    protected final Context mCtx;
    private int mLaunchColor;
    private Intent mLaunchIntent;
    private boolean mLaunchTranslucent;

    /* renamed from: LauncherViewHolder.1 */
    class C01571 implements Runnable {
        C01571() {
        }

        public void run() {
            try {
                LauncherViewHolder.this.performLaunch();
            } catch (LaunchException e) {
                Log.e("LauncherViewHolder", "Could not perform launch:", e);
                Toast.makeText(LauncherViewHolder.this.mCtx, R.string.failed_launch, 0).show();
            }
        }
    }

    protected LauncherViewHolder(View v) {
        super(v);
        this.mCtx = v.getContext();
        v.setOnClickListener(this);
    }

    public void onClick(View v) {
        if(v instanceof BannerView && ((BannerView)v).mIsAddItem){
            //启动添加页面
            Log.i(TAG, "onClick->addItem->className:" + v.getContext().getClass().getName());
            Context context =  v.getContext();
            if(context != null && context instanceof MainActivity){
                ((MainActivity)context).preformIconMoreClick();
            }
            //Toast.makeText(v.getContext(), "启动添加页面", Toast.LENGTH_SHORT).show();
            return;
        }
        if (v != null && v == this.itemView) {
            ((MainActivity) this.mCtx).beginLaunchAnimation(v, this.mLaunchTranslucent, this.mLaunchColor, new C01571());
        }
    }

    protected final void setLaunchTranslucent(boolean launchTranslucent) {
        this.mLaunchTranslucent = launchTranslucent;
    }

    protected final void setLaunchColor(int launchColor) {
        this.mLaunchColor = launchColor;
    }

    protected final void setLaunchIntent(Intent launchIntent) {
        this.mLaunchIntent = launchIntent;
    }

    protected void performLaunch() {
        try {
            if(this.mLaunchIntent==null){
            //    this.mLaunchIntent = getCustomLaunchIntent();
              /*  Intent dummyIntent = new Intent();
                dummyIntent.setClass(this.mCtx, DummyActivity.class);
                PendingIntent pendingIntent =  PendingIntent.getActivity(this.mCtx, 0, dummyIntent, 0);
                Util.startActivity(this.mCtx,pendingIntent);*/
               // Util.startActivity(((MainActivity)mCtx).mContext,FullScreenSettingsActivity.class);
               // ((MainActivity)mCtx).startNewActivity(this.mLaunchIntent.getExtras().getString("CatID"));
              //  this.mCtx.startActivity(this.mLaunchIntent);
            }else {
              //  Log.e("Intentextra-",""+this.mLaunchIntent.hasExtra("CatID"));
                if(this.mLaunchIntent.hasExtra("CatID")){
                   // Log.e("Intentextrav-",""+this.mLaunchIntent.getExtras().getString("CatID"));
                    ((MainActivity)mCtx).startNewActivity(this.mLaunchIntent.getExtras().getString("CatID"));
                }else {
                    this.mCtx.startActivity(this.mLaunchIntent);
                }
            }
            onLaunchSucceeded();
        } catch (Throwable t) {
            LaunchException launchException = new LaunchException("Failed to launch intent: " + this.mLaunchIntent, t);
        }
    }

    protected void onLaunchSucceeded() {
    }


    private Intent getCustomLaunchIntent(){
        /*ComponentName componentName = new ComponentName("com.googleandroidtv", "com.googleandroidtv.DummyActivity");
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setComponent(componentName);
        intent.addFlags(270532608);
        return intent;*/
        Intent intent1 = new Intent();
        intent1.setClass(((MainActivity)mCtx).mContext,FullScreenSettingsActivity.class);
        return intent1;
    }


}

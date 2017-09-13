package com.googleandroidtv.custom_app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.widget.Toast;

import com.googleandroidtv.DummyActivity;
import com.googleandroidtv.R;

import com.google.gson.Gson;
import com.googleandroidtv.asyncloopj.AsyncLoopj;
import com.googleandroidtv.bean.ApplicationDetailsBean;
import com.googleandroidtv.bean.RelatedApplicationListBean;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by corochann on 6/7/2015.
 */
public class ApplicationDetailsFragment extends DetailsFragment {

    private static final String TAG = ApplicationDetailsFragment.class.getSimpleName();

    private static final int ACTION_PLAY_VIDEO = 1;
    private static final int ACTION_INSTALL= 2;
    private static final int ACTION_OPEN= 3;
    private static final int ACTION_PLAYSTORE= 4;
    private static final int ACTION_UPGRADE= 5;
    private static final int ACTION_UNINSTALL= 6;

    private static final int FULL_WIDTH_DETAIL_THUMB_WIDTH = 220;
    private static final int FULL_WIDTH_DETAIL_THUMB_HEIGHT = 120;

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    public static final String CATEGORY_FULL_WIDTH_DETAILS_OVERVIEW_ROW_PRESENTER = "FullWidthDetailsOverviewRowPresenter";
    public static final String CATEGORY_DETAILS_OVERVIEW_ROW_PRESENTER = "DetailsOverviewRowPresenter";

    /* Attribute */
    private ArrayObjectAdapter mAdapter;
    private CustomFullWidthDetailsOverviewRowPresenter mFwdorPresenter;
    private ClassPresenterSelector mClassPresenterSelector;
    private ListRow mRelatedVideoRow = null;

    private DetailsRowBuilderTask mDetailsRowBuilderTask;

    /* Relation */
    private PicassoBackgroundManager mPicassoBackgroundManager;
    String AppId;
    private boolean isAppInstalled = false;
    boolean ActivityStart = true;

    ApplicationDetailsBean applicationDetailsBeenList;
    RelatedApplicationListBean[] relatedApplicationListBeen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mFwdorPresenter = new CustomFullWidthDetailsOverviewRowPresenter(new AppDetailsDescriptionPresenter(getActivity()));
    //    mDorPresenter = new CustomDetailsOverviewRowPresenter(new DetailsDescriptionPresenter(), getActivity());

        mPicassoBackgroundManager = new PicassoBackgroundManager(getActivity());
       // mSelectedApp = getActivity().getIntent().getParcelableExtra(DetailsActivity.APPLICATIONS);
        AppId = getActivity().getIntent().getStringExtra("AppID");

        getAppDetails(AppId);

      /*  mDetailsRowBuilderTask = (DetailsRowBuilderTask) new DetailsRowBuilderTask().execute(mSelectedMovie);
        setOnItemViewClickedListener(new ItemViewClickedListener());
        mPicassoBackgroundManager.updateBackgroundWithDelay(mSelectedMovie.getCardImageUrl());*/
    }

    private void getAppDetails(String appId) {

    //    http://itechnotion.in/androidtv/api.php?app_id=1
        RequestParams params =  new RequestParams();
        params.put("app_id","1");
        AsyncLoopj.get("api.php",params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    if(jsonObject.getString("status").equals("1")){
                        JSONArray array = jsonObject.getJSONArray("result");
                        JSONObject object = array.getJSONObject(0);

                        applicationDetailsBeenList =  new Gson().fromJson(object.toString(), ApplicationDetailsBean.class);
                        relatedApplicationListBeen = new Gson().fromJson(object.getString("related"), RelatedApplicationListBean[].class);

                        mDetailsRowBuilderTask = (DetailsRowBuilderTask) new DetailsRowBuilderTask().execute(applicationDetailsBeenList);
                        setOnItemViewClickedListener(new ItemViewClickedListener());
                        //  setOnItemViewSelectedListener(new ItemViewSelectedListener());
                        mPicassoBackgroundManager.updateBackgroundWithDelay(CustomUtils.SERVER_IMAGE_PATH+applicationDetailsBeenList.getTile_image());

                    }else{
                        Toast.makeText(getActivity(),"Data not found",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

        /*RestClient.get().getAppDetails(appId).enqueue(new Callback<AppDetailsJsonBean>() {
            @Override
            public void onResponse(Call<AppDetailsJsonBean> call, Response<AppDetailsJsonBean> response) {

                if(response.isSuccessful()){
                    if(response.body().getMovieReview().size()>0){
                        mSelectedApp = response.body().getMovieReview().get(0);
                        RelatedAppList = response.body().getMovieReview().get(0).getRelated();

                        mDetailsRowBuilderTask = (DetailsRowBuilderTask) new DetailsRowBuilderTask().execute(mSelectedApp);
                        setOnItemViewClickedListener(new ItemViewClickedListener());
                      //  setOnItemViewSelectedListener(new ItemViewSelectedListener());
                        mPicassoBackgroundManager.updateBackgroundWithDelay(Utils.SERVER_IMAGE_UPFOLDER_THUMB+mSelectedApp.getMovie_image());

                    }
                }
            }

            @Override
            public void onFailure(Call<AppDetailsJsonBean> call, Throwable t) {

            }
        });*/
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mClassPresenterSelector = new ClassPresenterSelector();
    //    Log.v(TAG, "mFwdorPresenter.getInitialState: " + mFwdorPresenter.getInitialState());
       // if(CatId.equals("6")) {
            /* If category name is "DetailsOverviewRowPresenter", show DetailsOverviewRowPresenter for demo purpose (this class is deprecated from API level 22) */
       //     mClassPresenterSelector.addClassPresenter(DetailsOverviewRow.class, mDorPresenter);
     //   } else {
            /* Default behavior, show FullWidthDetailsOverviewRowPresenter */
        mFwdorPresenter.setInitialState(FullWidthDetailsOverviewRowPresenter.ALIGN_MODE_MIDDLE);
            mClassPresenterSelector.addClassPresenter(DetailsOverviewRow.class, mFwdorPresenter);
       // }
        mClassPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());

        mAdapter = new ArrayObjectAdapter(mClassPresenterSelector);
        setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        mDetailsRowBuilderTask.cancel(true);
        super.onStop();
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof RelatedApplicationListBean) {
                RelatedApplicationListBean movie = (RelatedApplicationListBean) item;
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), ApplicationDetailsActivity.class);
                intent.putExtra("AppID",movie.getRel_app_id());
               // intent.putExtra(DetailsActivity.MOVIE, movie);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(), ApplicationDetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            }
        }
    }

   /* private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            if( item != null ){
                if(item instanceof RelatedAppListBean){
                mPicassoBackgroundManager.updateBackgroundWithDelay(((RelatedAppListBean) item).getRelMovie_imageeURI(Utils.SERVER_IMAGE_UPFOLDER_THUMB));
                }else{
                    mPicassoBackgroundManager.updateBackgroundWithDelay(((AppDetailsBean) item).getMovie_imageeURI(Utils.SERVER_IMAGE_UPFOLDER_THUMB));
                }
            } else {
                Log.w(TAG, "item is null");
            }

        }
    }*/

    private class DetailsRowBuilderTask extends AsyncTask<ApplicationDetailsBean, Integer, DetailsOverviewRow> {
        @Override
        protected DetailsOverviewRow doInBackground(ApplicationDetailsBean... params) {
            Log.v(TAG, "DetailsRowBuilderTask doInBackground");
            int width, height;
       //     if(CatId.equals("6")) {
                /* If category name is "DetailsOverviewRowPresenter", show DetailsOverviewRowPresenter for demo purpose (this class is deprecated from API level 22) */
   //             width = DETAIL_THUMB_WIDTH;
    //            height = DETAIL_THUMB_HEIGHT;
          //  } else {
                /* Default behavior, show FullWidthDetailsOverviewRowPresenter */
                width = FULL_WIDTH_DETAIL_THUMB_WIDTH;
                height = FULL_WIDTH_DETAIL_THUMB_HEIGHT;
          //  }

            DetailsOverviewRow row = new DetailsOverviewRow(applicationDetailsBeenList);
            try {
                // Bitmap loading must be done in background thread in Android.
                Bitmap poster = Picasso.with(getActivity())
                        .load(CustomUtils.SERVER_IMAGE_PATH+applicationDetailsBeenList.getTile_image().trim().replace(" ","%20"))
                        .resize(CustomUtils.convertDpToPixel(getActivity().getApplicationContext(), width), CustomUtils.convertDpToPixel(getActivity().getApplicationContext(), height))
                        .centerCrop()
                        .get();
                row.setImageBitmap(getActivity(), poster);

              //  mVideoLists = VideoProvider.buildMedia(getActivity());
            } catch (IOException e) {
                Log.w(TAG, e.toString());
            }
            return row;
        }

        @Override
        protected void onPostExecute(DetailsOverviewRow row) {
            Log.v(TAG, "DetailsRowBuilderTask onPostExecute");
            /* 1st row: DetailsOverviewRow */

              /* action setting*/
            SparseArrayObjectAdapter sparseArrayObjectAdapter = new SparseArrayObjectAdapter();
           /* sparseArrayObjectAdapter.set(0, new Action(ACTION_PLAY_VIDEO, "Play Video"));
            sparseArrayObjectAdapter.set(1, new Action(1, "Action 2", "label"));
            sparseArrayObjectAdapter.set(2, new Action(2, "Action 3", "label"));*/
            isAppInstalled = isPackageInstalled(applicationDetailsBeenList.getPackage_name(),getActivity());
            if(isAppInstalled) {
                sparseArrayObjectAdapter.set(1, new Action(ACTION_OPEN, "OPEN"));
                sparseArrayObjectAdapter.set(4, new Action(ACTION_UNINSTALL, "UNINSTALL"));
            }else{
                sparseArrayObjectAdapter.set(1, new Action(ACTION_INSTALL, "INSTALL"));
            }
            sparseArrayObjectAdapter.set(2, new Action(ACTION_PLAYSTORE, "SEE IN PLAY STORE"));
            sparseArrayObjectAdapter.set(3, new Action(ACTION_UPGRADE, "UPGRADE"));

            row.setActionsAdapter(sparseArrayObjectAdapter);

            mFwdorPresenter.setOnActionClickedListener(new DetailsOverviewRowActionClickedListener());
       //     mDorPresenter.setOnActionClickedListener(new DetailsOverviewRowActionClickedListener());

            /* 2nd row: ListRow CardPresenter */

            if (relatedApplicationListBeen == null) {
                // Error occured while fetching videos
                Log.i(TAG, "mVideoLists is null, skip creating mRelatedVideoRow");
            } else {
                RelatedCardViewPresenter cardPresenter = new RelatedCardViewPresenter(getActivity());
                    ArrayObjectAdapter cardRowAdapter = new ArrayObjectAdapter(cardPresenter);
                for (RelatedApplicationListBean categoryBean : relatedApplicationListBeen) {
                    cardRowAdapter.add(categoryBean);
                }
                    //HeaderItem header = new HeaderItem(index, entry.getKey());
                    HeaderItem header = new HeaderItem(0, "Related Applications");
                    mRelatedVideoRow = new ListRow(header, cardRowAdapter);
            }

            /* 2nd row: ListRow */
/*            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());

            ArrayList<Movie> mItems = MovieProvider.getMovieItems();
            for (Movie movie : mItems) {
                listRowAdapter.add(movie);
            }
            HeaderItem headerItem = new HeaderItem(0, "Related Videos");
*/

            mAdapter = new ArrayObjectAdapter(mClassPresenterSelector);
            /* 1st row */
            mAdapter.add(row);

            /* 2nd row */
            if(mRelatedVideoRow != null){
                mAdapter.add(mRelatedVideoRow);
            }
            //mAdapter.add(new ListRow(headerItem, listRowAdapter));

            /* 3rd row */
            //adapter.add(new ListRow(headerItem, listRowAdapter));
            setAdapter(mAdapter);
        }
    }

    private boolean isPackageInstalled(String pname, Context context) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(pname, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            app_installed = false;
        }
        return app_installed;
    }

    @Override
    public void onResume() {
        Log.e("OnResume","OnResumeCalledDetail");
        if(!ActivityStart) {
            mDetailsRowBuilderTask = (DetailsRowBuilderTask) new DetailsRowBuilderTask().execute(applicationDetailsBeenList);
           // setOnItemViewClickedListener(new ItemViewClickedListener());
            //  setOnItemViewSelectedListener(new ItemViewSelectedListener());
            mPicassoBackgroundManager.updateBackgroundWithDelay(CustomUtils.SERVER_IMAGE_PATH + applicationDetailsBeenList.getTile_image());
        }
        super.onResume();
    }

    public class DetailsOverviewRowActionClickedListener implements OnActionClickedListener {
        @Override
        public void onActionClicked(Action action) {
            if(action.getId() == ACTION_INSTALL){
               Intent intent = new Intent(getActivity().getBaseContext(), DummyActivity.class);//InstallDialogActivity
               // Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity()).toBundle();
                Bundle extras = new Bundle();
                extras.putSerializable("movie", applicationDetailsBeenList);
                intent.putExtras(extras);
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity()).toBundle();
                startActivity(intent,bundle);
            }else if(action.getId() == ACTION_OPEN){
                Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(applicationDetailsBeenList.getPackage_name());
                startActivity(launchIntent);
              //  Toast.makeText(getActivity(),"ACTION_OPEN",Toast.LENGTH_SHORT).show();
            }else if(action.getId() == ACTION_PLAYSTORE){
           //     Toast.makeText(getActivity(),"ACTION_PLAYSTORE",Toast.LENGTH_SHORT).show();
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + applicationDetailsBeenList.getPackage_name()));
                    startActivity(goToMarket);
            }else if(action.getId() == ACTION_UPGRADE){
                Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + applicationDetailsBeenList.getPackage_name()));
                startActivity(goToMarket);
          //      Toast.makeText(getActivity(),"ACTION_UPGRADE",Toast.LENGTH_SHORT).show();
            }else if(action.getId() == ACTION_UNINSTALL){
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:" + applicationDetailsBeenList.getPackage_name()));
                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(getActivity(),"ACTION_UNINSTALL",Toast.LENGTH_SHORT).show();
            }
            ActivityStart = false;
        }
    }
}
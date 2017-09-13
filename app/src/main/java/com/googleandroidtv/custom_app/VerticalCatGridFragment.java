package com.googleandroidtv.custom_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.googleandroidtv.DummyActivity;
import com.googleandroidtv.R;
import com.googleandroidtv.asyncloopj.AsyncLoopj;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * VerticalGridFragment shows contents with vertical alignment
 */
public class VerticalCatGridFragment extends VerticalGridFragment {

    private static final String TAG = VerticalGridFragment.class.getSimpleName();
    private static final int NUM_COLUMNS = 5;
    private static final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM;
    private ArrayObjectAdapter mAdapter;
    private PicassoBackgroundManager picassoBackgroundManager;

    SpinnerFragment mSpinnerFragment;
    String CatID;
    SubCategoryBean[] SubCategotriesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        CatID = getActivity().getIntent().getStringExtra("CatID");
      //  picassoBackgroundManager = new PicassoBackgroundManager(getActivity());

        setTitle(getResources().getString(R.string.applications));
        //setBadgeDrawable(getResources().getDrawable(R.drawable.app_icon_your_company));

        //   db = new DatabaseHandler(getActivity());
        setupFragment();
        setupEventListeners();

        // it will move current focus to specified position. Comment out it to see the behavior.
        // setSelectedPosition(5);

        getAllAppListFromServer();
    }

    private void getAllAppListFromServer() {
        mSpinnerFragment = new SpinnerFragment();
        getFragmentManager().beginTransaction().add(R.id.vertical_grid_fragment, mSpinnerFragment).commit();

        RequestParams params = new RequestParams();
        params.put("cat_id",CatID);
        String Url = "category-by-id.php?";
        AsyncLoopj.get(Url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                getFragmentManager().beginTransaction().remove(mSpinnerFragment).commit();
                try {
                    JSONArray array = new JSONArray(response.toString());
                    JSONObject object = array.getJSONObject(0);
                    SubCategotriesList = new Gson().fromJson(object.getString("subgroup_list"), SubCategoryBean[].class);

                    mAdapter = new ArrayObjectAdapter(new VideoCardViewPresenter(getActivity(), R.style.VideoGridCardTheme));
                    if(SubCategotriesList.length > 0) {
                        for (SubCategoryBean categoryBean : SubCategotriesList) {
                            mAdapter.add(categoryBean);
                        }
                        setAdapter(mAdapter);
                    }else{
                        Toast.makeText(getActivity(),"Data not found",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                getFragmentManager().beginTransaction().remove(mSpinnerFragment).commit();
                super.onFailure(statusCode, headers, responseString, throwable);

            }
        });

    /*    RestClient.get().getAppList("19").enqueue(new Callback<AppListJsonBean>() {
            @Override
            public void onResponse(Call<AppListJsonBean> call, Response<AppListJsonBean> response) {
                getFragmentManager().beginTransaction().remove(mSpinnerFragment).commit();
                mAdapter = new ArrayObjectAdapter(new AppListSerCardPresenter());
                ArrayAppList = response.body().getMovieReview();
                if(ArrayAppList.size()>0) {
                    for (int i = 0; i < ArrayAppList.size(); i++) {
                        mAdapter.add(ArrayAppList.get(i));
                    }
                    setAdapter(mAdapter);
                }else{
                    Toast.makeText(getActivity(),"Data not found",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AppListJsonBean> call, Throwable t) {
                getFragmentManager().beginTransaction().remove(mSpinnerFragment).commit();
                Toast.makeText(getActivity(),"Data not found",Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void setupFragment() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(ZOOM_FACTOR);
        gridPresenter.setNumberOfColumns(NUM_COLUMNS);
        setGridPresenter(gridPresenter);
      //  mAdapter = new ArrayObjectAdapter(new SubCategoryCardPresenter());
      /*  mAdapter = new ArrayObjectAdapter(new VideoCardViewPresenter(getActivity(), R.style.VideoGridCardTheme));
        setAdapter(mAdapter);*/
    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof SubCategoryBean) {
                SubCategoryBean movie = (SubCategoryBean) item;
                Intent intent = new Intent(getActivity(), ApplicationDetailsActivity.class);
                //   intent.putExtra(DetailsActivity.APPLICATIONS, movie);
                intent.putExtra("AppID",movie.getId());
                //    intent.putExtra("CatID",movie.getCid());
                getActivity().startActivity(intent);
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
           /* if( item != null ){
                try {
                    URI uri = new URI(CustomUtils.SERVER_IMAGE_PATH+((SubCategoryBean)item).getSub_tile_image());
            //        picassoBackgroundManager.updateBackgroundWithDelay(uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                Log.w(TAG, "item is null");
            }*/

        }
    }
}
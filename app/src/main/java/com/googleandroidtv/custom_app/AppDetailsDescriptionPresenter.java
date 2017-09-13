/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.googleandroidtv.custom_app;

import android.app.Activity;
import android.content.Context;
import android.support.v17.leanback.widget.Presenter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.googleandroidtv.R;
import com.googleandroidtv.bean.ApplicationDetailsBean;

public class AppDetailsDescriptionPresenter extends Presenter {
    private Context mContext;
    public AppDetailsDescriptionPresenter(Activity activity) {
        mContext = activity;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.detail_view_content, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ApplicationDetailsBean movie = (ApplicationDetailsBean) item;

        TextView primaryText = (TextView) viewHolder.view.findViewById(R.id.primary_text);
        ImageView imgGameControl = (ImageView) viewHolder.view.findViewById(R.id.imgGameControl);
        TextView gameControlType = (TextView) viewHolder.view.findViewById(R.id.gameControlType);
        TextView gameSize = (TextView) viewHolder.view.findViewById(R.id.gameSize);
        TextView gamePrice = (TextView) viewHolder.view.findViewById(R.id.gamePrice);
        ImageView imgViews = (ImageView) viewHolder.view.findViewById(R.id.imgViews);
        TextView gameViews = (TextView) viewHolder.view.findViewById(R.id.gameViews);
        TextView extraText = (TextView) viewHolder.view.findViewById(R.id.extra_text);

        primaryText.setText(movie.getApp_name());
        gameControlType.setText(movie.getGroup_name());
        gameSize.setText(movie.getApp_size());
        gamePrice.setText("FREE");
    //    gameViews.setText(movie.getMovie_view());
        extraText.setText(Html.fromHtml(movie.getApp_desc()));

     /*   TextView primaryText = mResourceCache.getViewById(viewHolder.view, R.id.primary_text);
        TextView sndText1 = mResourceCache.getViewById(viewHolder.view, R.id.secondary_text_first);
        TextView sndText2 = mResourceCache.getViewById(viewHolder.view, R.id.secondary_text_second);
        TextView extraText = mResourceCache.getViewById(viewHolder.view, R.id.extra_text);

        DetailedCard card = (DetailedCard) item;
        primaryText.setText(card.getTitle());
        sndText1.setText(card.getDescription());
        sndText2.setText(card.getYear() + "");
        extraText.setText(card.getText());
*/
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }

   /* @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        AppDetailsBean movie = (AppDetailsBean) item;

        if (movie != null) {
            viewHolder.getTitle().setText(movie.getMovie_title());
            viewHolder.getSubtitle().setText(movie.getCategory_name());
            viewHolder.getBody().setText(Html.fromHtml(movie.getMovie_desc()));
        }
    }*/
}

/*
 * Copyright (C) 2016 The Android Open Source Project
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

import android.content.Context;
import android.support.v17.leanback.widget.ImageCardView;

import com.googleandroidtv.bean.RelatedApplicationListBean;
import com.squareup.picasso.Picasso;

/**
 * Presenter for rendering video cards on the Vertical Grid fragment.
 */
public class RelatedCardViewPresenter extends RelImageCardViewPresenter {

    public RelatedCardViewPresenter(Context context, int cardThemeResId) {
        super(context, cardThemeResId);
    }

    public RelatedCardViewPresenter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(RelatedApplicationListBean card, final ImageCardView cardView) {
        super.onBindViewHolder(card, cardView);
        RelatedApplicationListBean videoCard = (RelatedApplicationListBean) card;
    /*    Glide.with(getContext())
                .asBitmap()
                .load(videoCard.getImageUrl())
                .into(cardView.getMainImageView());*/
        Picasso.with(getContext())
                .load(CustomUtils.SERVER_IMAGE_PATH+videoCard.getRel_icon_image())
                .into(cardView.getMainImageView());

    }
}

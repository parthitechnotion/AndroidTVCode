package com.googleandroidtv.animation;

import android.view.View;

public interface Joinable {
    void exclude(View view);

    void include(View view);
}

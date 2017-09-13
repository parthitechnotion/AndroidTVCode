package com.googleandroidtv;

import com.googleandroidtv.animation.ViewDimmer.DimState;

public interface DimmableItem {
    void setDimState(DimState dimState, boolean z);
}

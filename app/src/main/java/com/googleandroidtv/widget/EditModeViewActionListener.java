package com.googleandroidtv.widget;

public interface EditModeViewActionListener {
    void onEditModeExitTriggered();

    void onFocusLeavingEditModeLayer(int i);

    String onPrepForUninstall();

    void onUninstallComplete();

    void onUninstallFailure();
}

package com.veridiumid.sdk.fourfexport.defaultui.activity;

import android.graphics.RectF;

public interface RoisRenderer {
    void update(RectF[] newRoisArr, int newColor);

    void clear();
}

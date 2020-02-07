package com.vfs.fingerprint.anylinedocumentscanner;

import com.vfs.fingerprint.R;

/**
 * Created by andrea on 30/05/16.
 */
public enum FeedbackType {
    SHAKY(R.drawable.flash_icon, R.string.app_name),
    TOO_BRIGHT(R.drawable.flash_icon, R.string.app_name),
    TOO_DARK(R.drawable.flash_icon, R.string.app_name),
    PERFECT(R.drawable.flash_icon, R.string.app_name);

    private int iconId;
    private int stringId;

    FeedbackType(int iconId, int stringId) {
        this.iconId = iconId;
        this.stringId = stringId;
    }

    public int getIconId() {
        return iconId;
    }

    public int getStringId() {
        return stringId;
    }
}

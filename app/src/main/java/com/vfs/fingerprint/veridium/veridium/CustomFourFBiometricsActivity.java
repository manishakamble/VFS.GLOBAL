package com.vfs.fingerprint.veridium.veridium;

import android.os.Bundle;

import com.veridiumid.sdk.defaultdata.DataStorage;
import com.veridiumid.sdk.fourfexport.defaultui.activity.DefaultFourFExportBiometricsActivity;
import com.veridiumid.sdk.fourfexport.defaultui.activity.UICustomization;
import com.veridiumid.sdk.model.data.persistence.IKVStore;

;

public class CustomFourFBiometricsActivity extends DefaultFourFExportBiometricsActivity {

    private static final String LOG_TAG = CustomFourFBiometricsActivity.class.getName();

    private final int FOURF_TIMEOUT = 120000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTimeout(FOURF_TIMEOUT);
        UICustomization.setBackgroundColor(0xffE86020);
    }

    /*
    // Override action dialog behaviour.
    // Rather than show the dialog, just dismiss it.
    // (UICust)
    @Override
    protected void showCompleteDialog(Dialog mDialog, int actionId){
        TextView next = (TextView) mDialog.findViewById(com.veridiumid.sdk.fourf.defaultui.R.id.tv_next);
        next.performClick();
    }
    */

    /*
    // Override to show a different processing screen / dialog / fragment
    @Override
    public void onProcessingStart() {
        showFragment(new MyFourFWaitForProcessingFragment());
    }
    */

    /*
    // Override to show a custom 4F fragment
    @Override
    public void kickOffFourFFragment() {
        showFragment(new CustomFourFFragment());
    }
    */

    @Override
    protected IKVStore openStorage() {
        return DataStorage.getDefaultStorage();
    }


}

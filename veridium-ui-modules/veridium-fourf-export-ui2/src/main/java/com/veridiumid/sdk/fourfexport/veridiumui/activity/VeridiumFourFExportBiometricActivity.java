package com.veridiumid.sdk.fourfexport.veridiumui.activity;

import android.os.Bundle;
import android.view.View;

import com.veridiumid.sdk.fourfexport.FourFExportInterface;
import com.veridiumid.sdk.fourfexport.defaultui.activity.DefaultFourFExportBiometricsActivity;
import com.veridiumid.sdk.fourfexport.defaultui.activity.DefaultFourFFragment;
import com.veridiumid.sdk.fourfexport.defaultui.activity.UICustomization;
import com.veridiumid.sdk.fourfexport.veridiumui.R;
import com.veridiumid.sdk.fourfintegrationexport.HandGuideHelper;

public class VeridiumFourFExportBiometricActivity extends DefaultFourFExportBiometricsActivity {


    private final int FOURF_TIMEOUT = 120000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTimeout(FOURF_TIMEOUT);
    }

    @Override
    public void onFourFFragmentReady(final DefaultFourFFragment fourFFragment){
        super.onFourFFragmentReady(fourFFragment);
        if(UICustomization.getBackgroundImage() != null) {
            fourFFragment.rl_top_image.setBackground(getResources().getDrawable(R.drawable.hand_scan_ui_banner));
        }
    }

    @Override
    public void showInstructionalFragment()
    {
        VeridiumFourFExportInstructionalFragment fragment = new VeridiumFourFExportInstructionalFragment();
        Bundle fragmentBundle = new Bundle();
        if(isEnrollment() || isEnrollExport()){
            fragmentBundle.putString("Heading", String.valueOf(getText(R.string.enroll)));
        }else{
            fragmentBundle.putString("Heading", String.valueOf(getText(R.string.export)));
        }
        fragment.setArguments(fragmentBundle);
        showFragment(fragment);
    }

    public void onInstructionalFragmentReady(final VeridiumFourFExportInstructionalFragment instructionalFragment) {
        instructionalFragment.btn_getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kickOffBiometricsProcess();
            }
        });

        instructionalFragment.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    @Override
    public void setHandGuideDesign(){
        HandGuideHelper.setGuideDesign(FourFExportInterface.GuideDesign.MITTEN_DARK_LARGE);
    }

    @Override
    public boolean displayROIs() {
        return false;
    }


    @Override
    public boolean useHandMeter() { return true; }

    @Override
    public boolean useGuidanceArrows() { return false; }

}

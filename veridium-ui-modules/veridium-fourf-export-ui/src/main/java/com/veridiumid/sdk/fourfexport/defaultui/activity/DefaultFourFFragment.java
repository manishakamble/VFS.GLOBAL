package com.veridiumid.sdk.fourfexport.defaultui.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.veridiumid.sdk.fourfexport.FourFExportInterface;
import com.veridiumid.sdk.fourfexport.defaultui.R;
import com.veridiumid.sdk.support.base.VeridiumBaseFragment;
import com.veridiumid.sdk.support.ui.AspectRatioSafeFrameLayout;

public class DefaultFourFFragment extends VeridiumBaseFragment {

    private static final String LOG_TAG = DefaultFourFFragment.class.getName();

    private DefaultFourFExportBiometricsActivity mFourFActivity = null;

    protected RelativeLayout rl_top;
    public ImageView rl_top_image;
    protected Button btn_cancel;

    protected TextView tv_placeYourFingers;

    protected RealtimeRoisDecorator roiRenderer;
    protected AspectRatioSafeFrameLayout mCameraLayout;
    protected ImageView iv_imgFingerHint;

    protected ImageView iv_imgGuidanceNone; // An empty/invisible image for when nothing should be shown. This allows the rule that one is always shown.
    protected ImageView[] array_GuidanceImages = new ImageView[FourFExportInterface.TrackingState.nStates]; // Array allows indexing to the relevant
    protected int int_currentlyShownGuidanceImageIndex = 0;  // ImageViews for toggling which is shown.

    //public TextView tv_moveDots;

    protected TextView tv_tooClose;
    protected TextView tv_tooFar;
    protected TextView tv_tooCloseRight;
    protected TextView tv_tooFarRight;
    protected ImageView iv_arrow;
    protected ImageView iv_arrowRight;
    protected RelativeLayout rl_meter;
    protected RelativeLayout rl_meterRight;

    protected CheckBox left_right_switch;

    protected Button button_tips;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(com.veridiumid.sdk.fourfexport.defaultui.R.layout.export_layout_fourf_security, container, false);
    }

    @Override
    protected void initView(View view) {

        super.initView(view);
        setup4FFragment(view);
    }

    public void setup4FFragment(View view){

        mCameraLayout = view.findViewById(R.id.camera_preview);

        // remove onscreen soft buttons and set layout to ignore them popping up
        //  to prevent draw shifts
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        view.setSystemUiVisibility(uiOptions);

        mFourFActivity = (DefaultFourFExportBiometricsActivity) baseActivity;

        btn_cancel = view.findViewById((R.id.button_cancel));

        btn_cancel.setTextColor(UICustomization.getForegroundColor());

        left_right_switch = view.findViewById(R.id.left_right_switch);


        TextView topText = view.findViewById(R.id.topText);
        topText.setTextColor(UICustomization.getForegroundColor());

        tv_placeYourFingers = view.findViewById(R.id.tv_placeYourFingers);
        tv_placeYourFingers.setTextColor(UICustomization.getForegroundColor());

       // rl_centre_message = (RelativeLayout) view.findViewById(R.id.rl_centre_message);

//        tv_moveDots = (TextView) view.findViewById(R.id.tv_dots); // Fixme Lets get this back working
//        tv_moveDots.setTextColor(UICustomization.getForegroundColor());
//        tv_moveDots.setText("");

        iv_imgFingerHint = view.findViewById(R.id.img_finger_hint);

        tv_tooClose = view.findViewById(R.id.tv_tooCloseLeft);
        tv_tooFar = view.findViewById(R.id.tv_tooFarLeft);
        tv_tooCloseRight = view.findViewById(R.id.tv_tooCloseRight);
        tv_tooFarRight =  view.findViewById(R.id.tv_tooFarRight);

        iv_arrow = view.findViewById(R.id.iv_arrowLeft);
        iv_arrowRight = view.findViewById(R.id.iv_arrowRight);
        rl_meter = view.findViewById(R.id.rl_meterLeft);
        rl_meterRight = view.findViewById(R.id.rl_meterRight);

        rl_meter.setVisibility(View.GONE);
        rl_meterRight.setVisibility(View.GONE);

        button_tips = view.findViewById(R.id.button_tips);

        //UICustomization.applyFingerColorMask(iv_imgFingerHint); // NO, don't colour the guide.

        ImageView iv_leftArrow = view.findViewById(R.id.iv_guidance_leftArrow);
        UICustomization.applyForegroundColorMask(iv_leftArrow);
        ImageView iv_rightArrow = view.findViewById(R.id.iv_guidance_rightArrow);
        UICustomization.applyForegroundColorMask(iv_rightArrow);
        ImageView iv_backwardArrow = view.findViewById(R.id.iv_guidance_backwardArrow);
        UICustomization.applyForegroundColorMask(iv_backwardArrow);
        ImageView iv_forwardArrow = view.findViewById(R.id.iv_guidance_forwardArrow);
        UICustomization.applyForegroundColorMask(iv_forwardArrow);
        ImageView iv_downArrow = view.findViewById(R.id.iv_guidance_downArrow);
        UICustomization.applyForegroundColorMask(iv_downArrow);
        ImageView iv_upArrow = view.findViewById(R.id.iv_guidance_upArrow);
        UICustomization.applyForegroundColorMask(iv_upArrow);

        //Define guidance images and put them in the array; indexing must match FourFImagingProcessor.
        iv_imgGuidanceNone = view.findViewById(R.id.iv_guidance_none);
        array_GuidanceImages[FourFExportInterface.TrackingState.PREVIEW_STAGE_NORMAL.getCode()] = iv_imgGuidanceNone;
        array_GuidanceImages[FourFExportInterface.TrackingState.PREVIEW_STAGE_NULL.getCode()] = iv_imgGuidanceNone;
        array_GuidanceImages[FourFExportInterface.TrackingState.PREVIEW_STAGE_STABILIZED.getCode()] = iv_imgGuidanceNone;
        array_GuidanceImages[FourFExportInterface.TrackingState.PREVIEW_STAGE_TOO_CLOSE.getCode()] = iv_backwardArrow;
        array_GuidanceImages[FourFExportInterface.TrackingState.PREVIEW_STAGE_TOO_FAR.getCode()] = iv_forwardArrow;
        array_GuidanceImages[FourFExportInterface.TrackingState.PREVIEW_STAGE_FINGERS_APART.getCode()] = iv_imgGuidanceNone;
        array_GuidanceImages[FourFExportInterface.TrackingState.PREVIEW_STAGE_TOO_HIGH.getCode()] = iv_downArrow;
        array_GuidanceImages[FourFExportInterface.TrackingState.PREVIEW_STAGE_TOO_LOW.getCode()] = iv_upArrow;
        array_GuidanceImages[FourFExportInterface.TrackingState.PREVIEW_STAGE_TOO_LEFT.getCode()] = iv_rightArrow;
        array_GuidanceImages[FourFExportInterface.TrackingState.PREVIEW_STAGE_TOO_RIGHT.getCode()] = iv_leftArrow;
        array_GuidanceImages[FourFExportInterface.TrackingState.PREVIEW_STAGE_FRAME_DIM.getCode()] = iv_imgGuidanceNone;
        array_GuidanceImages[FourFExportInterface.TrackingState.PREVIEW_STAGE_INVALID_ROIS.getCode()] = iv_imgGuidanceNone;
        array_GuidanceImages[FourFExportInterface.TrackingState.PREVIEW_STAGE_PICTURE_REQUESTED.getCode()] = iv_imgGuidanceNone;
        array_GuidanceImages[FourFExportInterface.TrackingState.PREVIEW_STAGE_TAKING_PICTURE.getCode()] = iv_imgGuidanceNone;
        array_GuidanceImages[FourFExportInterface.TrackingState.PREVIEW_STAGE_NOT_CENTERED.getCode()] = iv_imgGuidanceNone;
        for (int makeInvisibleIndex = 0; makeInvisibleIndex < FourFExportInterface.TrackingState.nStates; makeInvisibleIndex++){
            array_GuidanceImages[makeInvisibleIndex].setVisibility(View.INVISIBLE);
        }

        rl_top = view.findViewById(R.id.rl_top);
        rl_top_image = view.findViewById(R.id.rl_top_image);

        if(UICustomization.getBackgroundImage() ==  null){
            rl_top_image.setVisibility(View.INVISIBLE);
            rl_top.setBackgroundColor(UICustomization.getBackgroundColor());
        }else{
            rl_top_image.setVisibility(View.VISIBLE);
            rl_top_image.setBackground(UICustomization.getBackgroundImage());
        }

        mFourFActivity.onFourFFragmentReady(this);
    }


    public void setGuidanceSymbol(FourFExportInterface.TrackingState newGuidanceIndex) {
        if (int_currentlyShownGuidanceImageIndex != newGuidanceIndex.getCode()) {
            array_GuidanceImages[int_currentlyShownGuidanceImageIndex].setVisibility(View.INVISIBLE);
            array_GuidanceImages[newGuidanceIndex.getCode()].setVisibility(View.VISIBLE);
            int_currentlyShownGuidanceImageIndex = newGuidanceIndex.getCode();
        }
    }

    public void setMeterSize(double handFraction){
        final double scaleFactor = 0.95; // scale down a bit
        handFraction *= 0.95;

        final DisplayMetrics displayMetrics=getResources().getDisplayMetrics();
        //final float screenWidthInDp=displayMetrics.widthPixels/displayMetrics.density;
        final float screenHeightInDp=displayMetrics.heightPixels/displayMetrics.density; // just get the height in dp

        int height_dp = (int) Math.floor(screenHeightInDp * handFraction);
        int width_dp = Math.round(height_dp * (156.0f/942.0f)); // ratio of the graphic

        final int min_width_dp = 40;
        if(width_dp < min_width_dp) width_dp = min_width_dp; // force a minimum width to fit text

        // you need to convert to pixels to set the layout height
        int height_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height_dp, getResources().getDisplayMetrics());
        int width_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width_dp, getResources().getDisplayMetrics());

        // set the meter layout heights
        ViewGroup.LayoutParams params = rl_meterRight.getLayoutParams();
        params.height = height_px;
        params.width = width_px;
        rl_meterRight.setLayoutParams(params);

        params = rl_meter.getLayoutParams();
        params.height = height_px;
        params.width = width_px;
        rl_meter.setLayoutParams(params);
    }

}

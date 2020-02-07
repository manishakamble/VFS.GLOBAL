package com.veridiumid.sdk.fourfexport.defaultui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.veridiumid.sdk.fourfexport.FourFExportInterface;
import com.veridiumid.sdk.fourfexport.defaultui.R;
import com.veridiumid.sdk.support.base.VeridiumBaseFragment;
import com.veridiumid.sdk.support.ui.AspectRatioSafeFrameLayout;

public class DefaultThumbFragment extends VeridiumBaseFragment {

    private static final String LOG_TAG = DefaultThumbFragment.class.getName();

    private DefaultFourFExportBiometricsActivity mThumbActivity = null;

    protected ImageView iv_cancelCircle;
    protected ImageView iv_cancelIcon;
    protected RelativeLayout rl_cancel;

    protected TextView tv_info;
    protected TextView tv_countDown;
    protected TextView tv_countDownLeft;
    protected TextView tv_placeYourFingers;

    protected ImageView iv_handSide;
    protected TextView tv_handside;


    protected TextView tv_switchHands;

    protected RelativeLayout rl_top;
    //protected RelativeLayout rl_countDown;
    //protected RelativeLayout rl_countDownLeft;
    protected RelativeLayout rl_centre_message;

    //protected ImageView iv_countDown;
    //protected ImageView iv_countDownLeft;

    protected RealtimeRoisDecorator roiRenderer;
    protected AspectRatioSafeFrameLayout mCameraLayout;
    protected ImageView iv_imgFingerHint;

    ImageView iv_imgGuidanceNone; // An empty/invisible image for when nothing should be shown. This allows the rule that one is always shown.
    ImageView[] array_GuidanceImages = new ImageView[FourFExportInterface.TrackingState.nStates]; // Array allows indexing to the relevant
    int int_currentlyShownGuidanceImageIndex = 0;  // ImageViews for toggling which is shown.

    protected TextView tv_switchLeftHand;
    protected TextView tv_switchRightHand;

    protected ImageView iv_switchHandCircle;
    protected ImageView iv_switchHandIcon;
    protected RelativeLayout rl_switchHand;

    protected boolean handGuideWasSet = false;

    private int i = 1;
    public TextView tv_moveDots;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.export_layout_thumb_security, container, false);
    }

    @Override
    protected void initView(View view) {

        super.initView(view);

        Drawable logo = UICustomization.getLogo();
        if (logo != null) {
            ImageView logoBox = (ImageView) view.findViewById(R.id.iv_consumer_logo);
            logoBox.setImageDrawable(logo);
        }

        mCameraLayout = (AspectRatioSafeFrameLayout) view.findViewById(R.id.camera_preview);

        mThumbActivity = (DefaultFourFExportBiometricsActivity) baseActivity;

        rl_cancel = (RelativeLayout) view.findViewById((R.id.rl_cancel));
        iv_cancelCircle = (ImageView) view.findViewById(R.id.cancel_circle);
        UICustomization.applyBackgroundColorMask(iv_cancelCircle);
        iv_cancelIcon = (ImageView) view.findViewById(R.id.cancel_icon);
        UICustomization.applyForegroundColorMask(iv_cancelIcon);

        iv_handSide = (ImageView)view.findViewById(R.id.iv_handSide);
        tv_handside = (TextView)view.findViewById(R.id.tv_handside);
        tv_handside.setTextColor(UICustomization.getForegroundColor());

        iv_switchHandCircle = (ImageView) view.findViewById(R.id.switch_hand_circle);
        UICustomization.applyBackgroundColorMask(iv_switchHandCircle);
        iv_switchHandIcon = (ImageView) view.findViewById(R.id.switch_hand_icon);
        UICustomization.applyForegroundColorMask(iv_switchHandIcon);
        rl_switchHand = (RelativeLayout) view.findViewById(R.id.rl_switch_hand);

        //tv_info = (TextView) view.findViewById(com.veridiumid.sdk.fourf.defaultui.R.id.tv_info);
        //tv_countDown = (TextView) view.findViewById(com.veridiumid.sdk.fourf.defaultui.R.id.tv_countDown);
        //tv_countDownLeft = (TextView) view.findViewById(com.veridiumid.sdk.fourf.defaultui.R.id.tv_countDownLeft);
        //tv_countDown.setTextColor(UICustomization.getForegroundColor());
        //tv_countDownLeft.setTextColor(UICustomization.getForegroundColor());

        tv_moveDots = (TextView) view.findViewById(R.id.tv_dots);
        tv_moveDots.setTextColor(UICustomization.getForegroundColor());
        tv_moveDots.setText("");



        tv_placeYourFingers = (TextView) view.findViewById(R.id.tv_placeYourFingers);
        tv_placeYourFingers.setTextColor(UICustomization.getForegroundColor());

         //rl_centre_message = (RelativeLayout) view.findViewById(R.id.rl_centre_message);


        iv_imgFingerHint = (ImageView) view.findViewById(R.id.img_finger_hint);


        UICustomization.applyFingerColorMask(iv_imgFingerHint);


        //Define guidance images and put them in the array; indexing must match FourFImagingProcessor.
        //iv_imgGuidanceNone = (ImageView) view.findViewById(R.id.iv_guidance_none);


        rl_top = (RelativeLayout) view.findViewById(R.id.rl_top);

        ImageView rl_top_image = (ImageView)view.findViewById(R.id.rl_top_image);

        if(UICustomization.getBackgroundImage() ==  null){
            rl_top_image.setVisibility(View.INVISIBLE);
            rl_top.setBackgroundColor(UICustomization.getBackgroundColor());

        }else{
            rl_top_image.setBackground(UICustomization.getBackgroundImage());
        }

        mThumbActivity.onThumbFragmentReady(this);
    }

}

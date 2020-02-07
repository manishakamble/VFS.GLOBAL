package com.veridiumid.sdk.fourfexport.defaultui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.veridiumid.sdk.fourfexport.defaultui.R;
import com.veridiumid.sdk.support.base.VeridiumBaseFragment;

public class DefaultFourFEnrollmentFailedFragment extends VeridiumBaseFragment {

    protected Button btn_getStarted;
    protected Button btn_cancel;
    protected TextView tv_animationInfo;
    protected TextView tv_header_text;
    protected ImageView iv_header_bg;
    protected TextView tv_subheading;
    protected ImageView iv_fail_image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.export_layout_default_4f_instructions, container, false);
    }

    @Override
    protected void initView(final View view)
    {
        super.initView(view);

        btn_getStarted = (Button) view.findViewById(R.id.btn_continue);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        tv_animationInfo = (TextView)view.findViewById(R.id.tv_animationInfo);
        tv_header_text = (TextView)view.findViewById(R.id.tv_header_text);
        iv_header_bg = (ImageView)view.findViewById(R.id.iv_header_bg);
        tv_subheading = (TextView)view.findViewById(R.id.tv_subheading);
        //final RelativeLayout rl_videoContainer = (RelativeLayout) view.findViewById(R.id.rl_animationContainer);
        // RelativeLayout rl_header = (RelativeLayout) view.findViewById(R.id.rl_header);
        iv_fail_image =  (ImageView)view.findViewById(R.id.iv_fail_image);

        // Use the cancel button?, no
        btn_cancel.setVisibility(View.GONE);

        // Set text
        tv_header_text.setText(getString(R.string.failed_heading));
        tv_subheading.setVisibility(View.VISIBLE);
        tv_subheading.setText(getString(R.string.failed_subheading));
        tv_animationInfo.setText(getString(R.string.fourf_failed_advice));

        // **Set UI
        if(UICustomization.getBackgroundColor() != UICustomization.defaultBackgroundColor)
        {
            btn_getStarted.setBackgroundColor(UICustomization.getBackgroundColor());
            iv_header_bg.setBackgroundColor(UICustomization.getBackgroundColor());
        }

        if(UICustomization.getForegroundColor() != UICustomization.defaultForegroundColor)
        {
            btn_getStarted.setTextColor(UICustomization.getForegroundColor());
            tv_header_text.setTextColor(UICustomization.getForegroundColor());
        }

        if(UICustomization.getBackgroundImage() != null)
        { // header background
            iv_header_bg.setBackground(UICustomization.getBackgroundImage());
        }
        // **done

        // no vid yet for this
        final TextureView videoview = view.findViewById(R.id.videoView);
        videoview.setVisibility(View.GONE);

        iv_fail_image.setVisibility(View.VISIBLE);
        iv_fail_image.setImageDrawable(getResources().getDrawable(R.drawable.error) );

        ((DefaultFourFExportBiometricsActivity) baseActivity).onFailedFragmentReady(this);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

}
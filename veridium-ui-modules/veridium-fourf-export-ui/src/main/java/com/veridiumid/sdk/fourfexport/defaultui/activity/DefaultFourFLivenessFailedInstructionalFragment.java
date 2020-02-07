package com.veridiumid.sdk.fourfexport.defaultui.activity;

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.veridiumid.sdk.fourfexport.FourFExportInterface;
import com.veridiumid.sdk.fourfexport.defaultui.R;
import com.veridiumid.sdk.support.base.VeridiumBaseFragment;

public class DefaultFourFLivenessFailedInstructionalFragment extends VeridiumBaseFragment {

    protected Button btn_getStarted;
    protected Button btn_cancel;
    protected TextView tv_animationInfo;
    protected TextView tv_header_text;
    protected ImageView iv_header_bg;
    protected TextView tv_subheading;

    private MediaPlayer introMediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.export_layout_default_4f_instructions, container, false);
    }

    @Override
    protected void initView(final View view)
    {
        super.initView(view);
        int value = getArguments().getInt("livenessType");

        btn_getStarted = (Button) view.findViewById(R.id.btn_continue);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        tv_animationInfo = (TextView)view.findViewById(R.id.tv_animationInfo);
        tv_header_text = (TextView)view.findViewById(R.id.tv_header_text);
        iv_header_bg = (ImageView)view.findViewById(R.id.iv_header_bg);
        tv_subheading = (TextView)view.findViewById(R.id.tv_subheading);
        //final RelativeLayout rl_videoContainer = (RelativeLayout) view.findViewById(R.id.rl_animationContainer);
        // RelativeLayout rl_header = (RelativeLayout) view.findViewById(R.id.rl_header);

        // Use the cancel button?, yes
        btn_cancel.setVisibility(View.VISIBLE);

        // Set text
        tv_header_text.setText(getString(R.string.failed_heading));
        tv_subheading.setVisibility(View.VISIBLE);
        tv_subheading.setText(getString(R.string.failed_liveness_subheading));
        tv_animationInfo.setText(getString(R.string.liveness_failed_advice));
        btn_getStarted.setText(getString(R.string.retry));

        // **Set UI
        if(UICustomization.getBackgroundColor() != UICustomization.defaultBackgroundColor)
        {
            btn_getStarted.setBackgroundColor(UICustomization.getBackgroundColor());
            btn_cancel.setBackgroundColor(UICustomization.getBackgroundColor());
            iv_header_bg.setBackgroundColor(UICustomization.getBackgroundColor());
        }

        if(UICustomization.getForegroundColor() != UICustomization.defaultForegroundColor)
        {
            btn_getStarted.setTextColor(UICustomization.getForegroundColor());
            btn_cancel.setTextColor(UICustomization.getForegroundColor());
            tv_header_text.setTextColor(UICustomization.getForegroundColor());
        }

        if(UICustomization.getBackgroundImage() != null)
        { // header background
            iv_header_bg.setBackground(UICustomization.getBackgroundImage());
        }
        // **done

        final TextureView videoview = (TextureView) view.findViewById(R.id.videoView);
        videoview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                try {
                    destoryIntroVideo();

                } catch (Exception e) {
                    System.err.println("Error playing intro video: " + e.getMessage());
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
        ((DefaultFourFExportBiometricsActivity) baseActivity).onLivenessInstructionalFragmentReady(this);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        destoryIntroVideo();
    }

    private void destoryIntroVideo() {
        if (introMediaPlayer != null) {
            introMediaPlayer.stop();
            introMediaPlayer.release();
            introMediaPlayer = null;
        }
    }

}
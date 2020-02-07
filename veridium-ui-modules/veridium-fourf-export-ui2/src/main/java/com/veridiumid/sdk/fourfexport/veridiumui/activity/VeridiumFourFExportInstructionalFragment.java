package com.veridiumid.sdk.fourfexport.veridiumui.activity;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.veridiumid.sdk.fourfexport.defaultui.activity.UICustomization;
import com.veridiumid.sdk.fourfexport.veridiumui.R;
import com.veridiumid.sdk.support.base.VeridiumBaseFragment;

public class VeridiumFourFExportInstructionalFragment extends VeridiumBaseFragment {

    protected Button btn_getStarted;

    protected Button btn_cancel;

    protected ImageView texture;

    protected TextView tv_animationInfo;

    private MediaPlayer introMediaPlayer;

    protected TextView tv_header_text;

    private String headingText;

    private ImageView cover_circle;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        headingText = bundle.getString("Heading");

        return inflater.inflate(R.layout.layout_veridium_export_4f_instructional, container, false);
    }

    @Override
    protected void initView(final View view) {
        super.initView(view);
        btn_getStarted = (Button) view.findViewById(R.id.btn_getStarted);
        cover_circle = (ImageView)view.findViewById(R.id.cover_circle);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        TextView info = (TextView)view.findViewById(R.id.tv_animationInfo);
        tv_header_text = (TextView)view.findViewById(R.id.tv_header_text);
        ImageView iv_header_bg = (ImageView)view.findViewById(R.id.iv_header_bg);

        final RelativeLayout rl_videoContainer = (RelativeLayout) view.findViewById(R.id.rl_animationContainer);
        // rl_videoContainer.setVisibility(View.INVISIBLE);

        iv_header_bg.setBackgroundColor(UICustomization.getBackgroundColor());
        tv_header_text.setTextColor(UICustomization.getForegroundColor());
        tv_header_text.setText(headingText);
        // **Set UI
        if(UICustomization.getBackgroundColor() != UICustomization.defaultBackgroundColor)
        {
            btn_getStarted.setBackgroundColor(UICustomization.getBackgroundColor());
        }

        if(UICustomization.getForegroundColor() != UICustomization.defaultForegroundColor)
        {
            btn_getStarted.setTextColor(UICustomization.getForegroundColor());
        }

        if(UICustomization.getBackgroundImage() == null)
        { // header background
            tv_header_text.setText(getString(R.string.instructions_header));
        }else{
            iv_header_bg.setBackground(getResources().getDrawable(R.drawable.blue_image_top));
        }


        final TextureView videoview = (TextureView) view.findViewById(R.id.videoView);
        videoview.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                try {
                    destoryIntroVideo();


                    introMediaPlayer = MediaPlayer.create(VeridiumFourFExportInstructionalFragment.this.getContext(), R.raw.veridium_instructional_fourf_left);
                    introMediaPlayer.setSurface(new Surface(surface));
                    introMediaPlayer.setLooping(true);
                    introMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    introMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            int videoWidth = mediaPlayer.getVideoWidth();
                            int videoHeight = mediaPlayer.getVideoHeight();

                            //Get the width of the screen
                            int screenHeight = cover_circle.getHeight();

                            //Get the SurfaceView layout parameters
                            android.view.ViewGroup.LayoutParams lp = videoview.getLayoutParams();

                            //Set the width of the SurfaceView to the width of the screen
                            lp.width = screenHeight;

                            //Set the height of the SurfaceView to match the aspect ratio of the video
                            //be sure to cast these as floats otherwise the calculation will likely be 0
                            lp.height = screenHeight;



                            //Commit the layout parameters
                            videoview.setLayoutParams(lp);
                            mediaPlayer.start();
                        }
                    });

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
        ((VeridiumFourFExportBiometricActivity) baseActivity).onInstructionalFragmentReady(this);
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

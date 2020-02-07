package com.veridiumid.sdk.fourfexport.defaultui.activity;

import android.hardware.Camera;
import com.veridiumid.sdk.imagingcore.CameraSamplingPolicy;

public class IndividualFourFCameraPolicy implements CameraSamplingPolicy {
    @Override
    public int selectCamera() {
        return Camera.CameraInfo.CAMERA_FACING_BACK;
    }

    @Override
    public boolean useTorch() {
        return true;
    }

    @Override
    public boolean torchModeBatterySaving() {
        return true;
    }

    @Override
    public boolean useFlash() {
        return true;
    }

    @Override
    public boolean useShutterSound() {
        return false;
    }

    @Override
    public boolean useVibration() {
        return false;
    }

    @Override
    public boolean useFlashAnim() {
        return false;
    }

    @Override
    public boolean useCamera2() {
        return false;
    }

    @Override
    public int autofocusMaxRetriesCount() {
        return 3;
    }

    @Override
    public long autofocusRetryDelay() {
        return 300;
    }

    @Override
    public float autofocusRetryDelayFactor() {
        return 1.25f;
    }
}

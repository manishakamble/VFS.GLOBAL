package com.vfs.fingerprint.veridium.veridium;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.veridiumid.sdk.support.base.VeridiumBaseFragment;
import com.vfs.fingerprint.R;

public class SplashFragment extends VeridiumBaseFragment implements Handler.Callback {

    private static final int MSG_GO_HOME = 0;

    private static final long START_APP_DELAY = 489;

    private Handler mHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mHandler = new Handler(this);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.obtainMessage(MSG_GO_HOME).sendToTarget();
            }
        }, START_APP_DELAY);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GO_HOME:
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(), null).commitAllowingStateLoss();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroy() {
        mHandler.removeMessages(MSG_GO_HOME);
        super.onDestroy();
    }
}

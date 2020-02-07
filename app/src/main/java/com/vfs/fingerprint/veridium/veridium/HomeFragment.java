package com.vfs.fingerprint.veridium.veridium;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.veridiumid.sdk.support.base.VeridiumBaseFragment;
import com.vfs.fingerprint.R;

public class HomeFragment extends VeridiumBaseFragment {

    private static final String LOG_TAG = HomeFragment.class.getSimpleName();

    public CardView button_2F;
    public CardView button_8F_capture_doubleOptimise;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void initView(View view) {
        button_2F = view.findViewById(R.id.button_2F);
        button_8F_capture_doubleOptimise = view.findViewById(R.id.button_capture_doubleOpt_8F);
        ((MainFragmentActivity) getActivity()).onHomeFragmentReady(this);
    }
}

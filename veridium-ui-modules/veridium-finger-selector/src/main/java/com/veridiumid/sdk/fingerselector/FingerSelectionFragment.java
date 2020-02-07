package com.veridiumid.sdk.fingerselector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.veridiumid.sdk.support.base.VeridiumBaseFragment;

/**
 * Created by lewiscarney on 04/10/2017.
 */

public class FingerSelectionFragment extends VeridiumBaseFragment {

    protected Button btn_getStarted;
    protected Button btn_cancel;


    protected ImageView missing_fingers_hand;
    protected ImageView missing_fingers_thumb;
    protected ImageView missing_fingers_index;
    protected ImageView missing_fingers_middle;
    protected ImageView missing_fingers_ring;
    protected ImageView missing_fingers_little;

    protected ImageView texture;

    protected TextView tv_animationInfo;

    protected ImageView switchHands;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_4f_individual_finger_selector, container, false);
    }

    @Override
    protected void initView(final View view) {
        super.initView(view);
        btn_getStarted = (Button) view.findViewById(R.id.activity_fourf_instructional_button_begin);
        btn_cancel = (Button) view.findViewById(R.id.activity_fourf_instructional_button_cancel);

        missing_fingers_hand = (ImageView)view.findViewById(R.id.missing_fingers_hand);

        missing_fingers_thumb = (ImageView)view.findViewById(R.id.missing_fingers_thumb);
        missing_fingers_index = (ImageView)view.findViewById(R.id.missing_fingers_index);
        missing_fingers_middle = (ImageView)view.findViewById(R.id.missing_fingers_middle);
        missing_fingers_ring = (ImageView)view.findViewById(R.id.missing_fingers_ring);
        missing_fingers_little = (ImageView)view.findViewById(R.id.missing_fingers_little);



        switchHands = (ImageView) view.findViewById(R.id.switch_hand_circle);

        ((FingerSelectionActivity) baseActivity).onMissingFingerFragmentReady(this);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

    }
}

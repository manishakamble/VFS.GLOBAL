package com.veridiumid.sdk.fingerselector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.veridiumid.sdk.fourfintegrationexport.ExportConfig;
import com.veridiumid.sdk.support.base.VeridiumBaseActivity;

/**
 * Created by lewiscarney on 08/06/2017.
 */


public class FingerSelectionActivity extends VeridiumBaseActivity {

    private boolean isThumbRemoved = false;
    private boolean isIndexRemoved = false;
    private boolean isMiddleRemoved = false;
    private boolean isRingRemoved = false;
    private boolean isLittleRemoved = false;

    private boolean isIndividualRightHand = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(com.veridiumid.sdk.fingerselector.R.layout.activity_fragment_main);
        showFragment(new FingerSelectionFragment());
    }

    public void onMissingFingerFragmentReady(final FingerSelectionFragment missingFingerFragment)
    {
        // Check current config to set a hand
        if(ExportConfig.getCaptureHandSide() == ExportConfig.CaptureHand.RIGHT)
        {
            boolean isRight = getIndividualRightHand();
            if(!isRight) {
                setIndividualRightHand(true);
                flipImages(missingFingerFragment);
            }
        }

        // If config is fixed, don't allow switching
        if(ExportConfig.getCaptureHandFixed())
        {
            missingFingerFragment.switchHands.setVisibility(View.INVISIBLE);
        }

        missingFingerFragment.switchHands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isRight = getIndividualRightHand();
                setIndividualRightHand(!isRight);

                flipImages(missingFingerFragment);
            }
        });

       /* missingFingerFragment.missing_fingers_thumb.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        missingFingerFragment.missing_fingers_index.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        missingFingerFragment.missing_fingers_middle.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        missingFingerFragment.missing_fingers_ring.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        missingFingerFragment.missing_fingers_little.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);*/


        RectF thumbRect = new RectF(0,0.407f,0.242f,0.680f);
        RectF indexRect = new RectF(0.242f,0.074f,0.433f,0.428f);
        RectF middleRect = new RectF(0.450f,0,0.577f,0.415f);
        RectF ringRect = new RectF(0.591f,0.065f,0.791f,0.421f);
        RectF littleRect1 = new RectF(0.798f,0.314f,0.989f,0.432f);
        RectF littleRect2 = new RectF(0.725f,0.432f,0.925f,0.543f);


        final RectF[] arrayOfFingerRectangles = new RectF[]{thumbRect,indexRect,middleRect,ringRect,littleRect1,littleRect2};
        final ImageView[] arrayOfFingerImages = new ImageView[]{missingFingerFragment.missing_fingers_thumb,
                missingFingerFragment.missing_fingers_index, missingFingerFragment.missing_fingers_middle,
                missingFingerFragment.missing_fingers_ring, missingFingerFragment.missing_fingers_little,
                missingFingerFragment.missing_fingers_little};


        missingFingerFragment.missing_fingers_hand.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float screenX = event.getX();
                    float screenY = event.getY();
                    float viewX = ((screenX ) / v.getWidth()) ;
                    float viewY = (((screenY ) / v.getHeight()));


                    for(int i=0; i<arrayOfFingerRectangles.length; i++){
                        if(pointIsWithinRect(viewX, viewY, arrayOfFingerRectangles[i])){
                            int iCopy = i;
                            if(i == 5){
                                iCopy = 4;
                            }
                            boolean isRemoved = getFingerRemoved(Fingers.resolve(iCopy));
                            setFingerRemoved(!isRemoved,  Fingers.resolve(iCopy));

                            if(isThumbRemoved && isIndexRemoved && isMiddleRemoved && isRingRemoved && isLittleRemoved){
                                setFingerRemoved(isRemoved,  Fingers.resolve(iCopy));
                                return true;

                            }

                            if(getFingerRemoved(Fingers.resolve(iCopy))){
                                arrayOfFingerImages[iCopy].setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                            }else{
                                arrayOfFingerImages[iCopy].setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                            }

                        }
                    }
                }
                return true;
            }
        });

        missingFingerFragment.btn_getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(getFingerRemoved(Fingers.INDEX) && getFingerRemoved(Fingers.MIDDLE) && getFingerRemoved(Fingers.RING) && getFingerRemoved(Fingers.LITTLE) && getFingerRemoved(Fingers.THUMB)) {

                    Toast t = Toast.makeText(FingerSelectionActivity.this, "Cannot continue because all fingers have been selected as missing.",
                            Toast.LENGTH_LONG);
                    TextView v = (TextView) t.getView().findViewById(android.R.id.message);
                    if (v != null) {
                        v.setGravity(Gravity.CENTER);
                    }
                    t.show();
                    return;
                }

                ExportConfig.setIndividualFingerCapture(true);
                ExportConfig.setIndividualindex(!getFingerRemoved(Fingers.INDEX));
                ExportConfig.setIndividualmiddle(!getFingerRemoved(Fingers.MIDDLE));
                ExportConfig.setIndividualring(!getFingerRemoved(Fingers.RING));
                ExportConfig.setIndividuallittle(!getFingerRemoved(Fingers.LITTLE));
                ExportConfig.setIndividualthumb(!getFingerRemoved(Fingers.THUMB));
                ExportConfig.CaptureHand captureHand = getIndividualRightHand() ? ExportConfig.CaptureHand.RIGHT : ExportConfig.CaptureHand.LEFT;

                ExportConfig.setCaptureHand(captureHand);


                Intent intent = new Intent();
                intent.putExtra("IndividualCapture", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        missingFingerFragment.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("IndividualCapture", false);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void flipImages(FingerSelectionFragment missingFingerFragment){
        flipImage(missingFingerFragment, missingFingerFragment.missing_fingers_hand);
        flipImage(missingFingerFragment, missingFingerFragment.missing_fingers_thumb);
        flipImage(missingFingerFragment, missingFingerFragment.missing_fingers_index);
        flipImage(missingFingerFragment, missingFingerFragment.missing_fingers_middle);
        flipImage(missingFingerFragment, missingFingerFragment.missing_fingers_ring);
        flipImage(missingFingerFragment, missingFingerFragment.missing_fingers_little);

    }

    private void flipImage(FingerSelectionFragment missingFingerFragment, ImageView hand) {
        Bitmap outputHand;
        Bitmap inputHand = ((BitmapDrawable)(hand.getDrawable())).getBitmap();
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        outputHand = Bitmap.createBitmap(inputHand, 0, 0, inputHand.getWidth(), inputHand.getHeight(), matrix, true);
        hand.setImageBitmap(outputHand);
    }

    private boolean pointIsWithinRect(float xValue, float yValue, RectF rect){
        if(getIndividualRightHand()){
            xValue = 1-xValue;
        }

        if(rect.left < xValue && rect.right > xValue && rect.top < yValue && rect.bottom > yValue){
            return true;
        }else{
            return false;
        }


    }

    private boolean getFingerRemoved(Fingers fingerTarget){
        switch (fingerTarget){
            case THUMB:
                return isThumbRemoved;
            case INDEX:
                return isIndexRemoved;
            case MIDDLE:
                return isMiddleRemoved;
            case RING:
                return isRingRemoved;
            case LITTLE:
                return isLittleRemoved;
            default:
                return false;
        }
    }

    private void setFingerRemoved(boolean removed, Fingers fingerTarget){
        switch (fingerTarget){
            case THUMB:
                isThumbRemoved = removed;
                break;
            case INDEX:
                isIndexRemoved = removed;
                break;
            case MIDDLE:
                isMiddleRemoved = removed;
                break;
            case RING:
                isRingRemoved = removed;
                break;
            case LITTLE:
                isLittleRemoved = removed;
                break;
        }
    }

    private boolean getIndividualRightHand(){
        return isIndividualRightHand;
    }
    private void setIndividualRightHand(boolean isRight){
        isIndividualRightHand = isRight;
    }

    private enum Fingers{
        THUMB(0),
        INDEX(1),
        MIDDLE(2),
        RING(3),
        LITTLE(4);
        private int code;

        public static final int nStates = 5;

        Fingers(int code) {
            this.code = code;
        }

        private int getCode() {
            return code;
        }

        private static Fingers resolve(int what) {
            Fingers[] states = values();
            for (Fingers state : states) {
                if (state.code == what) {
                    return state;
                }
            }
            return null;
        }
    }

}
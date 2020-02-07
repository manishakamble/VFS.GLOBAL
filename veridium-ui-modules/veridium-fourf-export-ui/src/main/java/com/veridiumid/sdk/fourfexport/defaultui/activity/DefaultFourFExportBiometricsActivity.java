package com.veridiumid.sdk.fourfexport.defaultui.activity;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.UiThread;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.veridiumid.sdk.model.biometrics.engine.BiometricsEngineState;
import com.veridiumid.sdk.model.biometrics.engine.processing.handling.BiometricOutcome;
import com.veridiumid.sdk.model.biometrics.packaging.IBiometricFormats.TemplateFormat;
import com.veridiumid.sdk.analytics.Analytics;
import com.veridiumid.sdk.model.biometrics.engine.IBiometricsEngine;
import com.veridiumid.sdk.model.domain.BiometricsResult;
import com.veridiumid.sdk.model.biometrics.engine.processing.handling.impl.AdaptiveEnrollmentHandler;
import com.veridiumid.sdk.model.biometrics.engine.processing.handling.impl.AuthenticationHandler;
import com.veridiumid.sdk.model.biometrics.engine.processing.handling.impl.ChainedHandler;
import com.veridiumid.sdk.model.biometrics.engine.processing.handling.impl.PersistEnrollmentHandler;
import com.veridiumid.sdk.model.biometrics.engine.processing.matching.LocalBiometricMatcher;
import com.veridiumid.sdk.model.biometrics.exception.BiometricsException;
import com.veridiumid.sdk.model.biometrics.persistence.impl.BytesTemplatesStorage;
import com.veridiumid.sdk.model.biometrics.persistence.impl.TemplateProviderCache;
import com.veridiumid.sdk.model.biometrics.persistence.impl.TemplateProviderFromTemplateStorage;
import com.veridiumid.sdk.model.biometrics.persistence.impl.TemplateStorageInMemory;
import com.veridiumid.sdk.model.data.persistence.IKVStore;
import com.veridiumid.sdk.model.data.persistence.impl.InMemoryKVStore;
import com.veridiumid.sdk.fourfexport.CaptureConfig;
import com.veridiumid.sdk.fourfexport.FourFExportInterface;
import com.veridiumid.sdk.fourfexport.FourFExportInterface.OptimiseMode;
import com.veridiumid.sdk.fourfexport.defaultui.R;
import com.veridiumid.sdk.fourfintegrationexport.AdaptiveEnrollmentHandlerLive;
import com.veridiumid.sdk.fourfintegrationexport.ExportConfig;
import com.veridiumid.sdk.fourfnative.FourFIntegrationWrapper;
import com.veridiumid.sdk.fourfintegrationexport.FourFProcessor;
import com.veridiumid.sdk.fourfintegrationexport.HandGuideHelper;
import com.veridiumid.sdk.fourfintegrationexport.IFourFProcessingListener;
import com.veridiumid.sdk.fourfintegrationexport.IFourFTrackingListener;
import com.veridiumid.sdk.fourfintegrationexport.LocalFourFBiometricMatcher;
import com.veridiumid.sdk.fourfintegrationexport.LocalFourFGetLivenessTemplateRH;
import com.veridiumid.sdk.fourfintegrationexport.LocalFourFGetSecondLivenessTemplateRH;
import com.veridiumid.sdk.fourfintegrationexport.LocalFourFGetTemplateInternalRH;
import com.veridiumid.sdk.fourfintegrationexport.ThumbProcessor;
import com.veridiumid.sdk.imagingcore.CameraLayoutDecorator;
import com.veridiumid.sdk.imagingcore.CameraSamplingPolicy;
import com.veridiumid.sdk.imagingcore.help.DisplayHelper;
import com.veridiumid.sdk.imagingcore.model.ImageHolder;
import com.veridiumid.sdk.support.BaseImagingBiometricsActivity;
import com.veridiumid.sdk.support.help.CustomCountDownTimer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.veridiumid.sdk.fourfexport.defaultui.R.string.hold_still;
import static com.veridiumid.sdk.fourfexport.defaultui.R.string.place_fingers_in_template;
import static com.veridiumid.sdk.fourfintegrationexport.FourFProcessor.getDesiredSubjectDistance;
import static com.veridiumid.sdk.fourfintegrationexport.FourFProcessor.getDesiredSubjectDistanceDigit;
import static java.lang.Math.abs;

public class DefaultFourFExportBiometricsActivity extends BaseImagingBiometricsActivity implements IFourFTrackingListener, IFourFProcessingListener {

    private final int ENROLLMENT_STEP1_COMPLETE = 1;
    private final int INDEX_COMPLETE = 2;
    private final int SWITCH_HANDS = 3;
    private final int ENROLLMENT_COMPLETE = 4;
    private final int AUTHENTICATION_COMPLETE = 5;
    private final int FAILED_SCAN = 6;
    private final int CAPTURE_COMPLETE = 7;
    private final int PASSIVE_LIVENESS_FAILED = 8;
    private final int RETRY_CAPTURE = 9;

    private static final String LOG_TAG = DefaultFourFExportBiometricsActivity.class.getName();

    private static final int ADAPTIVE_TEMPLATES_LIMIT = 3;

    private static boolean AUTO_START_DEFAULT = true;

    private static boolean async = true;

    protected FourFProcessor fourFProcessor;

    protected ThumbProcessor thumbProcessor;

    protected CustomCountDownTimer mCountDownTimer;

    protected CustomCountDownTimer mSwitchHandTimer; // time how long we show intermediate fragments

    private int SWITCH_HAND_TIMEOUT = 2000;

    private int FOURF_TIMEOUT = 120000;

    private int FOURF_TIMEOUT_WARN = 20000;

    private boolean timer_state = false;

    private final int SECOND = 1000;

    private int currentConfigIndex; // counter through the list

    private List<CaptureConfig> captureSequence = new ArrayList<>(); // list of configs to cycle through for capture

    private CaptureConfig currentConfig; // current configuration

    protected Map<String, BiometricsResult<ImageHolder>> finalResults; // sent off in super.onComplete()

    // Stores all results prior to sending off to super.onComplete()
    private BiometricsResult<ImageHolder> previousResults;
    private BiometricsResult<ImageHolder> eightF_fuse_queue; // when ==2, gets joined and added to previousResults
    private BiometricsResult<ImageHolder> multishot_fuse_queue; // when ==2, gets merged and added to previousResults
    private List<Integer> outputSlots;
    private BiometricsResult<ImageHolder> outputResults;

    private BytesTemplatesStorage templates_store_left; // enroll templates
    private BytesTemplatesStorage templates_store_right;

    private BytesTemplatesStorage templates_store_handle_left; // store to pass to enrol handler
    private BytesTemplatesStorage templates_store_handle_right;
    private BytesTemplatesStorage templates_store;

    private static boolean doNotShowAgainTickBoxChecked = false;

    protected void setTimeout(int timeout) {
        FOURF_TIMEOUT = timeout;
    }

    private int[] feedback = new int[1];


    private void setupActionbarAndStatusbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (getSupportActionBar() != null) {
                ActionBar actionBar;
                actionBar = getSupportActionBar();
                actionBar.setIcon(new ColorDrawable(this.getResources().getColor(android.R.color.transparent)));
                actionBar.setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.colorPrimaryDark)));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(this.getResources().getColor(R.color.colorAccent));
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        checkForLibraryDebugMode(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setupActionbarAndStatusbar();
        setContentView(com.veridiumid.sdk.fourfexport.defaultui.R.layout.activity_fragment_main);
        persistence = openStorage();
        openTemplateStorage();

        setUpInitialResults();

        if(!checkFormatSupported()){  // Check that the chosen formats are supported
            onError(BiometricsException.ERROR_UNSUPPORTED_CONGIF,  getString(R.string.unsupported_template_format)+":"+TemplateFormat.resolveFriendly(ExportConfig.getFormat()));
        }

        if (isEnrollment())
        {
            clearEnrollement();
            setup_enrollSequence();
        }
        else if(isEnrollExport())
        {
            clearEnrollement();
            setup_enrollExportSequence();
        }
        else if (isExport())
        {
            setup_exportSequence();
        }
        else if(isAuthentication())
        {
            if(!isEnrolled()){
                onError(BiometricsException.ERROR_TEMPLATE_HANDLING, "User not enrolled");
                return;
            }
            setup_authenticationSequence();
        }
        else if(isAuthenticationExport())
        {
            if(!isEnrolled()){
                onError(BiometricsException.ERROR_TEMPLATE_HANDLING, "User not enrolled");
                return;
            }
            setup_authExportSequence();
        }
        else
        {
            onError(BiometricsException.ERROR_TEMPLATE_HANDLING, "Unknown Mode");
        }

        if (tipsDisabled() || isAuthentication()) {
            kickOffBiometricsProcess();
        } else {
            showInstructions();
        }

    }

    // Show the correct instructions for the job
    private void showInstructions(){
        if(isEnrollment() || isEnrollExport() || isAuthentication()){
            showInstructionalFragment();
        }else{
            if(ExportConfig.isThumbExportMode()){
                showThumbInstructionalFragment();
            }else if(ExportConfig.mode == ExportConfig.ExportMode.INDIVIDUAL_FINGER){
                showIndividualInstructionalFragment();
            }else{
                showInstructionalFragment();
            }
        }
    }

    public void showInstructionalFragment(){
        showFragment(new DefaultFourFCaptureInstructionalFragment());
    }

    public void showThumbInstructionalFragment(){
        showFragment(new DefaultFourFCaptureInstructionalThumbFragment());
    }

    public void showIndividualInstructionalFragment(){
        showFragment(new DefaultFourFCaptureInstructionalIndividualFFragment());
    }

    private void checkForLibraryDebugMode(Context context) {
        if(FourFIntegrationWrapper.isDebugMode()) {
            if (!doNotShowAgainTickBoxChecked) {
                View checkBoxView = View.inflate(this, R.layout.export_checkbox, null);
                CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.alert_checkbox);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        doNotShowAgainTickBoxChecked = isChecked;
                    }
                });
                AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(context);
                builder.setTitle("DEBUG MODE")
                        .setView(checkBoxView)
                        .setMessage("DO NOT RELEASE IN DEBUG MODE.\n\nThe library is in debug mode, are you sure you want to continue?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which){
                                dialog.dismiss();
                                cancel();
                            }
                        })
                        .setIcon(R.drawable.warning)
                        .setCancelable(false)
                        .show();
            }
        }
    }

    private void setUpInitialResults() {
        currentConfigIndex = 0;
        previousResults = new BiometricsResult<ImageHolder>(FourFExportInterface.UID);
        outputResults = new BiometricsResult<ImageHolder>(FourFExportInterface.UID);
        eightF_fuse_queue = new BiometricsResult<ImageHolder>(FourFExportInterface.UID);
        multishot_fuse_queue = new BiometricsResult<ImageHolder>(FourFExportInterface.UID);
    }

    /* Check if a user is enrolled with at least one hand
     * Templates should have been opened with openTemplateStorage();
     * Can be enrolled for left, right, or both.
     */
    private boolean isEnrolled(){
        boolean enrolled = false;
        if(templates_store_left!=null){
            enrolled = templates_store_left.isEnrolled();
        }
        if(templates_store_right!=null){
            enrolled = enrolled || templates_store_right.isEnrolled();
        }
        return enrolled;
    }

    /*
     Create a capture sequence for single hand enrolment
     */

    private void setup_enrollSequence(){
        boolean rightHand = ExportConfig.getCaptureHandSide()== ExportConfig.CaptureHand.RIGHT;

        CaptureConfig firstImage = new CaptureConfig(FourFExportInterface.CaptureType.ENROLMENT_ONE,
                TemplateFormat.FORMAT_VERIDFP, rightHand);
        firstImage.setStoreForMatch(true);
        firstImage.setAllowUserHandSwitch(!ExportConfig.getCaptureHandFixed());
        firstImage.setStoreAsEnrolTemplate(true);
        firstImage.setShowProcessingScreen(true);

        CaptureConfig secondImage = new CaptureConfig(FourFExportInterface.CaptureType.ENROLMENT_TWO,
                TemplateFormat.FORMAT_VERIDFP, rightHand);
        secondImage.setMatchWithStored(true);
        secondImage.setUserSelectedHand(!ExportConfig.getCaptureHandFixed());
        secondImage.setStoreAsEnrolTemplate(true);
        secondImage.setShowProcessingScreen(true);

        captureSequence.clear();
        captureSequence.add(firstImage);
        captureSequence.add(secondImage);

        outputSlots = Arrays.asList(0);

        Log.d(LOG_TAG, ExportConfig.getConfig());

        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.CONFIG, ExportConfig.getConfig());
        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.SEQUENCE, "Enroll Sequence");
    }

    // all the sequences for doing export. Reads the export config to determine exactly what to do.
    private void setup_exportSequence(){
        if(ExportConfig.mode == ExportConfig.ExportMode.FOUR_F ){
            setup_captureSequence();
        }else if(ExportConfig.mode == ExportConfig.ExportMode.EIGHT_F ){
            setup_8F_captureSequence();
        } else if(ExportConfig.mode == ExportConfig.ExportMode.TWO_THUMB) {
            setup_2F_capture_basic_Sequence();
        }else if(ExportConfig.mode == ExportConfig.ExportMode.THUMB) {
            setup_1F_capture_basic_Sequence();
        }else if(ExportConfig.mode == ExportConfig.ExportMode.INDIVIDUAL_FINGER) {
            setup_individual_finger_capture_sequence();
        }
    }

    private void setup_enrollExportSequence()
    {
        CaptureConfig.JoinMergeFormat = ExportConfig.getFormat();

        // hand selection
        boolean rightHand = false;
        boolean allowHandSwitch = false;
        if(ExportConfig.getCaptureHandFixed()){
            if(ExportConfig.getCaptureHandSide() == ExportConfig.CaptureHand.RIGHT){
                rightHand = true;
            }
        }else{
            allowHandSwitch = true;
        }

        //---------
        CaptureConfig firstImage = new CaptureConfig(FourFExportInterface.CaptureType.ENROLMENT_ONE,
                TemplateFormat.FORMAT_VERIDFP, rightHand);

        firstImage.setAllowUserHandSwitch(allowHandSwitch);
        firstImage.setStoreForMatch(true);        // store and save first template for match and enrol
        firstImage.setStoreAsEnrolTemplate(true);
        firstImage.setUseLiveness(ExportConfig.getUseLiveness());
        firstImage.setShowProcessingScreen(true);

        firstImage.setAdditionalTemplateformat(ExportConfig.getFormat()); // grab an export template from shot 1
        firstImage.setStore_image_interally(true);
        firstImage.setGetTemplateFromStoredImage(true);

        CaptureConfig secondImage = new CaptureConfig(FourFExportInterface.CaptureType.ENROLMENT_TWO,
                TemplateFormat.FORMAT_VERIDFP, rightHand);
        secondImage.setMatchWithStored(true);
        secondImage.setUserSelectedHand(!ExportConfig.getCaptureHandFixed());
        secondImage.setStoreAsEnrolTemplate(true);
        secondImage.setShowProcessingScreen(true);

        captureSequence.clear();
        captureSequence.add(firstImage);
        captureSequence.add(secondImage);

        outputSlots = Arrays.asList(1);

        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.CONFIG, ExportConfig.getConfig());
        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.SEQUENCE, "Enroll-Export Sequence");
    }

    private void setup_authExportSequence(){

        CaptureConfig.JoinMergeFormat = ExportConfig.getFormat();

        // hand selection
        boolean rightHand = false;
        boolean allowHandSwitch = false;

        // Determine which hand to auth
        if(ExportConfig.getCaptureHandFixed()){
            if(ExportConfig.getCaptureHandSide() == ExportConfig.CaptureHand.RIGHT && templates_store_right.isEnrolled()){
                rightHand = true;
            }else if(ExportConfig.getCaptureHandSide() == ExportConfig.CaptureHand.LEFT && templates_store_left.isEnrolled()) {
                rightHand = false;
            }else{
                onError(BiometricsException.ERROR_UNSUPPORTED_CONGIF, "Forced request hand is not enrolled");
            }
        }else {
            if ((ExportConfig.getCaptureHandSide() == ExportConfig.CaptureHand.RIGHT && templates_store_right.isEnrolled())
                    || !templates_store_left.isEnrolled()) {
                rightHand = true;
            } else {
                rightHand = false;
            }
            if(templates_store_right.isEnrolled() && templates_store_left.isEnrolled()){
                allowHandSwitch = true;
            }
        }


        //---------
        CaptureConfig firstImage = new CaptureConfig(FourFExportInterface.CaptureType.NONE,
                TemplateFormat.FORMAT_VERIDFP, rightHand);

        firstImage.setStoreForMatch(true);        // store and save first liveness image for match and enrol
        firstImage.setStoreAsEnrolTemplate(true); // for adaptive enrollment
        firstImage.setMatchAgainstEnrol(true);
        firstImage.setAllowUserHandSwitch(allowHandSwitch);
        firstImage.setUseLiveness(ExportConfig.getUseLiveness());

        firstImage.setStore_image_interally(true);
        firstImage.setGetTemplateFromStoredImage(true);
        firstImage.setAdditionalTemplateformat(ExportConfig.getFormat());

        firstImage.setShowProcessingScreen(true);

        captureSequence.clear();
        captureSequence.add(firstImage);

        outputSlots = Arrays.asList(1);
        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.CONFIG, ExportConfig.getConfig());
        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.SEQUENCE, "Authenticate-Export Sequence");
    }

    /*
     *   Create a capture sequence for single hand authentication
    */
    private void setup_authenticationSequence(){
        boolean rightHand = false;
        boolean allowHandSwitch = false;

        // Determine which hand to auth
        if(ExportConfig.getCaptureHandFixed()){
            if(ExportConfig.getCaptureHandSide() == ExportConfig.CaptureHand.RIGHT && templates_store_right.isEnrolled()){
                rightHand = true;
            }else if(ExportConfig.getCaptureHandSide() == ExportConfig.CaptureHand.LEFT && templates_store_left.isEnrolled()) {
                rightHand = false;
            }else{
                onError(BiometricsException.ERROR_TEMPLATE_HANDLING, "Forced requested hand is not enrolled");
            }
        }else {
            if ((ExportConfig.getCaptureHandSide() == ExportConfig.CaptureHand.RIGHT && templates_store_right.isEnrolled())
                    || !templates_store_left.isEnrolled()) {
                rightHand = true;
            } else {
                rightHand = false;
            }
            if(templates_store_right.isEnrolled() && templates_store_left.isEnrolled()){
                allowHandSwitch = true;
            }
        }

        FourFExportInterface.CaptureType type = FourFExportInterface.CaptureType.AUTH;

        CaptureConfig firstImage = new CaptureConfig(type, TemplateFormat.FORMAT_VERIDFP, rightHand);
        firstImage.setStoreAsEnrolTemplate(true); // for adaptive enrollment
        firstImage.setMatchAgainstEnrol(true);
        firstImage.setAllowUserHandSwitch(allowHandSwitch);
        firstImage.setShowProcessingScreen(true);
        firstImage.setUseLiveness(ExportConfig.getUseLiveness());

        captureSequence.clear();
        captureSequence.add(firstImage);

        outputSlots = Arrays.asList(0);

        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.CONFIG, ExportConfig.getConfig());
        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.SEQUENCE, "Authentification Sequence");
    }


    /*
     Create a capture sequence for extracting export templates from both hands
     Supports various combo's of liveness, and finger optimisations
     */
    private void setup_8F_captureSequence() {
        if(ExportConfig.getOptimiseForIndexLittle()){
            setup_8F_capture_DoubleOptimise_Sequence();
        }else{
            setup_8F_capture_basic_Sequence();
        }
        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.CONFIG, ExportConfig.getConfig());
        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.SEQUENCE, "8F Capture Sequence");
    }

    /*
        Create a capture sequence for extracting export templates from both hands
        One image for right and left. Liveness can be on / off
    */
    private void setup_8F_capture_basic_Sequence(){
        boolean rightHand = false;

        OptimiseMode optimiseMode = getOptimiseModeFromConfig();
        CaptureConfig.JoinMergeFormat = ExportConfig.getFormat();

        CaptureConfig firstImage = new CaptureConfig(FourFExportInterface.CaptureType.EIGHTF_LEFT,
                ExportConfig.getFormat(), rightHand);
        firstImage.setOptimiseMode(optimiseMode);
        firstImage.setShowProcessingScreen(true);
        firstImage.setShowSwitchHands(true);
        firstImage.setAdd_to_8F_fuse_queue(true);
        firstImage.setUseLiveness(ExportConfig.getUseLiveness());

        rightHand = true;
        CaptureConfig secondImage = new CaptureConfig(FourFExportInterface.CaptureType.EIGHTF_RIGHT,
                ExportConfig.getFormat(), rightHand);
        secondImage.setOptimiseMode(optimiseMode);
        secondImage.setAdd_to_8F_fuse_queue(true);
        secondImage.setShowProcessingScreen(true);
        secondImage.setUseLiveness(ExportConfig.getUseLiveness());

        captureSequence.clear();
        captureSequence.add(firstImage);
        captureSequence.add(secondImage);

        outputSlots = Arrays.asList(2);

        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.CONFIG, ExportConfig.getConfig());
        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.SEQUENCE, "8F Capture Basic Sequence");
    }

    private void setup_2F_capture_basic_Sequence(){
        boolean rightHand = false;

        CaptureConfig.JoinMergeFormat = ExportConfig.getFormat();

        ExportConfig.setReliabilityMask(ExportConfig.getReliabilityMask());
        ExportConfig.setPackExtraScale(ExportConfig.getPackExtraScale());

        CaptureConfig firstImage = new CaptureConfig(FourFExportInterface.CaptureType.NONE,
                                                     ExportConfig.getFormat(), rightHand);
        firstImage.setOptimiseMode(OptimiseMode.THUMB);
        firstImage.setShowProcessingScreen(true);
        firstImage.setShowSwitchHands(true);
        firstImage.setAdd_to_8F_fuse_queue(true);
        firstImage.setIndividualFingerCaptured(FourFExportInterface.IndividualFingerCaptured.THUMB_LEFT);

        rightHand = true;
        CaptureConfig secondImage = new CaptureConfig(FourFExportInterface.CaptureType.NONE,
                                                      ExportConfig.getFormat(), rightHand);
        secondImage.setOptimiseMode(OptimiseMode.THUMB);
        secondImage.setShowProcessingScreen(true);
        secondImage.setAdd_to_8F_fuse_queue(true);
        secondImage.setIndividualFingerCaptured(FourFExportInterface.IndividualFingerCaptured.THUMB_RIGHT);

        captureSequence.clear();
        captureSequence.add(firstImage);
        captureSequence.add(secondImage);

        outputSlots = Arrays.asList(2);

        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.CONFIG, ExportConfig.getConfig());
        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.SEQUENCE, "2F Capture Basic Sequence");
    }

    private void setup_1F_capture_basic_Sequence(){
        boolean rightHand = ExportConfig.getCaptureHandSide() == ExportConfig.CaptureHand.RIGHT;
        CaptureConfig firstImage = new CaptureConfig(FourFExportInterface.CaptureType.NONE,
                                                     ExportConfig.getFormat(), rightHand);
        firstImage.setOptimiseMode(OptimiseMode.THUMB);
        firstImage.setShowProcessingScreen(true);
        firstImage.setAllowUserHandSwitch(!ExportConfig.getCaptureHandFixed());
        firstImage.setIndividualFingerCaptured(rightHand ? FourFExportInterface.IndividualFingerCaptured.THUMB_RIGHT : FourFExportInterface.IndividualFingerCaptured.THUMB_LEFT);

        captureSequence.clear();
        captureSequence.add(firstImage);

        outputSlots = Arrays.asList(0);

        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.CONFIG, ExportConfig.getConfig());
        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.SEQUENCE, "1F Capture Basic Sequence");
    }


    private void setup_individual_finger_capture_sequence()
    {
        CaptureConfig.JoinMergeFormat = ExportConfig.getFormat();

        boolean rightHand = ExportConfig.getCaptureHandSide() == ExportConfig.CaptureHand.RIGHT;

        ExportConfig.setReliabilityMask(ExportConfig.getReliabilityMask());
        ExportConfig.setPackExtraScale(ExportConfig.getPackExtraScale());

        captureSequence.clear();


        int takingIndexPicture = ExportConfig.getIndividualindex() ? 1:0;
        int takingMiddlePicture = ExportConfig.getIndividualmiddle() ? 1:0;
        int takingRingPicture = ExportConfig.getIndividualring() ? 1:0;
        int takingLittlePicture = ExportConfig.getIndividuallittle() ? 1:0;
        int takingThumbPicture = ExportConfig.getIndividualthumb() ? 1:0;
        int indiviudalFingerArray[] = new int[]{takingIndexPicture, takingMiddlePicture, takingRingPicture, takingLittlePicture};

        int totalPics = takingIndexPicture + takingMiddlePicture + takingRingPicture + takingLittlePicture + takingThumbPicture;

        for(int i = 0; i< indiviudalFingerArray.length; i++){
            if(indiviudalFingerArray[i] == 1) {
                CaptureConfig firstImage = new CaptureConfig(FourFExportInterface.CaptureType.NONE, ExportConfig.getFormat(), rightHand);
                firstImage.setOptimiseMode(FourFExportInterface.OptimiseMode.INDIVIDUAL_FINGER);
                firstImage.setShowProcessingScreen(true);
                int fingerToUse = i + 2;
                if(rightHand == false){
                    fingerToUse = i + 7;
                }
                firstImage.setIndividualFingerCaptured(FourFExportInterface.IndividualFingerCaptured.resolve(fingerToUse));
                //firstImage.setShowSwitchHands(true);
                firstImage.setAdd_to_8F_fuse_queue(true);
                captureSequence.add(firstImage);
            }
        }

        if(takingThumbPicture == 1){
            CaptureConfig secondImage = new CaptureConfig(FourFExportInterface.CaptureType.NONE, ExportConfig.getFormat(), rightHand);
            secondImage.setOptimiseMode(OptimiseMode.THUMB);
            secondImage.setShowProcessingScreen(true);
            int fingerToUse = 1;
            if(rightHand == false){
                fingerToUse = 6;
            }
            secondImage.setIndividualFingerCaptured(FourFExportInterface.IndividualFingerCaptured.resolve(fingerToUse));
            //firstImage.setShowSwitchHands(true);
            secondImage.setAdd_to_8F_fuse_queue(true);
            captureSequence.add(secondImage);
        }

        outputSlots = Arrays.asList((totalPics - 1)*2);

        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.CONFIG, ExportConfig.getConfig());
        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.SEQUENCE, "missingf_capture_sequence");
    }


    /*
        8F capture, but with vertical liveness and finger fusing.
        No Liveness here. 4 separate shots.
     */
    private void setup_8F_capture_DoubleOptimise_Sequence(){
        Log.d(LOG_TAG, "Double optimise sequence");
        CaptureConfig.JoinMergeFormat = ExportConfig.getFormat();

        //-----------------------
        boolean rightHand = false;
        OptimiseMode optimiseMode = OptimiseMode.INDEX_MIDDLE;
        CaptureConfig firstImage = new CaptureConfig(FourFExportInterface.CaptureType.NONE,
                ExportConfig.getFormat(), rightHand);
        firstImage.setOptimiseMode(optimiseMode);
        firstImage.setAdd_to_multishot_fuse_queue(true);
        firstImage.setShowProcessingScreen(true);
        firstImage.setUseLiveness(ExportConfig.getUseLiveness());
        //----------------------

        rightHand = false;
        optimiseMode = OptimiseMode.RING_LITTLE;
        CaptureConfig secondImage = new CaptureConfig(FourFExportInterface.CaptureType.NONE,
                ExportConfig.getFormat(), rightHand);
        secondImage.setOptimiseMode(optimiseMode);
        secondImage.setShowSwitchHands(true);
        secondImage.setAdd_to_multishot_fuse_queue(true);
        secondImage.setMultishot_to_8F_queue(true);
        secondImage.setShowProcessingScreen(true);
        secondImage.setUseLiveness(ExportConfig.getUseLiveness());
        //---------------------

        rightHand = true;
        optimiseMode = OptimiseMode.INDEX_MIDDLE;
        CaptureConfig thirdImage = new CaptureConfig(FourFExportInterface.CaptureType.NONE,
                ExportConfig.getFormat(), rightHand);
        thirdImage.setOptimiseMode(optimiseMode);
        thirdImage.setAdd_to_multishot_fuse_queue(true);
        thirdImage.setShowProcessingScreen(true);
        thirdImage.setUseLiveness(ExportConfig.getUseLiveness());
        //---------------------

        rightHand = true;
        optimiseMode = OptimiseMode.RING_LITTLE;
        CaptureConfig forthImage = new CaptureConfig(FourFExportInterface.CaptureType.NONE,
                ExportConfig.getFormat(), rightHand);
        forthImage.setOptimiseMode(optimiseMode);
        forthImage.setAdd_to_multishot_fuse_queue(true);
        forthImage.setMultishot_to_8F_queue(true);
        forthImage.setShowProcessingScreen(true);
        forthImage.setUseLiveness(ExportConfig.getUseLiveness());
        //--------------------

        captureSequence.clear();
        captureSequence.add(firstImage);
        captureSequence.add(secondImage);
        captureSequence.add(thirdImage);
        captureSequence.add(forthImage);

        outputSlots = Arrays.asList(6);

        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.CONFIG, ExportConfig.getConfig());
        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.SEQUENCE, "8F Capture Double Optimise Sequence");
    }

    /*
    Create a capture sequence for extracting export templates from one hand
    */
    private void setup_captureSequence(){
        boolean rightHand = ExportConfig.getCaptureHandSide()== ExportConfig.CaptureHand.RIGHT;
        FourFExportInterface.CaptureType type = rightHand ? FourFExportInterface.CaptureType.EIGHTF_RIGHT : FourFExportInterface.CaptureType.EIGHTF_LEFT;

        OptimiseMode optimiseMode = getOptimiseModeFromConfig();

        CaptureConfig firstImage = new CaptureConfig(type, ExportConfig.getFormat(), rightHand);
        firstImage.setOptimiseMode(optimiseMode);
        firstImage.setAllowUserHandSwitch(!ExportConfig.getCaptureHandFixed());
        firstImage.setShowProcessingScreen(true);
        firstImage.setUseLiveness(ExportConfig.getUseLiveness());
        captureSequence.clear();

        outputSlots = Arrays.asList(0);
        captureSequence.add(firstImage);

        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.CONFIG, ExportConfig.getConfig());
        Analytics.d(Analytics.Comp.FOURF_EXP, Analytics.Cat.SEQUENCE, "Capture Sequence");
    }

    private OptimiseMode getOptimiseModeFromConfig(){
        if(ExportConfig.getOptimiseForIndex()){
            return OptimiseMode.INDEX_MIDDLE;
        }else{
            return OptimiseMode.NONE;
        }
    }

    private static InMemoryKVStore sMemoryKVStore = new InMemoryKVStore();

    protected IKVStore openStorage() {
        return sMemoryKVStore;
    }

    // Called at each kickoff event
    @Override
    protected void configureBiometricEngine(IBiometricsEngine<ImageHolder, RectF[]> engine)
    {
        if(currentConfig.getOptimiseMode() == OptimiseMode.THUMB || currentConfig.getOptimiseMode() == OptimiseMode.INDIVIDUAL_FINGER){
            thumbProcessor = new ThumbProcessor(this, FOURF_TIMEOUT, getTemplatesCount(), async, this, this);
            thumbProcessor.setConfig(currentConfig);

            ChainedHandler<ImageHolder, byte[]> handler = new ChainedHandler<>();
            configureFourFResultsHandler(handler);
            thumbProcessor.setResultHandler(handler);
            engine.addProcessors(thumbProcessor);
        }
        else {
            fourFProcessor = new FourFProcessor(this, FOURF_TIMEOUT, getTemplatesCount(), async, this, this);
            fourFProcessor.setConfig(currentConfig);

            ChainedHandler<ImageHolder, byte[]> handler = new ChainedHandler<>();
            configureFourFResultsHandler(handler);
            fourFProcessor.setResultHandler(handler);
            engine.addProcessors(fourFProcessor);
        }
    }

    protected TemplateStorageInMemory temporaryStorage = null; // temp slot for sending templates to handlers
    protected TemplateStorageInMemory additionalTemplateStorage = null; // temp slot for sending templates to handlers
    protected TemplateStorageInMemory temporaryStorage_liveness2 = null; // temp slot for sending templates to handlers, from the second liveness image
    protected TemplateStorageInMemory additionalTemplateStorage_liveness2 = null; // temp slot for sending templates to handlers, from second liveness image

    // Set up the chained handlers according to the currentConfig
    // If the chain breaks at any point the capture sequence will fail
    protected void configureFourFResultsHandler(ChainedHandler<ImageHolder, byte[]> chainedHandler) {

        Log.d(LOG_TAG, "Config results handler");

        if( currentConfig.isMatchWithStored()){
            LocalBiometricMatcher matcher = createMatcher();
            TemplateProviderFromTemplateStorage templateProvider = new TemplateProviderFromTemplateStorage(temporaryStorage);
            matcher.setTemplatedProvider(templateProvider);
            chainedHandler.addSuccessor(new AuthenticationHandler<ImageHolder>(matcher)); // match first acquired template against the one from the review step
        }

        if( currentConfig.isStoreForMatch()){
            temporaryStorage = new TemplateStorageInMemory();
            chainedHandler.addSuccessor(new PersistEnrollmentHandler<ImageHolder>(temporaryStorage));
        }

        if( currentConfig.isGetTemplateFromLivenessImage()){
            additionalTemplateStorage = new TemplateStorageInMemory();
            chainedHandler.addSuccessor(new LocalFourFGetLivenessTemplateRH<ImageHolder>(additionalTemplateStorage, currentConfig.getAdditionalTemplateformat().getCode(), ExportConfig.getConfig() ));
        }else if(currentConfig.isGetTemplateFromStoredImage()){
            additionalTemplateStorage = new TemplateStorageInMemory();
            chainedHandler.addSuccessor(new LocalFourFGetTemplateInternalRH<ImageHolder>(additionalTemplateStorage, currentConfig.getAdditionalTemplateformat().getCode(), ExportConfig.getConfig() ));
        }

        if( currentConfig.isGetTemplateFromSecondLivenessImage()){ // get an additional template from the second liveness image
            additionalTemplateStorage_liveness2 = new TemplateStorageInMemory();
            chainedHandler.addSuccessor(new LocalFourFGetSecondLivenessTemplateRH<ImageHolder>(additionalTemplateStorage_liveness2, currentConfig.getAdditionalTemplateformat().getCode(), ExportConfig.getConfig() ));
        }

        if(currentConfig.isMatchAgainstEnrol() || currentConfig.isStoreAsEnrolTemplate()){
            //ITemplatesStorage storage = templates_store;
            TemplateProviderCache cacheProvider = new TemplateProviderCache(new TemplateProviderFromTemplateStorage(templates_store));

            if(currentConfig.isMatchAgainstEnrol()){
                LocalBiometricMatcher matcher = createMatcher();
                matcher.setTemplatedProvider(cacheProvider);
                chainedHandler.addSuccessor(new AuthenticationHandler<ImageHolder>(matcher));
            }

            if(currentConfig.isStoreAsEnrolTemplate()){
                chainedHandler.addSuccessor(new AdaptiveEnrollmentHandler<ImageHolder>(cacheProvider, templates_store, ADAPTIVE_TEMPLATES_LIMIT));
            }
        }

        // use the second liveness image as an enrol verification, and store as an enrol template
        if( currentConfig.isSecondLivenessImageAsEnrol()){
            // save as an enrol template
            TemplateProviderCache cacheProvider = new TemplateProviderCache(new TemplateProviderFromTemplateStorage(templates_store));
            chainedHandler.addSuccessor(new AdaptiveEnrollmentHandlerLive<ImageHolder>(cacheProvider, templates_store, temporaryStorage_liveness2, ADAPTIVE_TEMPLATES_LIMIT));
        }

    }

    @Override
    protected void retry() {
        temporaryStorage = null;
        setUpInitialResults();
        super.retry();
    }

    private LocalBiometricMatcher createMatcher() {
        return new LocalFourFBiometricMatcher();
    }

    protected void openTemplateStorage() {
        templates_store_right = new BytesTemplatesStorage(FourFExportInterface.SUFFIX_KEY_ENROL_RIGHT, persistence);
        templates_store_left = new BytesTemplatesStorage(FourFExportInterface.SUFFIX_KEY_ENROL_LEFT, persistence);
        templates_store = new BytesTemplatesStorage("VOID", persistence); // past to handlers
    }

    @Override
    protected CameraSamplingPolicy getCameraPolicy() {
        if (ExportConfig.isSingleDigitExportMode()) {
            return new IndividualFourFCameraPolicy();
        } else {
            return new DefaultFourFCameraPolicy();
        }
    }

    @Override
    public void onComplete(Map<String, BiometricsResult<ImageHolder>> results) {
        dismissDialog();

        //DecentralizedBiometricsEngineImpl.startDetailedSampling = false;//TODO why is this needed?

        // stop the timers
        if (mCountDownTimer != null) {
            mCountDownTimer.stop();
        }
        if (mSwitchHandTimer != null) {
            mSwitchHandTimer.stop();
        }

        // add the results to previous results
        BiometricsResult new_result = results.get(FourFExportInterface.UID);
        previousResults.addResult(  (ImageHolder) new_result.getInputs()[0], new_result.getOutput(-1));

        // add second liveness image to multishot queue. Do first, as this is the index-middle we want
        if( currentConfig.isSecondLiveness_to_multishot_queue()){
            try {
                byte[] added_template = additionalTemplateStorage_liveness2.restore()[0];
                Log.d(LOG_TAG, "added second liveness template to multishot queue");
                multishot_fuse_queue.addResult( (ImageHolder) new_result.getInputs()[0], added_template);
            }catch(IOException e){
                e.printStackTrace();
                onError(BiometricsException.ERROR_TEMPLATE_HANDLING, "failed to retrieve additionalTemplateStorage");
            }
        }

        // there is another template to save in from the additionalTemplateStorage
        if( currentConfig.isGetTemplateFromStoredImage() || currentConfig.isGetTemplateFromLivenessImage()){
            Log.d(LOG_TAG, "Adding template generated from stored");
            try{
                byte[] added_template = additionalTemplateStorage.restore()[0];
                previousResults.addResult( (ImageHolder) new_result.getInputs()[0], added_template);
                if(currentConfig.isStoreTemplate_to_multishot_queue()){
                    Log.d(LOG_TAG, "added additional store template to multishot queue");
                    multishot_fuse_queue.addResult( (ImageHolder) new_result.getInputs()[0], added_template);
                }
            }catch(IOException e){
                e.printStackTrace();
                onError(BiometricsException.ERROR_TEMPLATE_HANDLING, "failed to retrieve additionalTemplateStorage");
            }
        }

        // merge last two templates if specified. Gets added to previous results
        if(currentConfig.isAdd_to_multishot_fuse_queue()){
            //Log.d(LOG_TAG, "Add to multi-shot queue");
            multishot_fuse_queue.addResult( (ImageHolder) new_result.getInputs()[0], new_result.getOutput(-1));
        }


        // merge last two templates if specified. Gets added to previous results
        if(currentConfig.isAdd_to_8F_fuse_queue()){
            //Log.d(LOG_TAG, "Add to 8F fuse queue");
            eightF_fuse_queue.addResult( (ImageHolder) new_result.getInputs()[0], new_result.getOutput(-1));
        }

        // run fusing if queued
        if(multishot_fuse_queue.size() >=2) mergeTemplates(multishot_fuse_queue);
        if(eightF_fuse_queue.size() >=2) joinTemplates(eightF_fuse_queue);

        // see if we have completed the capture list, put all previous results into finalResults
        // and clear out the 4F lib
        boolean complete = false;
        if(currentConfigIndex == captureSequence.size()-1){
            onCaptureSequenceComplete();
            complete = true;
        }

        // move to next capture config
        currentConfigIndex++;

        // Show an intermediate fragment. Need to move this into the capture config.
        if((currentConfig.getCaptureType() == FourFExportInterface.CaptureType.ENROLMENT_ONE)){
            onEnrollmentStep1Complete();
        }else if(currentConfig.getCaptureType() == FourFExportInterface.CaptureType.ENROLMENT_TWO){
            onEnrollmentStep2Complete();
        }else if(currentConfig.getCaptureType() == FourFExportInterface.CaptureType.AUTH) {
            showDialogWithMode(AUTHENTICATION_COMPLETE);
        }
        else if(complete){
            showDialogWithMode(CAPTURE_COMPLETE);
        }
        else if(currentConfig.isShowSwitchHands()){
            showDialogWithMode(SWITCH_HANDS);
        }
        else if(currentConfig.getOptimiseMode() == OptimiseMode.INDIVIDUAL_FINGER){
            CaptureConfig nextConfig = captureSequence.get(currentConfigIndex);
            int nextFinger = nextConfig.getIndividualFingerCaptured().getCode();

            showNextIndividualDialog(nextConfig, nextFinger);


        }
        else if(ExportConfig.getOptimiseForIndexLittle()) {
            if (!currentConfig.isRightHand()) {
                if (currentConfig.getOptimiseMode() == OptimiseMode.INDEX_MIDDLE) {
                    showDialogWithMode(INDEX_COMPLETE);
                } else{
                    showDialogWithMode(SWITCH_HANDS);
                }
            } else {
                if (currentConfig.getOptimiseMode() == OptimiseMode.INDEX_MIDDLE) {
                    showDialogWithMode(INDEX_COMPLETE);
                } else {
                    onEnrollmentStep2Complete();
                }
            }
        }
        else if(ExportConfig.mode == ExportConfig.ExportMode.EIGHT_F) {
            if(!currentConfig.isRightHand()) {
                showDialogWithMode(SWITCH_HANDS);
            }
            else{
                showDialogWithMode(ENROLLMENT_COMPLETE);
            }
        }
        else {
            if(!complete) {
                kickOffBiometricsProcess();
            }else{
                super.onComplete(finalResults);
                return;
            }
        }
    }

    private void printBiometricsResult(BiometricsResult<ImageHolder> results){
         int N = results.size();
         Log.d(LOG_TAG, "printBiometricsResult size:"+N);
        for(int i=0; i <N; i++){
            Log.d(LOG_TAG, "output " + i+", length: " + results.getOutput(i).length);
        }
    }

    private void onCaptureSequenceComplete(){
        // When the capture list has been completed fill finalResults
        Log.d(LOG_TAG, "onCaptureSequenceComplete");
        printBiometricsResult(previousResults);

        ImageHolder[] inputs = previousResults.getInputs();
        byte[][] outputs = previousResults.getOutputs();

        for( int i : outputSlots){
            if(i<0 || i>=previousResults.size()){
                onError(BiometricsException.ERROR_TEMPLATE_HANDLING, "outputSlots exceeds captured results");
            }
            outputResults.addResult((ImageHolder) inputs[0], outputs[i]);
        }

        finalResults = new HashMap<>();
        finalResults.put(FourFExportInterface.UID, outputResults );
        previousResults = null;
        FourFIntegrationWrapper.purge();// this could be a shutdown call
    }

    private int getTemplatesCount() {
        return 1; // we also ask for one template.
    }

    protected void onPause() {
        super.onPause();
        pauseFourFProcessor();
    }

    private void pauseFourFProcessor() {
        if (fourFProcessor != null) {
            fourFFragment.tv_placeYourFingers.setVisibility(View.GONE);
            fourFProcessor.onPause();
            if (mBiometricsEngine != null) {
                mBiometricsEngine.pause();
            }
        }
    }

    protected void onResume() {
        super.onResume();
        resumeFourFProcessor();
    }

    protected DefaultFourFFragment fourFFragment;

    protected DefaultThumbFragment thumbFragment;


    /**
     * Access protected member roiRenderer
     *
     * @return Roi rendering object
     */
    protected RealtimeRoisDecorator getRoiRenderer() {
        return fourFFragment.roiRenderer;
    }

    protected IKVStore persistence;

    private static final String SUFFIX_USE_LEFT_HAND = "_useLeftHand";
    private static final String SUFFIX_HAND_FORCED = "_handForced";   // the user can/cannot change hand themselves
    private static final String SUFFIX_AUTOSTART = "_autostart";
    private static final String SUFFIX_TIPS = "_tips";

    public boolean isUseRightHand() {
        return currentConfig.isRightHand();
    }

    public void setUseRightHand(boolean useRightHand) {
        CaptureConfig.setRightHandChoice(useRightHand); // static user choice. Used when allow user choice is true
        setHand(useRightHand);
        updateMeterUI(useRightHand);
        currentConfig.setUserSelectedHand(true);

        if (persistence != null) {
            persistence.update(meta.getUID() + SUFFIX_USE_LEFT_HAND, String.valueOf(useRightHand).getBytes());
            persistence.commit(false);
        }
    }

    public void setAutoStart(boolean enable) {
        if (persistence != null) {
            persistence.update(meta.getUID() + SUFFIX_AUTOSTART, String.valueOf(enable).getBytes());
            persistence.commit(false);
        }
    }

    public void disableTips(boolean enabled) {
        if (persistence != null) {
            persistence.update(meta.getUID() + SUFFIX_TIPS, String.valueOf(enabled).getBytes());
            persistence.commit(false);
        }
    }

    public boolean tipsDisabled() {
        if (persistence != null) {
            String key = meta.getUID() + SUFFIX_TIPS;
            if (persistence.contains(key)) {
                byte[] data = persistence.read(key);
                if (data != null && data.length > 0) {
                    return Boolean.parseBoolean(new String(data));
                }
            }
        }
        return false;
    }

    public boolean isAutoStartEnabled() {
        if (persistence != null) {
            String key = meta.getUID() + SUFFIX_AUTOSTART;
            if (persistence.contains(key)) {
                byte[] data = persistence.read(key);
                if (data != null && data.length > 0) {
                    return Boolean.parseBoolean(new String(data));
                }
            }
        }
        return AUTO_START_DEFAULT;
    }

    private void clearEnrollement() {
        HandGuideHelper.clearGuides(this);
        try {
           templates_store_left.clear();
           templates_store_right.clear();
        }catch(Exception ex){
            ex.printStackTrace();
            onError(BiometricsException.ERROR_TEMPLATE_HANDLING, "Error clearing template: " + ex.getMessage());
        }
    }

    // Do not override this method. Please use kickOffFourFFragment() to show a custom
    // fourf fragment with custom layout (extends DefaultFourFFragment)
    @Override
    public void kickOffBiometricsProcess() {
        if(captureSequence.size()<1){
            onError(BiometricsException.ERROR_UNSUPPORTED_CONGIF, "The capture sequence is empty");
            return;
        }

        currentConfig = captureSequence.get(currentConfigIndex); // set config for this capture

        //Below is horrible hack to force a delay in android 4 phones. This is because the decorators
        //are not shown if the delay is not added as the decorators are added in the wrong order.
        //This should be fixed with a more permanent solution.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            kickOffOptimisedFragment();
        }
        else {
            new CountDownTimer(400, 1) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    kickOffOptimisedFragment();
                }
            }.start();
        }

    }

    private void kickOffOptimisedFragment() {
        if(currentConfig.getOptimiseMode() == OptimiseMode.THUMB || currentConfig.getOptimiseMode() == OptimiseMode.INDIVIDUAL_FINGER){
            kickOffThumbFragment();
        }
        else {
            kickOffFourFFragment();
        }
    }

    /*
       Display the default 4F capture screen
       Override this method to show a custom 4F fragment
       (UICust)
     */
    public void kickOffFourFFragment() {
        showFragment(new DefaultFourFFragment());
    }

    /*
       Display the default Thumb capture screen
       Override this method to show a custom thumb fragment
       (UICust)
     */
    public void kickOffThumbFragment() {
        showFragment(new DefaultThumbFragment());
    }

    private void setupButtons() {

        fourFFragment.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Cancelling");
                cancel();
            }
        });


        if (!isAutoStartEnabled()) {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startPreviewProcessing();
                }
            };
        } else {
        }

        if (fourFFragment.left_right_switch != null)
        {
            fourFFragment.left_right_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) { // change to right
                        setUseRightHand(true);
                    } else if (isChecked) { // change to left hand
                        setUseRightHand(false);
                    }
                    currentConfig.setUserSelectedHand(true);
                }
            });
        }

        if(fourFFragment.button_tips != null){
            fourFFragment.button_tips.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTipsDialog();
                }
            });
        }

    }


    private void setHand(boolean useRightHand){
        if(useRightHand) templates_store.changeStoreKey(FourFExportInterface.SUFFIX_KEY_ENROL_RIGHT);
        else templates_store.changeStoreKey(FourFExportInterface.SUFFIX_KEY_ENROL_LEFT);

        if(fourFProcessor!=null) fourFProcessor.updateDefaultFocusRegion(useRightHand);

        if(currentConfig.getOptimiseMode() == OptimiseMode.THUMB) {
            updateSwitchThumbUI();
            if (useRightHand) {
                //thumbFragment.iv_imgFingerHint.setImageDrawable(getResources().getDrawable(R.drawable.thumb_guide_right));
                thumbFragment.iv_handSide.setImageDrawable(getResources().getDrawable(R.drawable.righthand_thumb));
                thumbFragment.tv_handside.setText(getString(R.string.right_thumb));
                currentConfig.setIndividualFingerCaptured(FourFExportInterface.IndividualFingerCaptured.THUMB_RIGHT);
            } else {
                //thumbFragment.iv_imgFingerHint.setImageDrawable(getResources().getDrawable(R.drawable.thumb_guide));
                thumbFragment.iv_handSide.setImageDrawable(getResources().getDrawable(R.drawable.lefthand_thumb_instructional));
                thumbFragment.tv_handside.setText(getString(R.string.left_thumb));
                currentConfig.setIndividualFingerCaptured(FourFExportInterface.IndividualFingerCaptured.THUMB_LEFT);
            }

        }else if(currentConfig.getOptimiseMode() == OptimiseMode.INDIVIDUAL_FINGER){
            updateSwitchThumbUI();
            thumbFragment.iv_handSide.setImageDrawable(null);
            int currentFinger = currentConfig.getIndividualFingerCaptured().getCode();
            Drawable[] drawableIndicators = new Drawable[]{getResources().getDrawable(R.drawable.indicator_index),
                                                            getResources().getDrawable(R.drawable.indicator_middle),
                                                            getResources().getDrawable(R.drawable.indicator_ring),
                                                            getResources().getDrawable(R.drawable.indicator_little) };
            String[] arrayOfFingerLabels = new String[]{getString(R.string.right_thumb),getString(R.string.right_index),
                    getString(R.string.right_middle), getString(R.string.right_ring),
                    getString(R.string.right_little),getString(R.string.left_thumb),
                    getString(R.string.left_index), getString(R.string.left_middle),
                    getString(R.string.left_ring), getString(R.string.left_little)};

            if(useRightHand) {
                int imageIndex = currentFinger - 2;
                Bitmap outputHand;
                Bitmap inputHand = ((BitmapDrawable)(drawableIndicators[imageIndex])).getBitmap();
                Matrix matrix = new Matrix();
                matrix.preScale(-1.0f, 1.0f);
                outputHand = Bitmap.createBitmap(inputHand, 0, 0, inputHand.getWidth(), inputHand.getHeight(), matrix, true);
                thumbFragment.iv_handSide.setImageBitmap(outputHand);
            }else{
                int imageIndex = currentFinger - 7;
                thumbFragment.iv_handSide.setImageDrawable(drawableIndicators[imageIndex]);

            }

            thumbFragment.tv_handside.setText(arrayOfFingerLabels[currentFinger - 1]);
            setIndividualImageHand(useRightHand);


        }else{
            updateSwitchHandUI();
            setGuideImageHand(useRightHand);
        }
    }

    private void updateSwitchHandUI() {
        Log.d(LOG_TAG, "updateSwitchHandUI");
        //updateCountDownUI();
        if(fourFFragment!=null && fourFFragment.left_right_switch != null) {
            if (currentConfig.isAllowUserHandSwitch()) {
                fourFFragment.left_right_switch.setVisibility(View.VISIBLE);
            } else {
                fourFFragment.left_right_switch.setVisibility(View.INVISIBLE);
                return;
            }
        }
    }

    private void updateSwitchThumbUI() {
        Log.d(LOG_TAG, "updateSwitchThumbUI");
        if(thumbFragment!=null) {
            if (currentConfig.isAllowUserHandSwitch()) {
                thumbFragment.rl_switchHand.setVisibility(View.VISIBLE);
            } else {
                thumbFragment.rl_switchHand.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setGuideImageHand(boolean isRightHand) {
        float currentScale = fourFFragment.iv_imgFingerHint.getScaleX();

        if (isRightHand) {
            fourFFragment.iv_imgFingerHint.setScaleX(abs(currentScale) * -1.0f);
        } else {
            fourFFragment.iv_imgFingerHint.setScaleX(abs(currentScale));
        }
    }

    private void setIndividualImageHand(boolean isRightHand) {
        float currentScale = thumbFragment.iv_imgFingerHint.getScaleX();

        if(isRightHand){
            thumbFragment.iv_imgFingerHint.setScaleX(abs(currentScale) * -1.0f);
        }else{
            thumbFragment.iv_imgFingerHint.setScaleX(abs(currentScale));
        }
    }

    protected FrameLayout getCameraLayout() {
        if (currentConfig.getOptimiseMode() == OptimiseMode.THUMB || currentConfig.getOptimiseMode() == OptimiseMode.INDIVIDUAL_FINGER) {
            return thumbFragment.mCameraLayout;
        } else {
            return fourFFragment.mCameraLayout;
        }
    }

    @Override
    protected List<CameraLayoutDecorator> createCameraLayoutDecorators() {
        if (currentConfig.getOptimiseMode() == OptimiseMode.THUMB || currentConfig.getOptimiseMode() == OptimiseMode.INDIVIDUAL_FINGER) {
            List<CameraLayoutDecorator> list = new ArrayList<>();
            if (thumbFragment.roiRenderer == null) {
                synchronized (DefaultFourFExportBiometricsActivity.class) {
                    if (thumbFragment.roiRenderer == null) {
                        thumbFragment.roiRenderer = new RealtimeRoisDecorator(DefaultFourFExportBiometricsActivity.this, thumbFragment.mCameraLayout);
                    }
                }
            }

            list.add(thumbFragment.roiRenderer);


            list.add(new CameraLayoutDecorator() {
                @Override
                public void preAttachView() {

                }

                @Override
                public void postAttachView() {

                }

                @Override
                public void preDetachView() {

                }

                @Override
                public void postDetachView() {

                }

                @Override
                public void onCameraReady(float fov) {
                    DisplayMetrics dm = DisplayHelper.getDisplayMetrics(DefaultFourFExportBiometricsActivity.this);

                    int deviceWidth = dm.widthPixels;
                    int deviceHeight = dm.heightPixels;

                    float ratio = (float) deviceHeight / (float) deviceWidth;

                    setupThumbGuide(fov, ratio);
                }

                @Override
                public boolean decorate(Canvas canvas, int w, int h) {
                    return false;
                }
            });
            return list;
        }else{
            List<CameraLayoutDecorator> list = new ArrayList<>();
            if (fourFFragment.roiRenderer == null) {
                synchronized (DefaultFourFExportBiometricsActivity.class) {
                    if (fourFFragment.roiRenderer == null) {
                        fourFFragment.roiRenderer = new RealtimeRoisDecorator(DefaultFourFExportBiometricsActivity.this, fourFFragment.mCameraLayout);
                    }
                }
            }

            if(displayROIs()) {
                list.add(fourFFragment.roiRenderer);
            }

            list.add(new CameraLayoutDecorator() {
                @Override
                public void preAttachView() {

                }

                @Override
                public void postAttachView() {

                }

                @Override
                public void preDetachView() {

                }

                @Override
                public void postDetachView() {

                }

                @Override
                public void onCameraReady(float fov) {
                    DisplayMetrics dm = DisplayHelper.getDisplayMetrics(DefaultFourFExportBiometricsActivity.this);

                    int deviceWidth = dm.widthPixels;
                    int deviceHeight = dm.heightPixels;

                    float ratio = (float) deviceHeight / (float) deviceWidth;

                    setupHandGuide(fov, ratio);
                    setupMeter(fov, ratio);
                }

                @Override
                public boolean decorate(Canvas canvas, int w, int h) {
                    return false;
                }
            });

            return list;
        }
    }

    public boolean displayROIs() {
        return true;
    }


    protected void updateBiometricsStageMessage(final String displayMessage) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(currentConfig.getOptimiseMode() == OptimiseMode.THUMB || currentConfig.getOptimiseMode() == OptimiseMode.INDIVIDUAL_FINGER) {
                    thumbFragment.tv_placeYourFingers.setText(displayMessage);
                    if (thumbFragment.tv_placeYourFingers.getVisibility() != View.VISIBLE) {
                        thumbFragment.tv_placeYourFingers.setVisibility(View.VISIBLE);
                    }
                }else {
                    fourFFragment.tv_placeYourFingers.setText(displayMessage);
                    if (fourFFragment.tv_placeYourFingers.getVisibility() != View.VISIBLE) {
                        fourFFragment.tv_placeYourFingers.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
    }

    protected void startPreviewProcessing() {
        mCountDownTimer = new CustomCountDownTimer(getTimeout(), SECOND);
        mCountDownTimer.start();
        refreshTimer();
        super.startPreviewProcessing();
    }

    public void refreshTimer() {

        final Handler handler = new Handler();
        final Runnable counter = new Runnable() {

            public void run() {
                if (mCountDownTimer != null && mCountDownTimer.isStarted()) {
                    // update the timer when past
                    if(!timer_state && mCountDownTimer.getCurrentTime() < FOURF_TIMEOUT_WARN){
                        timer_state = true;
                    }
                    String timeout = Long.toString(mCountDownTimer.getCurrentTime() / SECOND);
                    handler.postDelayed(this, SECOND);
                }
            }
        };

        handler.postDelayed(counter, SECOND);
    }

    protected void onEnrollmentStep1Complete() {
        showDialogWithMode(ENROLLMENT_STEP1_COMPLETE);
    }

    protected void onEnrollmentStep2Complete() {
        showDialogWithMode(ENROLLMENT_COMPLETE);
    }

    // Join the last two templates in the provided results (normally left and right hand. Works for any)
    private void joinTemplates(BiometricsResult<ImageHolder> results){
        Log.d(LOG_TAG, "Join templates");

        int N = results.size();
        if(N < 2){
            onError(BiometricsException.ERROR_TEMPLATE_HANDLING,"Not enough templates to join");
        }

        //Log.d(LOG_TAG, "template 1 length: " + results.getOutput(N-2).length);
        //Log.d(LOG_TAG, "template 2 length: " + results.getOutput(N-1).length);

        byte[] joinedTemplate = FourFIntegrationWrapper.JoinExportData(results.getOutput(N-2),
                results.getOutput(N-1), CaptureConfig.JoinMergeFormat.getCode());

        //Log.d(LOG_TAG, "Joined template length: " + joinedTemplate.length);
        if (joinedTemplate.length == 1) { // need to check for an error
            int returnCode = joinedTemplate[0];
            if (returnCode != FourFExportInterface.JNIresult.SUCCESS.getCode()) {
                Log.e(LOG_TAG, "FourF ERROR Code = " + returnCode);
                onError(BiometricsException.ERROR_TEMPLATE_HANDLING,  getString(R.string.eight_f_template_failed));
            }
        }

        previousResults.addResult((ImageHolder) results.getInputs()[0], joinedTemplate);
        if(currentConfig.getIndividualFingerCaptured() == FourFExportInterface.IndividualFingerCaptured.NONE) {
            results.clearResults();
        }else{
            eightF_fuse_queue.addResult((ImageHolder) results.getInputs()[0], joinedTemplate);
        }
    }

    // Merge the last two templates in the provided results
    private void mergeTemplates(BiometricsResult<ImageHolder> results){
        Log.d(LOG_TAG, "Merge templates");

        int N = results.size();
        if(N < 2){
            onError(BiometricsException.ERROR_TEMPLATE_HANDLING,"Not enough templates to merge");
        }

        Log.d(LOG_TAG, "template 1 length: " + results.getOutput(N-2).length);
        Log.d(LOG_TAG, "template 2 length: " + results.getOutput(N-1).length);

        byte[] mergedTemplate = FourFIntegrationWrapper.MergeExportData(results.getOutput(N-2),
                results.getOutput(N-1), CaptureConfig.JoinMergeFormat.getCode());

        Log.d(LOG_TAG, "Merged template length: " + mergedTemplate.length);
        if (mergedTemplate.length == 1) { // need to check for an error
            int returnCode = mergedTemplate[0];
            if (returnCode != FourFExportInterface.JNIresult.SUCCESS.getCode()) {
                Log.e(LOG_TAG, "FourF ERROR Code = " + returnCode);
                onError(BiometricsException.ERROR_TEMPLATE_HANDLING,  getString(R.string.merge_f_template_failed));
            }
        }

        previousResults.addResult((ImageHolder) results.getInputs()[0], mergedTemplate);
        if(currentConfig.isMultishot_to_8F_queue()){
            Log.d(LOG_TAG, "added multishot result to 8F queue");
            eightF_fuse_queue.addResult( (ImageHolder) results.getInputs()[0], mergedTemplate);
        }
        results.clearResults();
    }

    @Override
    @UiThread
    public void clearRois(){
        fourFFragment.roiRenderer.clear();
        thumbFragment.roiRenderer.clear();
    }
//(final FourFExportInterface.TrackingState roiStatusCode, RectF[] newRois)
    @Override
    @UiThread
    public void updateRois(final FourFExportInterface.TrackingState roiStatusCode, RectF[] newRois) {
        //boolean shouldClearROIS = false;
        String displayMessage;

        if (FourFIntegrationWrapper.getValuedFeedbackArray(feedback)) {
            if (roiStatusCode == FourFExportInterface.TrackingState.PREVIEW_STAGE_INVALID_ROIS) {
                resetMeterUI(currentConfig.isRightHand());
            } else {
                animateMeter(currentConfig.isRightHand());
            }
        }

        switch (roiStatusCode) {
            case PREVIEW_STAGE_NORMAL:
                displayMessage = getString(hold_still);
                break;
            case PREVIEW_STAGE_INVALID_ROIS:
                displayMessage = getString(place_fingers_in_template);
                break;
            case PREVIEW_STAGE_PICTURE_REQUESTED:
                displayMessage = getString(R.string.taking_picture);
                //shouldClearROIS = true;
                break;
            case PREVIEW_STAGE_TOO_CLOSE:
                displayMessage = getString(R.string.Move_hand_further_away);
                break;
            case PREVIEW_STAGE_TOO_FAR:
                displayMessage = getString(R.string.Move_hand_closer);
                break;
            case PREVIEW_STAGE_FINGERS_APART:
                displayMessage = getString(R.string.Hold_fingers_together);
                break;
            case PREVIEW_STAGE_TOO_HIGH:
                displayMessage = getString(R.string.Move_hand_down);
                break;
            case PREVIEW_STAGE_TOO_LOW:
                displayMessage = getString(R.string.Move_hand_up);
                break;
            case PREVIEW_STAGE_TOO_LEFT:
                displayMessage = getString(R.string.Move_hand_right);
                break;
            case PREVIEW_STAGE_TOO_RIGHT:
                displayMessage = getString(R.string.Move_hand_left);
                break;
            case PREVIEW_STAGE_FRAME_DIM:
                displayMessage = getString(place_fingers_in_template);
                break;
            case PREVIEW_STAGE_NOT_CENTERED:
                displayMessage = getString(R.string.Place_hand_inside_mask);
                break;
            default:
                displayMessage = "Case " + roiStatusCode;
                break;
        }

        if (fourFFragment != null && fourFFragment.roiRenderer != null) {
            reviewGuidanceImages(roiStatusCode);
            fourFFragment.roiRenderer.update(newRois, fourfCodeToRoiColor(roiStatusCode));
        } else if(thumbFragment != null && thumbFragment.roiRenderer != null){
            thumbFragment.roiRenderer.update(newRois, fourfCodeToRoiColor(roiStatusCode));
        }

        updateBiometricsStageMessage(displayMessage);

    }

    public boolean useHandMeter() {
        return false;
    }

    private void resetMeterUI(boolean isRightHand) {
        if (!useHandMeter()) return;

        if(fourFFragment !=null) {
            if (isRightHand) {
                fourFFragment.iv_arrowRight.setVisibility(View.INVISIBLE);
                fourFFragment.tv_tooCloseRight.setShadowLayer(0, 0, 0, Color.WHITE);
                fourFFragment.tv_tooFarRight.setShadowLayer(0, 0, 0, Color.WHITE);
            } else {
                fourFFragment.iv_arrow.setVisibility(View.INVISIBLE);
                fourFFragment.tv_tooClose.setShadowLayer(0, 0, 0, Color.WHITE);
                fourFFragment.tv_tooFar.setShadowLayer(0, 0, 0, Color.WHITE);
            }
        }
    }

    // show / hide the appropriate meters and reset them
    private void updateMeterUI(boolean isRightHand) {
        if(fourFFragment !=null) {
            if(!useHandMeter()){
                fourFFragment.rl_meter.setVisibility(View.GONE);
                fourFFragment.rl_meterRight.setVisibility(View.GONE);
                return;
            }

            if(isRightHand) {
                if (fourFFragment.rl_meterRight.getVisibility() != View.VISIBLE) {
                    fourFFragment.rl_meterRight.setVisibility(View.VISIBLE);
                }
                if (fourFFragment.rl_meter.getVisibility() != View.GONE) {
                    fourFFragment.rl_meter.setVisibility(View.GONE);
                }
                resetMeterUI(isRightHand);
            }else{
                if (fourFFragment.rl_meterRight.getVisibility() != View.GONE) {
                    fourFFragment.rl_meterRight.setVisibility(View.GONE);
                }
                if (fourFFragment.rl_meter.getVisibility() != View.VISIBLE) {
                    fourFFragment.rl_meter.setVisibility(View.VISIBLE);
                }
                resetMeterUI(isRightHand);
            }
        }
    }

    // Move the little arrow
    private void animateMeter(boolean isRightHand){
        if(!useHandMeter()) return;

        if(fourFFragment !=null) {
            if(isRightHand) {
                meterAnimation(fourFFragment.iv_arrowRight, fourFFragment.tv_tooCloseRight, fourFFragment.tv_tooFarRight, fourFFragment.rl_meterRight);
            }else{
                meterAnimation(fourFFragment.iv_arrow, fourFFragment.tv_tooClose, fourFFragment.tv_tooFar, fourFFragment.rl_meter);
            }
        }
    }

    private void meterAnimation(ImageView arrow, TextView tooClose, TextView tooFar, RelativeLayout container) {
        // android animation introduces more latency. Instead rely on 4Fs interpolation to smooth arrow updates
        if (arrow.getVisibility() != View.VISIBLE) {
            arrow.setVisibility(View.VISIBLE);
        }

        float convertedFeedback = ((((float)feedback[FourFExportInterface.Feedback.DISTANCE.ordinal()])/1000.0f) * container.getHeight());

        float new_arrow_position = convertedFeedback + ((container.getHeight() / 2) - (arrow.getHeight() / 2));

        arrow.setY(new_arrow_position);
        toggleArrowColor(arrow);
        toggleToCloseToFarGlow(tooClose, tooFar);
    }

    private void toggleArrowColor(ImageView arrow) {
        if (feedback[0] >= -100 && feedback[0] <= 100) {
            arrow.setImageResource(R.drawable.triangle_green);
        } else {
            arrow.setImageResource(R.drawable.triangle_white);
        }
    }

    private void toggleToCloseToFarGlow(TextView tv_tooClose, TextView tv_tooFar) {
        if (feedback[0] > 100) {
            tv_tooClose.setShadowLayer(20, 0, 0, Color.WHITE);
        } else {
            tv_tooClose.setShadowLayer(0, 0, 0, Color.WHITE);
        }

        if (feedback[0] < -100) {
            tv_tooFar.setShadowLayer(20, 0, 0, Color.WHITE);
        } else {
            tv_tooFar.setShadowLayer(0, 0, 0, Color.WHITE);
        }
    }

    private static final int normalRoiColor = Color.argb(75, 0, 255, 0);
    private static final int errorRoiColor = Color.argb(75, 255, 0, 0);
    private static final int lockedRoiColor = Color.argb(128, 255, 255, 0);
    private static final int finalRoiColor = Color.argb(75, 0, 255, 0);

    private int fourfCodeToRoiColor(FourFExportInterface.TrackingState roiStatusCode) {
        switch (roiStatusCode) {
            case PREVIEW_STAGE_NORMAL:
                return normalRoiColor;
                //return errorRoiColor;
            case PREVIEW_STAGE_STABILIZED:
                return finalRoiColor;
            case PREVIEW_STAGE_TAKING_PICTURE:
            case PREVIEW_STAGE_PICTURE_REQUESTED:
                return finalRoiColor;
        }
        return errorRoiColor;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onBackPressed() {
        cancel();
    }

    @Override
    public void onFailure(int code, String description) {
        if (isEnrollment()) {
            dismissDialog();
            clearEnrollement();
            showDialogWithMode(FAILED_SCAN, code);
        } else {
            super.onFailure(code, description);
        }
    }

    @Override
    public void onError(int code, String message) {
        Log.d(LOG_TAG, "Fourf on error override");
        if (isEnrollment() || isEnrollExport()) {
            clearEnrollement();
        }
        super.onError(code, message);
    }

    public void onFourFFragmentReady(final DefaultFourFFragment fourFFragment) {
        this.fourFFragment = fourFFragment;
        setupButtons();
        setHand(currentConfig.isRightHand());
        super.kickOffBiometricsProcess();
    }

    public void onThumbFragmentReady(final DefaultThumbFragment thumbFragment) {
        this.thumbFragment = thumbFragment;
        setupThumbCancel();
        setHand(currentConfig.isRightHand());
        if(currentConfig.isRightHand() && currentConfig.getOptimiseMode() == OptimiseMode.THUMB) {
            thumbFragment.iv_handSide.setImageDrawable(getResources().getDrawable(R.drawable.righthand_thumb));
            thumbFragment.tv_handside.setText(getString(R.string.right_thumb));
        }
        super.kickOffBiometricsProcess();
    }

    private void setupThumbCancel() {
        thumbFragment.rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Cancelling");
                cancel();
            }
        });

        thumbFragment.rl_switchHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentConfig.isRightHand()) {
                    setUseRightHand(true);
                }
                else if (currentConfig.isRightHand()) {
                    setUseRightHand(false);
                }
            }
        });
    }

    public void onFailedFragmentReady(DefaultFourFEnrollmentFailedFragment enrollmentFailedFragment) {
        enrollmentFailedFragment.btn_getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefaultFourFExportBiometricsActivity.super.onFailure(BiometricOutcome.FAIL_ENROL,"FourFEx Fail enrolment");
            }
        });
    }

    public void onSingleDigitFailedFragmentReady(DefaultFourFSingleDigitFailedFragment failedFragment) {
        failedFragment.btn_getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefaultFourFExportBiometricsActivity.super.onFailure(BiometricOutcome.FAIL_EXPORT, "FourFEx Single Digit Export");
            }
        });
    }

    public void onLivenessInstructionalFragmentReady(final DefaultFourFLivenessFailedInstructionalFragment instructionalFragment) {
        instructionalFragment.btn_getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retry();
            }
        });
        instructionalFragment.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //** Instruction fragment callbacks
    public void onInstructionalFragmentReady(final DefaultFourFCaptureInstructionalFragment instructionalFragment) {

        instructionalFragment.btn_getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kickOffBiometricsProcess();
            }
        });
    }

    public void onInstructionalThumbFragmentReady(final DefaultFourFCaptureInstructionalThumbFragment instructionalThumbFragment) {

        instructionalThumbFragment.btn_getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kickOffBiometricsProcess();
            }
        });
    }

    public void onInstructionalIndividualFFragmentReady(final DefaultFourFCaptureInstructionalIndividualFFragment individualFFragment){
        individualFFragment.btn_getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kickOffBiometricsProcess();
            }
        });
    }
    //** End - Instruction fragment callbacks

    public void setHandGuideDesign(){
        HandGuideHelper.setGuideDesign(FourFExportInterface.GuideDesign.FINGERS_DARK);
    }

    // Set the size of the meter according to the choosen hand guide. Ensure setupHandGuide has
    // already been called.
    private void setupMeter(float cameraFOV, float screenRatio)
    {
        double fraction = HandGuideHelper.queryGuideHeightAsFraction(cameraFOV, screenRatio, getDesiredSubjectDistance());

        if(fourFFragment != null) {
            fourFFragment.setMeterSize(fraction*0.9); // scale down a 10% from guide height
        }
        updateMeterUI(currentConfig.isRightHand());
    }

    public void setupHandGuide(float cameraFOV, float screenRatio) {
        if (fourFFragment != null) {
            setHandGuideDesign();
            boolean handGuideWasSet = HandGuideHelper.setHandGuide(this, cameraFOV, screenRatio, getDesiredSubjectDistance(), fourFFragment.iv_imgFingerHint, currentConfig.getOptimiseMode());
            if (!handGuideWasSet) {
                Log.e(LOG_TAG, "Hand guide generation failed");
            }
        }
    }

    public void setupThumbGuide(float cameraFOV, float screenRatio) {
        if (thumbFragment != null) {
            boolean handGuideWasSet = HandGuideHelper.setHandGuide(this, cameraFOV, screenRatio, getDesiredSubjectDistanceDigit(), thumbFragment.iv_imgFingerHint, currentConfig.getOptimiseMode());
            if (!handGuideWasSet) {
                Log.e(LOG_TAG, "Hand guide generation failed");
            }
        }
    }

    /*
        Override to turn UI guidance arrows on/off
     */
    public boolean useGuidanceArrows() { return true; }

    protected void reviewGuidanceImages(FourFExportInterface.TrackingState guidanceImageIndex) {
        if(useGuidanceArrows()) {
            fourFFragment.setGuidanceSymbol(guidanceImageIndex);
        }
    }

    /*
        Override to change the vibration behaviour. Eg. blank it to turn off.
        Or use some other photo success indication (user can remove their hand)
        (UICust)
     */
    public void doVibrate(){
        Vibrator v = (Vibrator) DefaultFourFExportBiometricsActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(300);
    }

    public void onProcessingStart() {
        if(fourFFragment!=null) {
            this.fourFFragment.tv_placeYourFingers.setVisibility(View.GONE);
        }
        Log.d(LOG_TAG, "onProcessingStart");
        showProcessingDialog();
    }

    /*
      Do not override. Calls onProcessingStart() when required, dismisses dialog boxes
     */
    long start_processing_time = 0;

    @Override
    public void onProcessingStartInternal() {
        Log.d(LOG_TAG, "onProcessingStartInternal");
        doVibrate();  // Vibrate to indicate capture success.
        dismissDialog();
        if( currentConfig.isShowProcessingScreen()){
            start_processing_time = System.currentTimeMillis();
            onProcessingStart(); // show processing fragment.
        }
    }

    @Override
    public void onProcessingStop() {
        Log.d(LOG_TAG, "onProcessingStop");
    }

    @Override
    public void onDigestionComplete(){
        // template extraction has finished
        Log.d(LOG_TAG, "onDigestionComplete");
    }

    @Override
    public void onRetryCapture(){
        showDialogWithMode(RETRY_CAPTURE);
    }

    @Override
    public void onStereo1Accepted(){
        Log.d(LOG_TAG, "onStereo1Accepted");
        // No function
    }

    @Override
    public void onLivenessFail(){
        Log.d(LOG_TAG, "Liveness failed");
        dismissDialog();
        pauseFourFProcessor();
        showDialogWithMode(FAILED_SCAN, BiometricOutcome.FAIL_LIVENESS);
    }

    @Override
    public void onPassiveLivenessFail(){
        Log.d(LOG_TAG, "Passive Liveness failed");
        dismissDialog();
        pauseFourFProcessor();
        showDialogWithMode(PASSIVE_LIVENESS_FAILED, BiometricOutcome.FAIL_LIVENESS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissDialog();
    }

    @Override
    public void onAttemptError(){
        mCountDownTimer.stop();
        if(currentConfig.getOptimiseMode() == OptimiseMode.THUMB ||
                currentConfig.getOptimiseMode() == OptimiseMode.INDIVIDUAL_FINGER)
        {
            showFragment(new DefaultFourFSingleDigitFailedFragment());
        }else {
            showFragment(new DefaultFourFEnrollmentFailedFragment());
        }
    }

    @Override
    protected void cancel(){
        if (isEnrollment() || isEnrollExport()) {
            // clear any enrolled templates
            clearEnrollement();
        }
        super.cancel();
    }

    /* Check the chosen format is supported
     * @return True if supported, else false
     */
    protected boolean checkFormatSupported(){
        Log.d(LOG_TAG, "checkFormatSupported : " + TemplateFormat.resolveFriendly(ExportConfig.getFormat()));
        if(ExportConfig.isFour_F_mode()){
            return checkFormatSupportedCapture4F(ExportConfig.getFormat());
        }else if(ExportConfig.isEight_F_mode()){
            if(ExportConfig.getOptimiseForIndexLittle()){
                return checkFormatSupportedAllFingerOptimise(ExportConfig.getFormat());
            }else{
                return checkFormatSupportedCapture8F(ExportConfig.getFormat());
            }

        }else if(ExportConfig.isThumbExportMode()){
            return checkFormatSupportedCaptureThumb(ExportConfig.getFormat());
        }else{
            return true;
        }
    }

    /* Check the chosen format is supported for 4F capture
     * @return True if supported, else false
     */
    public static boolean checkFormatSupportedCapture4F(TemplateFormat choosenFormat){
        // supports all listed formats
        return true;
    }

    /* Check the chosen format is supported for 8F capture
     * @return True if supported, else false
     */
    public static boolean checkFormatSupportedCapture8F(TemplateFormat choosenFormat){
        switch (choosenFormat){
            case FORMAT_VERIDFP:
                return false;
            case FORMAT_NIST:
                return false;
            case FORMAT_INTERPOL:
                return false;
            case FORMAT_ZIP:
                return true;
            case FORMAT_ISO_4_2005:
                return true;
            case FORMAT_ISO_2_2005:
                return false;
            case FORMAT_JSON:
                return true;
            default:
                return false;
        }
    }

    /* Check the chosen format is supported for thumb capture
 * @return True if supported, else false
 */
    public static boolean checkFormatSupportedCaptureThumb(TemplateFormat choosenFormat){
        switch (choosenFormat){
            case FORMAT_VERIDFP:
                return false;
            case FORMAT_NIST:
                return false;
            case FORMAT_INTERPOL:
                return false;
            case FORMAT_ZIP:
                return false;
            case FORMAT_ISO_4_2005:
                return false;
            case FORMAT_ISO_2_2005:
                return false;
            case FORMAT_JSON:
                return true;
            default:
                return false;
        }
    }

    /* Check the chosen format is supported for 8F capture
 * @return True if supported, else false
 */
    public static boolean checkFormatSupportedAllFingerOptimise(TemplateFormat choosenFormat){
        switch (choosenFormat){
            case FORMAT_VERIDFP:
                return false;
            case FORMAT_NIST:
                return false;
            case FORMAT_INTERPOL:
                return false;
            case FORMAT_ZIP:
                return true;
            case FORMAT_ISO_4_2005:
                return false;
            case FORMAT_ISO_2_2005:
                return false;
            case FORMAT_JSON:
                return true;
            default:
                return false;
        }
    }

    private void resumeFourFProcessor() {
        if (fourFProcessor != null) {
            fourFFragment.tv_placeYourFingers.setVisibility(View.VISIBLE);
            if (mBiometricsEngine != null) {
                try {
                    if (mBiometricsEngine.isInState(BiometricsEngineState.PAUSED)) {
                        mBiometricsEngine.resume();
                    } else if (mBiometricsEngine.isInState(BiometricsEngineState.INITIATED)) {
                        Log.d(LOG_TAG, "BiometricsEngine is initiated");
                    } else {
                        Log.d(LOG_TAG, "BiometricsEngine is already resumed");
                    }
                } catch (BiometricsException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private Dialog dialog;

    private void showProcessingDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.export_dialog_processing);

        final SlidingImageView slidingImageView = dialog.findViewById(R.id.processing_fingerprint);
        final ImageView line = dialog.findViewById(R.id.splash_line);


        final ValueAnimator va = ValueAnimator.ofFloat(0f, 1f);
        va.setRepeatMode(ValueAnimator.REVERSE);
        va.setRepeatCount(ValueAnimator.INFINITE);
        int mDuration = 800; //in millis
        va.setDuration(mDuration);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                slidingImageView.mPercent = ((float)animation.getAnimatedValue());
                line.setTranslationX((((float)animation.getAnimatedValue()) * slidingImageView.getWidth())  - (line.getWidth() *0.5f) );
            }

        });
        va.start();

        dialog.setCancelable(false);
        dialog.show();
    }

    // convenience method when you have no 'fail_code' to provide
    private void showDialogWithMode(int mode){
        showDialogWithMode(mode, 0);
    }

    private void showDialogWithMode(int mode, int fail_code) {
        if(fourFFragment!=null) {
            fourFFragment.tv_placeYourFingers.setVisibility(View.GONE);
        }
        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.export_dialog_custom_fourf);

        RelativeLayout background = dialog.getWindow().getDecorView().findViewById(R.id.mainDialog);
        Drawable draw = background.getBackground();
        draw = UICustomization.getImageWithDialogColor(draw);
        background.setBackground(draw);

        TextView mainText = dialog.findViewById(R.id.tv_mainText);
        mainText.setTextColor(UICustomization.getDialogTextColor());
        TextView errorText = dialog.findViewById(R.id.tv_error_info); // text always grey
        TextView smallText = dialog.findViewById(R.id.tv_smallMessage);
        smallText.setTextColor(UICustomization.getDialogTextColor());
        Button cancel = dialog.findViewById(R.id.button_cancel);
        cancel.setTextColor(UICustomization.getBackgroundColor());
        Button next = dialog.findViewById(R.id.button_next);
        next.setTextColor(UICustomization.getBackgroundColor());
        ImageView image = dialog.findViewById(R.id.imageView);
        ImageView image2 = dialog.findViewById(R.id.imageView2);
        View verticalLine = dialog.findViewById(R.id.lineAcross2);
        View horizontalLine = dialog.findViewById(R.id.lineAcross1);

        cancel.setText(getString(R.string.cancel));

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                cancel();
            }
        });

        next.setText(getString(R.string.next));
        View.OnClickListener nextClickListenerContinue = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(fourFFragment!=null) {
                    fourFFragment.tv_placeYourFingers.setVisibility(View.VISIBLE);
                }
                kickOffBiometricsProcess();
            }
        };

        View.OnClickListener nextClickListenerFinish = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(fourFFragment!=null) {
                    fourFFragment.tv_placeYourFingers.setVisibility(View.VISIBLE);
                }
                DefaultFourFExportBiometricsActivity.super.onComplete(finalResults);
            }
        };

        View.OnClickListener nextClickListenerRetry = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(fourFFragment!=null) {
                    fourFFragment.tv_placeYourFingers.setVisibility(View.VISIBLE);
                }
                retry();
            }
        };

        if(mode == ENROLLMENT_STEP1_COMPLETE)
        {
            mainText.setText(getString(R.string.success_nfirst_scan_complete));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.complete_background)));
            image2.setImageDrawable(UICustomization.getImageWithForegroundColor(getResources().getDrawable(R.drawable.complete_foreground)));
            next.setOnClickListener(nextClickListenerContinue);
            dialog.show();
        }
        else if(mode == INDEX_COMPLETE)
        {
            mainText.setText(getString(R.string.success_n_scan_complete));
            smallText.setText(getString(R.string.please_move_hand_upwards_for_next_picture));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.complete_background)));
            image2.setImageDrawable(UICustomization.getImageWithForegroundColor(getResources().getDrawable(R.drawable.complete_foreground)));
            next.setOnClickListener(nextClickListenerContinue);
            dialog.show();
        }

        else if(mode == SWITCH_HANDS)
        {
            mainText.setText(getString(R.string.success_nchange_hands));
            smallText.setText(getString(R.string.please_change_hands));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.switch_hand_background)));
            image2.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.switch_hand_foreground)));
            next.setOnClickListener(nextClickListenerContinue);
            dialog.show();
        }
        else if(mode == ENROLLMENT_COMPLETE)
        {
            mainText.setText(getString(R.string.success_nsecond_scan_complete));
            smallText.setText(getString(R.string.you_have_completed_enrollment));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.encrypting_background)));
            image2.setImageDrawable(UICustomization.getImageWithForegroundColor(getResources().getDrawable(R.drawable.encrypting_foreground)));
            next.setOnClickListener(nextClickListenerFinish);
            next.setText(getString(R.string.ok));
            cancel.setVisibility(View.GONE);
            verticalLine.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) next.getLayoutParams();
            params.weight = 2.0f;
            next.setLayoutParams(params);
            showCompleteDialog(dialog, mode);
        }
        else if(mode == AUTHENTICATION_COMPLETE)
        {
            mainText.setText(getString(R.string.success));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.complete_background)));
            image2.setImageDrawable(UICustomization.getImageWithForegroundColor(getResources().getDrawable(R.drawable.complete_foreground)));
            next.setOnClickListener(nextClickListenerFinish);
            cancel.setVisibility(View.GONE);
            verticalLine.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) next.getLayoutParams();
            params.weight = 2.0f;
            next.setLayoutParams(params);
            showCompleteDialog(dialog, mode);
        }
        else if(mode == FAILED_SCAN)
        {
            mainText.setText(getString(R.string.could_not_process));
            errorText.setText("(" + fail_code + ")");
            smallText.setText(getString(R.string.tips));
            smallText.setTextColor(UICustomization.getBackgroundColor());
            smallText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Show tips dialog
                    showTipsDialog();
                }
            });
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.error_background)));
            image2.setImageDrawable(UICustomization.getImageWithForegroundColor(getResources().getDrawable(R.drawable.error_foreground)));
            next.setText(getString(R.string.retry));
            next.setOnClickListener(nextClickListenerRetry);
            dialog.show();
        }
        else if(mode == CAPTURE_COMPLETE)
        {
            mainText.setText(getString(R.string.success_n_scan_complete));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.complete_background)));
            image2.setImageDrawable(UICustomization.getImageWithForegroundColor(getResources().getDrawable(R.drawable.complete_foreground)));
            cancel.setVisibility(View.GONE);
            verticalLine.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) next.getLayoutParams();
            params.weight = 2.0f;
            next.setLayoutParams(params);
            next.setOnClickListener(nextClickListenerFinish);
            showCompleteDialog(dialog, mode);
        }
        else if(mode == PASSIVE_LIVENESS_FAILED){
            mainText.setText(getString(R.string.sorry));
            smallText.setText(getString(R.string.passive_failed));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.error_background)));
            image2.setImageDrawable(UICustomization.getImageWithForegroundColor(getResources().getDrawable(R.drawable.error_foreground)));
            next.setText(getString(R.string.retry));
            next.setOnClickListener(nextClickListenerRetry);
            dialog.show();
        }
        else if(mode == RETRY_CAPTURE){
            mainText.setText(getString(R.string.failed_capture_retry_prompt));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            image.setImageDrawable(UICustomization.getImageWithBackgroundColor(getResources().getDrawable(R.drawable.error_background)));
            image2.setImageDrawable(UICustomization.getImageWithForegroundColor(getResources().getDrawable(R.drawable.error_foreground)));
            next.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            verticalLine.setVisibility(View.GONE);
            horizontalLine.setVisibility(View.GONE);
            dialog.show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            }, 1200);


        }
    }


    private void showNextIndividualDialog(CaptureConfig nextConfig, int nextFinger){
        Drawable[] drawableIndicators = new Drawable[]{getResources().getDrawable(R.drawable.lefthand_thumb_instructional),
                getResources().getDrawable(R.drawable.indicator_index),
                getResources().getDrawable(R.drawable.indicator_middle),
                getResources().getDrawable(R.drawable.indicator_ring),
                getResources().getDrawable(R.drawable.indicator_little) };


        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.export_dialog_custom_fourf);

        RelativeLayout background = (RelativeLayout) dialog.getWindow().getDecorView().findViewById(R.id.mainDialog);

        Drawable draw = background.getBackground();
        draw = UICustomization.getImageWithDialogColor(draw);
        background.setBackground(draw);

        TextView mainText = (TextView) dialog.findViewById(R.id.tv_mainText);
        mainText.setTextColor(UICustomization.getDialogTextColor());
        TextView smallText = (TextView) dialog.findViewById(R.id.tv_smallMessage);
        smallText.setTextColor(UICustomization.getDialogTextColor());

        Button cancel = (Button) dialog.findViewById(R.id.button_cancel);
        cancel.setTextColor(UICustomization.getBackgroundColor());
        Button next = (Button) dialog.findViewById(R.id.button_next);
        next.setTextColor(UICustomization.getBackgroundColor());
        ImageView image = (ImageView) dialog.findViewById(R.id.imageView);
        View verticalLine = (View) dialog.findViewById(R.id.lineAcross2);
        View horizontalLine = (View) dialog.findViewById(R.id.lineAcross1);

        next.setText(getString(R.string.next));


        cancel.setVisibility(View.GONE);
        verticalLine.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) next.getLayoutParams();
        params.weight = 2.0f;
        next.setLayoutParams(params);

        if(nextConfig.isRightHand()) {
            int imageIndex = nextFinger - 1;
            Bitmap outputHand;
            Bitmap inputHand = ((BitmapDrawable)(drawableIndicators[imageIndex])).getBitmap();
            Matrix matrix = new Matrix();
            matrix.preScale(-1.0f, 1.0f);
            outputHand = Bitmap.createBitmap(inputHand, 0, 0, inputHand.getWidth(), inputHand.getHeight(), matrix, true);

            image.setImageBitmap(outputHand);
        }else{
            int imageIndex = nextFinger - 6;
            image.setImageDrawable(drawableIndicators[imageIndex]);

        }

        mainText.setText(getString(R.string.fingerprint_captured));
        smallText.setText(getString(R.string.change_fingers));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                kickOffBiometricsProcess();
            }
        });
        dialog.show();

    }

    protected void showTipsDialog(){

        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.export_dialog_tips);



        TextView mainText = (TextView) dialog.findViewById(R.id.tv_mainText);
        mainText.setTextColor(UICustomization.getDialogTextColor());
        Button next = (Button) dialog.findViewById(R.id.button_next);
        next.setTextColor(UICustomization.getBackgroundColor());

        mainText.setText(getString(R.string.tips_message));
        next.setText(getString(R.string.got_it));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /*
        Override "complete dialog" behaviour (dialogs shown at the very end of capture sequences).
        Called for these actionId's:

        ENROLLMENT_COMPLETE
        CAPTURE_COMPLETE
        AUTHENTICATION_COMPLETE

        Display the dialog, or run auto key press on its buttons, eg. to skip showing the dialog.
        (next, cancel)
        See CustomFourFBiometricsActivity.java in demoappexport sample app
        (UICust)
     */
    protected void showCompleteDialog(Dialog mDialog, int actionId){
        dialog.show();
    }
}

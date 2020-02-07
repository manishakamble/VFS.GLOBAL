package com.vfs.fingerprint.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.vfs.fingerprint.AppClass;
import com.vfs.fingerprint.R;
import com.vfs.fingerprint.anylinedocumentscanner.DocScanUIMainActivity;
import com.vfs.fingerprint.anylinemrz.mrz.ScanMrzActivity;
import com.vfs.fingerprint.face.FaceActivity;
import com.vfs.fingerprint.signature.SignaturePadActivity;
import com.vfs.fingerprint.veridium.veridium.MainFragmentActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AdminActivity.class.getSimpleName();
    private TextView txtProceed;
    private CardView txtPreview;
    private LinearLayout lnrLytSignature, lnrLytDocumentUpload, lnrLytFingerPrint, lnrLytfacialRecognition, lnrLytPassportDetails;
    private View view1, view2, view3, view4, view5;
    private EditText ediTextApplicationNo;
    private ImageView imgViewMrz, imgFacialRecog, imgViewFingerPrint, imgViewDocScan, imgViewSignature;
    private boolean mrzEnabled;
    private boolean faceEnabled;
    private boolean fingerEnabled;
    private boolean signatureEnabled;
    Boolean switchPref_passport;
    Boolean switchPref_face;
    Boolean switchPref_sig;
    Boolean switchPref_doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        txtProceed = findViewById(R.id.txtProceed);
        txtPreview = findViewById(R.id.txtPreview);

        txtProceed.setOnClickListener(this);
        txtPreview.setOnClickListener(this);

        lnrLytSignature = findViewById(R.id.lnrLytSignature);
        lnrLytDocumentUpload = findViewById(R.id.lnrLytDocumentUpload);
        lnrLytFingerPrint = findViewById(R.id.lnrLytFingerPrint);
        lnrLytfacialRecognition = findViewById(R.id.lnrLytfacialRecognition);
        lnrLytPassportDetails = findViewById(R.id.lnrLytPassportDetails);

        lnrLytPassportDetails.setOnClickListener(this);
        lnrLytfacialRecognition.setOnClickListener(this);
        lnrLytFingerPrint.setOnClickListener(this);
        lnrLytDocumentUpload.setOnClickListener(this);
        lnrLytSignature.setOnClickListener(this);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        view5 = findViewById(R.id.view5);

        ediTextApplicationNo = findViewById(R.id.ediTextApplicationNo);

        imgViewMrz = findViewById(R.id.imgViewMrz);
        imgFacialRecog = findViewById(R.id.imgFacialRecog);
        imgViewFingerPrint = findViewById(R.id.imgViewFingerPrint);
        imgViewDocScan = findViewById(R.id.imgViewDocScan);
        imgViewSignature = findViewById(R.id.imgViewSignature);
        // get Preference status here
        SharedPreferences sharedPref = androidx.preference.PreferenceManager
                .getDefaultSharedPreferences(this);

        switchPref_passport = sharedPref.getBoolean(SettingsDynamic.KEY_PREF_PASSPORT_SWITCH, false);
        switchPref_face = sharedPref.getBoolean(SettingsDynamic.KEY_PREF_FACE_SWITCH, false);
        switchPref_sig = sharedPref.getBoolean(SettingsDynamic.KEY_PREF_SIGNATURE_SWITCH, false);
        switchPref_doc = sharedPref.getBoolean(SettingsDynamic.KEY_PREF_DOCUMENT_SWITCH, false);

        Log.d("SETTING", "Passport " + switchPref_passport.toString());
        Log.d("SETTING", "Face " + switchPref_face.toString());
        Log.d("SETTING", "Signature " + switchPref_sig.toString());
        Log.d("SETTING", "Document " + switchPref_doc.toString());
    }

    private void assignMrzBroadcast() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter("passport-successfull"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtProceed:
                onProceedClick();
                break;
            case R.id.lnrLytPassportDetails:
                startMrzActivity();
                break;
            case R.id.lnrLytfacialRecognition:
                openFaceCapture();
                break;
            case R.id.lnrLytFingerPrint:
                openFingerScan();
                break;
            case R.id.lnrLytDocumentUpload:
                openDocumentScanner();
                break;
            case R.id.lnrLytSignature:
                openSignatureScreen();
                break;
            case R.id.txtPreview:
                openPreviewScreen();
                break;
        }
    }

    private void openPreviewScreen() {
        Intent intent = new Intent(this, PreviewActivity.class);
        startActivity(intent);
        finish();
    }

    private void startMrzActivity() {
        assignMrzBroadcast();
        Intent intent = new Intent(this, ScanMrzActivity.class);
        startActivity(intent);
    }

    private void openFaceCapture() {
        assignFaceBroadcast();
        Intent intent = new Intent(AdminActivity.this, FaceActivity.class);
        startActivity(intent);
    }

    private void openFingerScan() {
        assignFingerBroadcast();
        Intent intent = new Intent(AdminActivity.this, MainFragmentActivity.class);
        startActivity(intent);
    }

    private void openDocumentScanner() {
        assignDocumentBroadcast();
        Intent intent = new Intent(AdminActivity.this, DocScanUIMainActivity.class);
        startActivity(intent);
    }

    private void assignDocumentBroadcast() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter("doc-scan"));
    }

    private void assignFaceBroadcast() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter("face-recognizition"));
    }

    private void assignFingerBroadcast() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter("finger-print"));
    }

    private void openSignatureScreen() {
        assignSignatureBroadcast();
        Intent intent = new Intent(this, SignaturePadActivity.class);
        startActivity(intent);
    }

    private void assignSignatureBroadcast() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter("signature"));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.getAction().equalsIgnoreCase("passport-successfull")) {
                mrzEnabled = true;
//                showToastMessage("success mrz");
                imgViewMrz.setImageDrawable(ContextCompat.getDrawable(AdminActivity.this, R.drawable.tick));
                LocalBroadcastManager.getInstance(AdminActivity.this).unregisterReceiver(receiver);
            } else if (intent.getAction().equalsIgnoreCase("face-recognizition")) {
                faceEnabled = true;
                imgFacialRecog.setImageDrawable(ContextCompat.getDrawable(AdminActivity.this, R.drawable.tick));
                LocalBroadcastManager.getInstance(AdminActivity.this).unregisterReceiver(receiver);
            } else if (intent.getAction().equalsIgnoreCase("finger-print")) {
                fingerEnabled = true;
                imgViewFingerPrint.setImageDrawable(ContextCompat.getDrawable(AdminActivity.this, R.drawable.tick));
                LocalBroadcastManager.getInstance(AdminActivity.this).unregisterReceiver(receiver);
            } else if (intent.getAction().equalsIgnoreCase("doc-scan")) {
                imgViewDocScan.setImageDrawable(ContextCompat.getDrawable(AdminActivity.this, R.drawable.tick));
                LocalBroadcastManager.getInstance(AdminActivity.this).unregisterReceiver(receiver);
            } else if (intent.getAction().equalsIgnoreCase("signature")) {
                signatureEnabled = true;
                imgViewSignature.setImageDrawable(ContextCompat.getDrawable(AdminActivity.this, R.drawable.tick));
                LocalBroadcastManager.getInstance(AdminActivity.this).unregisterReceiver(receiver);
            }
        }
    };

    private void showToastMessage(String message) {
        Toast.makeText(AdminActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void onProceedClick() {
        LocalDateTime ldt = LocalDateTime.now();
        String date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(ldt);
        AppClass.getInstance().setTxnId(ediTextApplicationNo.getText().toString() + "_" + date.replace("-", ""));

        if (switchPref_sig) {
            lnrLytSignature.setVisibility(View.VISIBLE);
            view5.setVisibility(View.VISIBLE);
        }
        if (switchPref_doc) {
            lnrLytDocumentUpload.setVisibility(View.VISIBLE);
            view4.setVisibility(View.VISIBLE);
        }
        if (switchPref_face) {
            lnrLytfacialRecognition.setVisibility(View.VISIBLE);
            view2.setVisibility(View.VISIBLE);
        }
        if(switchPref_passport){
            lnrLytPassportDetails.setVisibility(View.VISIBLE);
            view1.setVisibility(View.VISIBLE);
        }
        lnrLytFingerPrint.setVisibility(View.VISIBLE);

        view3.setVisibility(View.VISIBLE);

        ediTextApplicationNo.setEnabled(false);
        AppClass.getInstance().setApplicationNo(ediTextApplicationNo.getText().toString());


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mrzEnabled == true && faceEnabled == true && fingerEnabled == true && signatureEnabled == true) {
            txtPreview.setVisibility(View.VISIBLE);
        }
        txtPreview.setVisibility(View.VISIBLE);

    }
}

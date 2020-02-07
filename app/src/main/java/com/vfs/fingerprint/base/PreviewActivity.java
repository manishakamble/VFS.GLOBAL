package com.vfs.fingerprint.base;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.netxsolutions.idx.impex.NIST;
import com.netxsolutions.idx.wsq.WSQWrapper;
import com.vfs.fingerprint.AppClass;
import com.vfs.fingerprint.R;
import com.vfs.fingerprint.database.DBHandler;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PreviewActivity extends AppCompatActivity {

    private String savePath = "/Veridium/TouchlessID_Export_Demo/";
    private File saveDir = null; // location to save biometric templates

    private static final String TAG = PreviewActivity.class.getSimpleName();
    private ImageView signatureImage, faceImage, imgViewFinger1, imgViewFinger2, imgViewFinger3, imgViewFinger4, imgViewFinger5, imgViewFinger6, imgViewFinger7, imgViewFinger8,
            imgViewFinger9, imgViewFinger10;
    private TextView txtViewPassportName, txtViewPassportSurName, txtViewSex, txtViewDOB, txtViewDocumentType, txtViewDocumentNumber, txtViewDateOfExpiry, txtViewNationality, txtViewPersonalNumber;
    private TextView btnSubmit, txtViewApplicationId, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        initViews();
    }

    private void initViews() {
        //signature init
        signatureImage = findViewById(R.id.signatureImage);

        //face init
        faceImage = findViewById(R.id.faceImage);

        //finger print init
        imgViewFinger1 = findViewById(R.id.imgViewFinger1);
        imgViewFinger2 = findViewById(R.id.imgViewFinger2);
        imgViewFinger3 = findViewById(R.id.imgViewFinger3);
        imgViewFinger4 = findViewById(R.id.imgViewFinger4);
        imgViewFinger5 = findViewById(R.id.imgViewFinger5);
        imgViewFinger6 = findViewById(R.id.imgViewFinger6);
        imgViewFinger7 = findViewById(R.id.imgViewFinger7);
        imgViewFinger8 = findViewById(R.id.imgViewFinger8);
        imgViewFinger9 = findViewById(R.id.imgViewFinger9);
        imgViewFinger10 = findViewById(R.id.imgViewFinger10);

        //mrz data init
        txtViewPassportName = findViewById(R.id.txtViewPassportName);
        txtViewPassportSurName = findViewById(R.id.txtViewPassportSurName);
        txtViewSex = findViewById(R.id.txtViewSex);
        txtViewDOB = findViewById(R.id.txtViewDOB);
        txtViewDocumentType = findViewById(R.id.txtViewDocumentType);
        txtViewDocumentNumber = findViewById(R.id.txtViewDocumentNumber);
        txtViewDateOfExpiry = findViewById(R.id.txtViewDateOfExpiry);
        txtViewNationality = findViewById(R.id.txtViewNationality);
        txtViewPersonalNumber = findViewById(R.id.txtViewPersonalNumber);

        //set signature image
        setSignatureImage();

        //set face image
        setFaceImage();

        //set finger image
        setFingerImage();

        //set mrz data
        setMrzData();

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createWsq();
                createNist();
                AppClass.getInstance().setBiometricStatus("Completed");
                addDataInDb();
                deleteAllFiles(1);
                Intent intent = new Intent(PreviewActivity.this, SuccessActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //init application id
        txtViewApplicationId = findViewById(R.id.txtViewApplicationId);
        //set application id
        txtViewApplicationId.setText("Application id:" + AppClass.getInstance().getApplicationNo());

        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAllFiles(0);
                Intent intent = new Intent(PreviewActivity.this, EnrollmentActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addDataInDb() {
        //get end date
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy_hhmm");
        String dateToStr = format.format(today);
        AppClass.getInstance().setEndDate(dateToStr);
        DBHandler dbHandler = DBHandler.Companion.getInstance(this);
        dbHandler.addMrzData();
    }

    private void deleteAllFiles(int no) {
        File sdCard = Environment.getExternalStorageDirectory();
        saveDir = new File(sdCard.getAbsolutePath() + savePath);
        Log.d(TAG, "deleteAllFiles: called ak check delete:" + saveDir.listFiles().length);
        for (File tempFile : saveDir.listFiles()) {
            if (no == 1) {
                if (tempFile.getAbsolutePath().contains("Face") || tempFile.getAbsolutePath().contains("Thumb") || tempFile.getAbsolutePath().contains("Finger") || tempFile.getAbsolutePath().contains("JSON")) {
                    tempFile.delete();
                }
            } else if (no == 0) {
                if (tempFile.getAbsolutePath().contains(AppClass.getInstance().getTxnId()) || tempFile.getAbsolutePath().contains("JSON")) {
                    tempFile.delete();
                }
            }
        }
        Log.d(TAG, "deleteAllFiles: called ak check delete after:" + saveDir.listFiles().length);
    }

    private void createWsq() {
        int licenseState = WSQWrapper.AuthenticateLicence(getString(R.string.netx_idx_licenced_to), getString(R.string.netx_idx_licence));
        Log.d(TAG, "createNist: called license state wsq:" + licenseState);
    }

    private void setMrzData() {
        AppClass appClass = AppClass.getInstance();
        txtViewPassportName.setText(appClass.getPassportName());
        txtViewPassportSurName.setText(appClass.getPassportSurname());
        txtViewSex.setText(appClass.getGender());
        txtViewDOB.setText(appClass.getDob());
        txtViewDocumentType.setText(appClass.getDocType());
        txtViewDocumentNumber.setText(appClass.getPassportNumber());
        txtViewDateOfExpiry.setText(appClass.getDateOfExpiry());
        txtViewNationality.setText(appClass.getNationality());
        txtViewPersonalNumber.setText(appClass.getPersonalNumber());
    }

    private void setFingerImage() {
        AppClass appClass = AppClass.getInstance();
        imgViewFinger1.setImageBitmap(base64ToBitmap(appClass.getFinger1()));
        imgViewFinger2.setImageBitmap(base64ToBitmap(appClass.getFinger2()));
        imgViewFinger3.setImageBitmap(base64ToBitmap(appClass.getFinger3()));
        imgViewFinger4.setImageBitmap(base64ToBitmap(appClass.getFinger4()));
        imgViewFinger5.setImageBitmap(base64ToBitmap(appClass.getFinger5()));
        imgViewFinger6.setImageBitmap(base64ToBitmap(appClass.getFinger6()));
        imgViewFinger7.setImageBitmap(base64ToBitmap(appClass.getFinger7()));
        imgViewFinger8.setImageBitmap(base64ToBitmap(appClass.getFinger8()));
        imgViewFinger9.setImageBitmap(base64ToBitmap(appClass.getThumb1()));
        imgViewFinger10.setImageBitmap(base64ToBitmap(appClass.getThumb2()));
    }

    private void setFaceImage() {
        if (AppClass.getInstance().getFaceBmp() != null) {
            faceImage.setImageBitmap(AppClass.getInstance().getFaceBmp());
        }
    }

    private void setSignatureImage() {
        if (AppClass.getInstance().getSignatureBitmap() != null) {
            signatureImage.setImageBitmap(AppClass.getInstance().getSignatureBitmap());
        }
    }

    private Bitmap base64ToBitmap(String encodedImage) {
        if (encodedImage == null)
            return null;
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private void createNist() {

        //        DBHandler dbHandler = DBHandler.Companion.getInstance(this);
        //        Log.d(TAG, "createNist: called ak chekc id:" + dbHandler.getId());
        //        dbHandler.getData(dbHandler.getId());
        Log.d(TAG, "onCreate: called ak check which 444:");
        int licenseStateWSQ = WSQWrapper.AuthenticateLicence(getString(R.string.netx_idx_licenced_to), getString(R.string.netx_idx_licence));
        NIST nist = new NIST();
        int licenseStateNist = nist.AuthenticateLicence(getString(R.string.netx_idx_licenced_to), getString(R.string.netx_idx_licence));
        int ret = nist.NewNIST();
        Log.d(TAG, "createNist: called ak license aj:" + licenseStateNist);
        //        DBHandler dbHandler= DBHandler.Companion.getInstance(this);
        //        dbHandler.getData(1);

        Log.d(TAG, "called ak check values nist is create today:$licenseStateWSQ");


        //        FingerModel fingerModel = AppClass.getInstance().getFingerModel();
        //        Log.d(TAG, "createNist: called ak check1:" + fingerModel.getSCALE100().getFingerprints().get(0).getFingerImpressionImage().getBinaryBase64ObjectBMP());


        //        nist.AddFingerprintImage(saveDir+"/RightThumb1.bmp", NIST.FingerPosition.RIGHT_THUMB, NIST.ImpressionType.LIVE_SCAN_PLAIN);

        /*       nist.AddFingerprintImage(fingerModel.getSCALE100().getFingerprints().get(0).getFingerImpressionImage().getBinaryBase64ObjectBMP(), NIST.FingerPosition.RIGHT_THUMB, NIST.ImpressionType.LIVE_SCAN_PLAIN);

        nist.AddFingerprintImage(fingerModel.getSCALE100().getFingerprints().get(0).getFingerImpressionImage().getBinaryBase64ObjectBMP(), NIST.FingerPosition.RIGHT_THUMB, NIST.ImpressionType.LIVE_SCAN_PLAIN);

        nist.AddFingerprintImage(fingerModel.getSCALE100().getFingerprints().get(0).getFingerImpressionImage().getBinaryBase64ObjectBMP(), NIST.FingerPosition.RIGHT_THUMB, NIST.ImpressionType.LIVE_SCAN_PLAIN);

        nist.AddFingerprintImage(dbHandler.getFC1(), NIST.FingerPosition.RIGHT_THUMB, NIST.ImpressionType.LIVE_SCAN_PLAIN);

        nist.AddFingerprintImage(dbHandler.getFC1(), NIST.FingerPosition.RIGHT_THUMB, NIST.ImpressionType.LIVE_SCAN_PLAIN);
        nist.AddFingerprintImage(dbHandler.getFC1(), NIST.FingerPosition.RIGHT_THUMB, NIST.ImpressionType.LIVE_SCAN_PLAIN);
        nist.AddFingerprintImage(dbHandler.getFC1(), NIST.FingerPosition.RIGHT_THUMB, NIST.ImpressionType.LIVE_SCAN_PLAIN);
        nist.AddFingerprintImage(dbHandler.getFC1(), NIST.FingerPosition.RIGHT_THUMB, NIST.ImpressionType.LIVE_SCAN_PLAIN);*/

        File sdCard = Environment.getExternalStorageDirectory();
        saveDir = new File(sdCard.getAbsolutePath() + savePath);

        AppClass appClass = AppClass.getInstance();


        nist.AddField(2, 31, appClass.getPassportName());

        nist.AddField(2, 32, appClass.getPassportSurname());

        nist.AddField(2, 33, appClass.getPassportNumber());

        nist.AddField(2, 34, appClass.getPassportDate());

        //			outputResult();
        //saving face image as jpg in nist file
        Log.d(TAG, "createNist: called ak save dir 2:" + nist.AddFacialImage(saveDir.toString() + "/" + appClass.getPassportName() + appClass.getPassportDate() + "Face.jpg", "F"));
        Log.d(TAG, "createNist for signature ak: " + nist.AddType8Image(saveDir.toString() + "/" + appClass.getPassportName() + appClass.getPassportDate() + "Face.jpg", NIST.SignatureRepresentationType.SCANNED_NOT_COMPRESSED, NIST.SignatureType.SIGNATURE_OF_SUBJECT, 500, 500));


        //        String FilePath1 = getAssetFile("RightThumb1.bmp");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(saveDir.toString() + "/Finger1.jpg", options);

        //encode finger and thumb into wsq
        Log.d(TAG, "check thumbs 1:" + WSQWrapper.EncodeWSQFile(saveDir.toString() + "/Thumb1.jpg", getCacheDir() + "/Thumb1.wsq", Float.parseFloat("2.25"), 0, 0, ""));
        Log.d(TAG, "check thumbs 2:" + WSQWrapper.EncodeWSQFile(saveDir.toString() + "/Thumb2.jpg", getCacheDir() + "/Thumb2.wsq", Float.parseFloat("2.25"), 0, 0, ""));

        WSQWrapper.EncodeWSQFile(saveDir.toString() + "/Finger1.jpg", getCacheDir() + "/Finger1.wsq", Float.parseFloat("2.25"), 0, 0, "");
        WSQWrapper.EncodeWSQFile(saveDir.toString() + "/Finger2.jpg", getCacheDir() + "/Finger2.wsq", Float.parseFloat("2.25"), 0, 0, "");
        WSQWrapper.EncodeWSQFile(saveDir.toString() + "/Finger3.jpg", getCacheDir() + "/Finger3.wsq", Float.parseFloat("2.25"), 0, 0, "");
        WSQWrapper.EncodeWSQFile(saveDir.toString() + "/Finger4.jpg", getCacheDir() + "/Finger4.wsq", Float.parseFloat("2.25"), 0, 0, "");

        WSQWrapper.EncodeWSQFile(saveDir.toString() + "/Finger5.jpg", getCacheDir() + "/Finger5.wsq", Float.parseFloat("2.25"), 0, 0, "");
        WSQWrapper.EncodeWSQFile(saveDir.toString() + "/Finger6.jpg", getCacheDir() + "/Finger6.wsq", Float.parseFloat("2.25"), 0, 0, "");
        WSQWrapper.EncodeWSQFile(saveDir.toString() + "/Finger7.jpg", getCacheDir() + "/Finger7.wsq", Float.parseFloat("2.25"), 0, 0, "");
        WSQWrapper.EncodeWSQFile(saveDir.toString() + "/Finger8.jpg", getCacheDir() + "/Finger8.wsq", Float.parseFloat("2.25"), 0, 0, "");

        //add fingers and thumb to nist file
        Log.d(TAG, "check thumbs 3:" + nist.AddFingerprintImage(getCacheDir() + "/Thumb1.wsq", NIST.FingerPosition.LEFT_THUMB, NIST.ImpressionType.LIVE_SCAN_PLAIN));
        Log.d(TAG, "check thumbs 4:" + nist.AddFingerprintImage(getCacheDir() + "/Thumb2.wsq", NIST.FingerPosition.RIGHT_THUMB, NIST.ImpressionType.LIVE_SCAN_PLAIN));

        //temp
//        nist.AddF("$cacheDir/Finger1.wsq", NIST.FingerPosition.LEFT_INDEX_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN)

        nist.AddFingerprintImage(getCacheDir() + "/Finger1.wsq", NIST.FingerPosition.LEFT_INDEX_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN);
        nist.AddFingerprintImage(getCacheDir() + "/Finger2.wsq", NIST.FingerPosition.LEFT_MIDDLE_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN);
        nist.AddFingerprintImage(getCacheDir() + "/Finger3.wsq", NIST.FingerPosition.LEFT_RING_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN);
        nist.AddFingerprintImage(getCacheDir() + "/Finger4.wsq", NIST.FingerPosition.LEFT_LITTLE_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN);

        nist.AddFingerprintImage(getCacheDir() + "/Finger5.wsq", NIST.FingerPosition.RIGHT_INDEX_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN);
        nist.AddFingerprintImage(getCacheDir() + "/Finger6.wsq", NIST.FingerPosition.RIGHT_MIDDLE_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN);
        nist.AddFingerprintImage(getCacheDir() + "/Finger7.wsq", NIST.FingerPosition.RIGHT_RING_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN);
        nist.AddFingerprintImage(getCacheDir() + "/Finger8.wsq", NIST.FingerPosition.RIGHT_LITTLE_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN);

        saveDir.mkdirs();

        String filePath = saveDir.toString() + "/" + appClass.getTxnId() + ".nist";
        Log.d(TAG, "createNist: called ak save dir 3:" + nist.SaveNISTFile(filePath));
        appClass.setNistPath(filePath);
        //        nist.SaveNISTFile(saveDir.toString() + "/DemoFileAk.nist");
    }

    static {
        Log.d(TAG, "static initializer: check static enabled");
        System.loadLibrary("WSQ");
        System.loadLibrary("NIST");
    }
}

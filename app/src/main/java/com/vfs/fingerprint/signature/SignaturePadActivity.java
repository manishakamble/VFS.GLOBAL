package com.vfs.fingerprint.signature;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.veridiumid.sdk.fourfintegrationexport.ExportConfig;
import com.veridiumid.sdk.model.biometrics.packaging.IBiometricFormats;
import com.vfs.fingerprint.AppClass;
import com.vfs.fingerprint.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static com.veridiumid.sdk.model.biometrics.packaging.IBiometricFormats.TemplateFormat.FORMAT_NIST;

public class SignaturePadActivity extends AppCompatActivity implements SignaturePad.OnSignedListener, View.OnClickListener {

    private static final String TAG = SignaturePadActivity.class.getSimpleName();
    private SignaturePad signaturePad;
    private CardView btnSave, btnClear;
    private String savePath = "/Veridium/TouchlessID_Export_Demo/";
    private File saveDir; // location to save biometric templates
    private String fileName;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature_pad);
        initViews();
    }

    private void initViews() {
        signaturePad = findViewById(R.id.signaturePad);
        btnSave = findViewById(R.id.btnSave);
        btnClear = findViewById(R.id.btnClear);
        btnSave.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        signaturePad.setOnSignedListener(this);
    }


    @Override
    public void onStartSigning() {

    }

    @Override
    public void onSigned() {


    }

    @Override
    public void onClear() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                onSavePressed();
                finish();
                break;
            case R.id.btnClear:
                onClearPressed();
                break;
        }
    }

    private void onSavePressed() {
        AppClass.getInstance().setSignatureBmp(signaturePad.getSignatureBitmap());
        Log.d(TAG, "save pressed:" + signaturePad.getSignatureBitmap());

        //convert bitmpa to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        signaturePad.getSignatureBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray=stream.toByteArray();
        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        storeSignatureBitmapToDb(base64);
        saveImage(signaturePad.getSignatureBitmap());
       // writeTemplate(stream.toByteArray());
    }

    private void storeSignatureBitmapToDb(String base64) {
//        DBHandler dbHandler=DBHandler.Companion.getInstance(this);
//        dbHandler.addSignatureInBase64(base64,dbHandler.getId());
    }

    private void onClearPressed() {
        AppClass.getInstance().setSignatureBmp(null);
        signaturePad.clear();
        finish();
    }

    private boolean writeTemplate(byte[] template) {
        // Create a save directory
        Log.d(TAG, "convertToNistFile: called ak nist6:");
        File sdCard = Environment.getExternalStorageDirectory();
        saveDir = new File(sdCard.getAbsolutePath() + savePath);

        String fileExtension = IBiometricFormats.TemplateFormat.getExtension(FORMAT_NIST);
        fileName = String.format("Signature_%03d_%d_%s%s", 101, System.currentTimeMillis(), ExportConfig.getFormat(), fileExtension);
        Log.d(TAG, "writeTemplate: called ak check file:" + ExportConfig.getFormat() + "____" + fileExtension);
        file = new File(saveDir, fileName);
        saveDir.mkdirs();
        Log.d(TAG, "convertToNistFile: called ak nist4:");
        try {
            FileOutputStream dos = new FileOutputStream(file);
            dos.write(template);
            dos.close();
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file))); // allow file to be seen via MTP
        } catch (Exception ex) {
            Log.d(TAG, "convertToNistFile: called ak nist5:"+ex.getMessage());
            ex.printStackTrace();
            return false;
        }

        Log.d(TAG, "convertToNistFile: called ak nist3:");
        convertToNistFile();

        //read .an2 file

       /* int size = (int) file.length();
        byte[] bytes = new byte[size];
        Log.d(TAG, "handleSuccess: find bmp 2:" + bytes.length);
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);*/
        return true;
    }

    private void convertToNistFile() {
     /*   Log.d(TAG, "convertToNistFile: called ak nist2:");
        File sdCard = Environment.getExternalStorageDirectory();
        saveDir = new File(sdCard.getAbsolutePath() + savePath);

        String fileExtension = IBiometricFormats.TemplateFormat.getExtension(FORMAT_NIST);

        fileName = "abc.an2";
        Log.d(TAG, "writeTemplate: called ak check file:" + ExportConfig.getFormat() + "____" + fileExtension);
        file = new File(saveDir, fileName);
        Log.d(TAG, "convertToNistFile: called ak nist11:"+file.getAbsolutePath());
        Nist nist = Jnbis.nist().decode(new File(file.getAbsolutePath()));
        Log.d(TAG, "convertToNistFile: called ak nist1:"+nist);*/
    }

    public Bitmap getBitmap(){
        return signaturePad.getSignatureBitmap();
    }

    private void saveImage(Bitmap tmp) {
        File sdCard = Environment.getExternalStorageDirectory();
        saveDir = new File(sdCard.getAbsolutePath() + savePath);

        AppClass appClass = AppClass.getInstance();

        String fileName = saveDir + "/" + "signature_" + appClass.getTxnId() + ".jpg";
        appClass.setSignaturePath(fileName);

        File file = new File(fileName);
        saveDir.mkdirs();
        try {
            FileOutputStream dos = new FileOutputStream(file);
            tmp.compress(Bitmap.CompressFormat.JPEG, 90, dos);
            dos.flush();
            dos.close();
            sendSignatureBroadcast();
        } catch (Exception ex) {
            Log.d(TAG, "Failed to write template file");
            ex.printStackTrace();
        }
    }

    private void sendSignatureBroadcast() {
        Intent intent = new Intent("signature");
        // You can also include some extra data.
//        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

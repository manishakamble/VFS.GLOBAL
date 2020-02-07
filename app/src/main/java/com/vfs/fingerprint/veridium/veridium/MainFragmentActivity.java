package com.vfs.fingerprint.veridium.veridium;

/*                  Veridium ID 4 Finger TouchlessID Export demo app.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.veridiumid.sdk.IVeridiumSDK;
import com.veridiumid.sdk.VeridiumSDK;
import com.veridiumid.sdk.fourfexport.FourFExportInterface;
import com.veridiumid.sdk.fourfintegrationexport.ExportConfig;
import com.veridiumid.sdk.licensing.exception.LicenseException;
import com.veridiumid.sdk.model.biometrics.packaging.IBiometricFormats.TemplateFormat;
import com.veridiumid.sdk.model.biometrics.results.BiometricResultsParser;
import com.veridiumid.sdk.model.biometrics.results.handling.IBiometricResultsHandler;
import com.veridiumid.sdk.support.base.VeridiumBaseActivity;
import com.veridiumid.sdk.support.help.ToastHelper;
import com.vfs.fingerprint.AppClass;
import com.vfs.fingerprint.R;
import com.vfs.fingerprint.database.FingerModel;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.veridiumid.sdk.model.biometrics.packaging.IBiometricFormats.TemplateFormat.FORMAT_JSON;

public class MainFragmentActivity extends VeridiumBaseActivity {

    private static final String LOG_TAG = MainFragmentActivity.class.getSimpleName();

    private static final int REQUEST_CAPTURE = 3;
    private static final int REQUEST_CAPTURE_8F = 4;
    private static final int REQUEST_2F = 6;
    private static final int REQUEST_INDIVIDUAL_F = 7;
    private static final int REQUEST_APP_MISSING_FINGERS = 0;
    private static final String TAG = MainFragmentActivity.class.getSimpleName();

    private static int session_id;
    private static int first_session_id = 1;
    private TextView tv_session_id;

    private String savePath = "/Veridium/TouchlessID_Export_Demo/";
    private File saveDir; // location to save biometric templates

    private Button missingFButton;

    private AlertDialog dialog_permissions;

    SharedPreferences sharedPref;

    private IVeridiumSDK mBiometricSDK;

    private boolean missingFEnabled = false;
    private int count;
    private int countToFinish;

    @Override
    @UiThread
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openAppPreferences();
        readAppData();
        setContentView(R.layout.activity_fragment_main);


        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new HomeFragment(), null).commitAllowingStateLoss();

        // Configure settings for fingerprint Export
        ExportConfig.setFormat(FORMAT_JSON);
        ExportConfig.setCalculate_NFIQ(true);

        Log.d(TAG, "onCreate: called ak check cal info::" + ExportConfig.getCalculateNFIQ());

        ExportConfig.setBackground_remove(true);
        ExportConfig.setPackDebugInfo(false);
        ExportConfig.setPackExtraScale(true);
        ExportConfig.setPackAuditImage(false);
        ExportConfig.setPack_raw_scaled(false);
        ExportConfig.setUseNistType4(false);
//        ExportConfig.setPack_wsq_scaled(true);
//        ExportConfig.setPack_png_scaled(false);
//        ExportConfig.setPackExtraScale(false);

        // Two ways to set WSQ bitrate.
        ExportConfig.setWSQCompressRatio(ExportConfig.WSQCompressRatio.COMPRESS_5to1);
        //ExportConfig.setWSQCompressionBitrate(2.25f); // range is 0.2 to 6.0

        ExportConfig.setCaptureHand(ExportConfig.CaptureHand.LEFT);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    @UiThread
    protected void initWindow(Window window) {
        super.initWindow(window);
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
    }

    private void openAppPreferences() {
        sharedPref = this.getPreferences(this.MODE_PRIVATE);
    }

    private void writeAppData() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.saved_session_id), session_id);
        editor.commit();
        Log.d(LOG_TAG, "Wrote session id: " + session_id);
    }

    private void readAppData() {
        session_id = sharedPref.getInt(getString(R.string.saved_session_id), first_session_id);
        Log.d(LOG_TAG, "Read session id: " + session_id);
    }

    /* Set session id text to the last saved value and write current to storage
     */
    private void updateSessionID() {
        writeAppData();
        if (session_id > first_session_id) {
        }
    }

    @Override
    protected void onStart() {
        if (Build.VERSION.SDK_INT > 22) {
            checkPermissions(requiredPermissions);
        }
        super.onStart();
    }

    private static final int REQUEST_APP_SETTINGS = 168;

    private static final String[] requiredPermissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
    };

    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermissions(@NonNull String... permissions) {

        List<String> ungranted = new ArrayList<String>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    permission)
                    != PackageManager.PERMISSION_GRANTED) {
                ungranted.add(permission);
            }
        }

        if (ungranted.size() != 0) {
            ActivityCompat.requestPermissions(this,
                    ungranted.toArray(new String[0]),
                    REQUEST_APP_SETTINGS);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        for (int r : grantResults) {
            if (r != PackageManager.PERMISSION_GRANTED) {
                showDeniedPermissionsDialog();
                break;
            }
        }
    }

    private void showDeniedPermissionsDialog() {
        if (dialog_permissions != null) {
            if (dialog_permissions.isShowing()) {
                return;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.permissions_denied_txt)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        dialog_permissions = builder.create();
        dialog_permissions.show();
    }

    /* A custom IBiometricResultsHandler, handles the resulting data from the biometric capture
     */
    IBiometricResultsHandler customCaptureHandler = new IBiometricResultsHandler() {
        @Override
        public void handleSuccess(Map<String, byte[][]> results) {
            Log.d(TAG, "handleSuccess: find bmp 1:" + results);
            if (results == null || results.size() == 0) {
                ToastHelper.showMessage(MainFragmentActivity.this, R.string.error);
            }

            // Here we extract the template in the first element and send to be written to disk
            // The output template is always in the first slot under the "4E" UID (FourF Export UID)

            for (Map.Entry<String, byte[][]> entry : results.entrySet()) {
                String bio_key = entry.getKey();
                byte[][] template = entry.getValue();

                Bitmap bitmap = BitmapFactory.decodeByteArray(template[0], 0, template[0].length);

                if (bio_key.equals(FourFExportInterface.UID)) {
                    writeTemplate(template[0]);
                    ToastHelper.showMessage(MainFragmentActivity.this, R.string.save_success);
                    session_id++;
                    updateSessionID();
                    if(countToFinish==0){
                        countToFinish=1;
                    }else if(countToFinish==1){
                        countToFinish=0;
                        finish();
                    }
                    return;
                }
            }
            ToastHelper.showMessage(MainFragmentActivity.this, R.string.error);
        }

        @Override
        public void handleFailure() {
            ToastHelper.showMessage(MainFragmentActivity.this, R.string.failed);
        }

        @Override
        public void handleCancellation() {
            ToastHelper.showMessage(MainFragmentActivity.this, R.string.cancelled);
        }

        @Override
        public void handleError(String message) {
            ToastHelper.showMessage(MainFragmentActivity.this, getString(R.string.error) + message);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_APP_MISSING_FINGERS) {
            missingFEnabled = data.getBooleanExtra("IndividualCapture", false);
            missingFButton.setEnabled(missingFEnabled && CustomFourFBiometricsActivity.checkFormatSupportedCaptureThumb(ExportConfig.getFormat()));
        } else {
            BiometricResultsParser.parse(resultCode, data, customCaptureHandler);
        }

        // complete
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* Setup UI components and callbacks
     */
    public void onHomeFragmentReady(final HomeFragment homeFragment) {
        initSDK();

        // set version info
        if (mBiometricSDK != null) {
        }

        homeFragment.button_2F.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExportConfig.mode = ExportConfig.ExportMode.TWO_THUMB;
                ExportConfig.optimiseForIndexLittle(false);
                ExportConfig.optimiseForIndex(false);
                if (mBiometricSDK != null) {
                    Intent captureIntent2 = mBiometricSDK.export(new String[]{FourFExportInterface.UID});
                    startActivityForResult(captureIntent2, REQUEST_2F);
                } else {
                    Log.d(TAG, "onClick: called ak check inint 2");
                    ToastHelper.showMessage(MainFragmentActivity.this, R.string.engine_not_initialise);
                    Log.e(LOG_TAG, "IVeridiumSDK object not initialised");
                }
            }
        });

        homeFragment.button_8F_capture_doubleOptimise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExportConfig.mode = ExportConfig.ExportMode.EIGHT_F;
                ExportConfig.optimiseForIndexLittle(true);
                if (mBiometricSDK != null) {
                    Intent captureIntent = mBiometricSDK.export(new String[]{FourFExportInterface.UID});
                    startActivityForResult(captureIntent, REQUEST_CAPTURE_8F);
                } else {
                    Log.d(TAG, "onClick: called ak check inint 7");
                    ToastHelper.showMessage(MainFragmentActivity.this, R.string.engine_not_initialise);
                    Log.e(LOG_TAG, "IVeridiumSDK object not initialised");
                }
            }
        });

        // Set current session id text
        updateSessionID();

        // Populate spinner with IBiometricFormats
        ArrayList<String> formats = new ArrayList<String>();
        for (TemplateFormat f : TemplateFormat.values()) {
            formats.add(TemplateFormat.resolveFriendly(f));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, formats);

        // Set spinner to currently set custom format - do after intiSDK has been called

        // disable 8F button if format not supported

        homeFragment.button_2F.setEnabled(CustomFourFBiometricsActivity.checkFormatSupportedCaptureThumb(ExportConfig.getFormat()));


        // disable All finger capture button if format not supported
        homeFragment.button_8F_capture_doubleOptimise.setEnabled(
                CustomFourFBiometricsActivity.checkFormatSupportedAllFingerOptimise(ExportConfig.getFormat())
        );


        // Create a save directory
        File sdCard = Environment.getExternalStorageDirectory();
        saveDir = new File(sdCard.getAbsolutePath() + savePath);
    }

    private void initSDK() {
        try {
            mBiometricSDK = VeridiumSDK.getSingleton();
        } catch (LicenseException e) {
            Log.d(TAG, "initSDK: called ak check error:" + e.getMessage());
            ToastHelper.showMessage(MainFragmentActivity.this, R.string.license_is_invalid);
            e.printStackTrace();
        }

        // UI customisation example using the UICustomization class
        // Uses hex colour encoding eg. (UICust)
        // 0xffffffff white
        // 0xffff0000 red
        // 0xffffff00 yellow
        // 0xff005eff dark blue
        //UICustomization.setForegroundColor(0xffffff00);
        //UICustomization.setBackgroundColor(0xffff0000);
        //UICustomization.setDialogColor(0xff005eff);
//        UICustomization.setLogo(getResources().getDrawable(R.drawable.veridium_logo_capture_screen));
    }

    /* Write a template to disk with session ID and timestamp
     *
     */
    private boolean writeTemplate(byte[] template) {
        Log.d(TAG, "writeTemplate: called ak check ext1:" + template);

        String fileExtension = TemplateFormat.getExtension(FORMAT_JSON);
        Log.d(TAG, "writeTemplate: called ak check ext2:" + fileExtension);
        String fileName = String.format("TouchlessID_%03d_%d_%s%s", session_id, System.currentTimeMillis(), ExportConfig.getFormat(), fileExtension);
        Log.d(LOG_TAG, "Save template with " + fileName);
        Log.d(LOG_TAG, "Template byte size to disk: " + template.length);

        File file = new File(saveDir, fileName);
        saveDir.mkdirs();
        try {
            FileOutputStream dos = new FileOutputStream(file);
            dos.write(template);
            dos.close();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file))); // allow file to be seen via MTP
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Failed to write template file");
            ex.printStackTrace();
            return false;
        }

        //read .an2 file

        int size = (int) file.length();
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

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        readFileData(file);
        return true;
    }

    private void readFileData(File file) {
        Log.d(TAG, "readFileData: called ak check3:");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            String result = convertStreamToString(fis);
            JsonParser parser = new JsonParser();
            JsonElement mJson = parser.parse(result);
            Gson gson = new Gson();
            AppClass appAppClass = AppClass.getInstance();
            FingerModel fingerModel = gson.fromJson(mJson, FingerModel.class);
            if (count == 0) {
//                AppClass.getInstance().setFingerModel(fingerModel);
                appAppClass.setFinger1(fingerModel.getSCALE100().getFingerprints().get(0).getFingerImpressionImage().getBinaryBase64ObjectBMP());
                appAppClass.setFinger2(fingerModel.getSCALE100().getFingerprints().get(1).getFingerImpressionImage().getBinaryBase64ObjectBMP());
                appAppClass.setFinger3(fingerModel.getSCALE100().getFingerprints().get(2).getFingerImpressionImage().getBinaryBase64ObjectBMP());
                appAppClass.setFinger4(fingerModel.getSCALE100().getFingerprints().get(3).getFingerImpressionImage().getBinaryBase64ObjectBMP());

                appAppClass.setFinger5(fingerModel.getSCALE100().getFingerprints().get(4).getFingerImpressionImage().getBinaryBase64ObjectBMP());
                appAppClass.setFinger6(fingerModel.getSCALE100().getFingerprints().get(5).getFingerImpressionImage().getBinaryBase64ObjectBMP());
                appAppClass.setFinger7(fingerModel.getSCALE100().getFingerprints().get(6).getFingerImpressionImage().getBinaryBase64ObjectBMP());
                appAppClass.setFinger8(fingerModel.getSCALE100().getFingerprints().get(7).getFingerImpressionImage().getBinaryBase64ObjectBMP());

                count++;
                createFingerFile();
            } else if (count == 1) {
                appAppClass.setThumb1(fingerModel.getSCALE100().getFingerprints().get(0).getFingerImpressionImage().getBinaryBase64ObjectBMP());

                appAppClass.setThumb2(fingerModel.getSCALE100().getFingerprints().get(1).getFingerImpressionImage().getBinaryBase64ObjectBMP());
//                AppClass.getInstance().setThumbModel(fingerModel);
                count = 0;
                createThumbFile();
            }
            storeDataToDataBase(fingerModel);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "readFileData: called ak check1:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "readFileData: called ak check2:" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "readFileData: called ak check5:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createThumbFile() {
        for (int i = 0; i < AppClass.getInstance().getListOfThumb().size(); i++) {
            String fileName = "";
            File sdCard = Environment.getExternalStorageDirectory();
            saveDir = new File(sdCard.getAbsolutePath() + savePath);
            File file = new File(saveDir, "Thumb"+(i+1)+"_"+AppClass.getInstance().getTxnId()+".jpg");
            saveDir.mkdirs();

            try {
                byte[] imageAsBytes = Base64.decode(AppClass.getInstance().getListOfThumb().get(i).getBytes(), 0);
//                imgView.setImageBitmap();
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                FileOutputStream dos = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, dos);
                dos.write(imageAsBytes);
                dos.flush();
                dos.close();
                sendFingerPrintBroadcast();
            } catch (Exception ex) {
                Log.d(LOG_TAG, "Failed to write template file");
                ex.printStackTrace();
            }
        }
    }

    private void sendFingerPrintBroadcast() {
        Intent intent = new Intent("finger-print");
        // You can also include some extra data.
//        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void createFingerFile() {
        for (int i = 0; i < AppClass.getInstance().getListOfFinger().size(); i++) {
            String fileName = "";
            File sdCard = Environment.getExternalStorageDirectory();
            saveDir = new File(sdCard.getAbsolutePath() + savePath);
            File file = new File(saveDir, "Finger"+(i+1)+"_"+AppClass.getInstance().getTxnId()+".jpg");
            saveDir.mkdirs();

            try {
                byte[] imageAsBytes = Base64.decode(AppClass.getInstance().getListOfFinger().get(i).getBytes(), 0);
//                imgView.setImageBitmap();
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                FileOutputStream dos = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, dos);
                dos.write(imageAsBytes);
                dos.flush();
                dos.close();

            } catch (Exception ex) {
                Log.d(LOG_TAG, "Failed to write template file");
                ex.printStackTrace();
            }
        }
    }

    private void storeDataToDataBase(FingerModel fingerModel) {
        /*DBHandler dbHandler = DBHandler.Companion.getInstance(this);
        Log.d(TAG, "storeDataToDataBase: called ak check:" + count);
        if (count == 0) {
            dbHandler.addFingerData(fingerModel,dbHandler.getId());
            count++;
        } else if (count == 1) {
            dbHandler.getId();
            dbHandler.addThumbData(fingerModel,dbHandler.getId());
            count = 0;
        }*/

    }

    public static String convertStreamToString(InputStream is) {
        StringBuilder sb = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            sb = new StringBuilder();
            String line = null;
            while (true) {
                if (!((line = reader.readLine()) != null)) break;
                sb.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}

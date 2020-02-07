package com.vfs.fingerprint.face;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.tascent.mx.android.sdk.ConnectionListener;
import com.tascent.mx.android.sdk.DeviceType;
import com.tascent.mx.android.sdk.FaceOption;
import com.tascent.mx.android.sdk.Tascent;
import com.tascent.plugin.sdk.RawImage;
import com.tascent.plugin.sdk.captures.face.FaceImage;
import com.veridiumid.sdk.fourfintegrationexport.ExportConfig;
import com.veridiumid.sdk.model.biometrics.packaging.IBiometricFormats;
import com.vfs.fingerprint.AppClass;
import com.vfs.fingerprint.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import kotlin.jvm.internal.Intrinsics;

import static com.veridiumid.sdk.model.biometrics.packaging.IBiometricFormats.TemplateFormat.FORMAT_NIST;

public class FaceActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 10;
    private static final int FACE_CAPTURE_REQUEST_CODE = 20;
    private static final String TAG = FaceActivity.class.getSimpleName();
    //public static final FaceActivity.Companion Companion = new FaceActivity.Companion((DefaultConstructorMarker)null);
    private HashMap _$_findViewCache;

    private ImageView imgView;

    private String savePath = "/Veridium/TouchlessID_Export_Demo/";
    private File saveDir; // location to save biometric templates

    private CardView confirmationButton,retry_button;

    private final ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void onConnectionComplete(DeviceType deviceType) {
            Log.i("MainScreenActivity", "onConnectionComplete " + deviceType);
        }

        @Override
        public void onConnectionAttached(DeviceType deviceType) {
            Log.e("MainScreenActivity", "onConnectionAttached: " + deviceType);
        }

        @Override
        public void onConnectionLost() {
            Log.w("MainScreenActivity", "onConnectionLost");
        }

        @Override
        public void onConnectionError(Exception e) {
            Log.e("MainScreenActivity", "onConnectionError " + e.getMessage());
        }
    };

    private final void captureFace() {
        try {
            FaceOption options = FaceOption.Companion.defaultOptions().withTorchMode(false).withUseFrontCamera(false).withFlashlightMode(false).withFailedQualityRetries(2);
            Intent intent = Tascent.INSTANCE.createFaceCaptureIntent((Context) this, options);
            this.startActivityForResult(intent, FACE_CAPTURE_REQUEST_CODE);
        } catch (Exception var3) {
            String var10001 = var3.getLocalizedMessage();
            Toast.makeText(FaceActivity.this, var10001, Toast.LENGTH_LONG).show();
        }

    }

    private final View.OnClickListener onFaceCaptureClicked = (View.OnClickListener) (new View.OnClickListener() {
        public final void onClick(View it) {
            FaceActivity.this.captureFace();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        Tascent.INSTANCE.addConnectionListener((ConnectionListener) this.connectionListener);
        /*if (this.faceCaptureButton == null) {
            Toast.makeText(FaceActivity.this, "NULL", Toast.LENGTH_LONG).show();
        }
        var10000.setOnClickListener(this.onFaceCaptureClicked);*/
        imgView = findViewById(R.id.imgView);
        captureFace();

        confirmationButton = findViewById(R.id.confirmation_button);

        retry_button = findViewById(R.id.retry_button);
        retry_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppClass.getInstance().saveFaceImage(null);
                finish();
            }
        });

        confirmationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                sendMrzBroadcast();
                finish();

            }
        });
    }

    private final void handleFaceCaptureResponse(int resultCode, Intent data) {
        if (resultCode == -1 && data != null) {
            Log.e("FACE", "retrieve face image");
            FaceImage captureResult = Tascent.INSTANCE.popFaceCaptureResult(data);
            if (captureResult != null) {
                Toast.makeText(FaceActivity.this, "Face Capture SUCCESS",
                        Toast.LENGTH_LONG).show();
                RawImage var10000 = captureResult.getImage();
                Bitmap tmp = var10000 != null ? var10000.toBitmap() : null;
                Log.d(TAG, "handleFaceCaptureResponse: called test ak1:" + tmp);

                imgView.setImageBitmap(tmp);

                //convert bitmpa to byte array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                tmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

                String base64 = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
//                storeFaceBitmapToDb(base64);
                saveImage(tmp);
//                writeTemplate(stream.toByteArray());
//                finish();
            } else {
                Toast.makeText(FaceActivity.this, "Face capture result is null",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e("handleFaceCaptureResponse", "No result or data");
            Toast.makeText(FaceActivity.this, "face capture failed!",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void sendMrzBroadcast() {
        Intent intent = new Intent("face-recognizition");
        // You can also include some extra data.
//        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void saveImage(Bitmap tmp) {
        AppClass.getInstance().saveFaceImage(tmp);
        File sdCard = Environment.getExternalStorageDirectory();
        saveDir = new File(sdCard.getAbsolutePath() + savePath);

        AppClass appClass = AppClass.getInstance();
        String fileName = "Face_" + appClass.getTxnId() + ".jpg";
        File file = new File(saveDir, fileName);
        saveDir.mkdirs();
        try {
            FileOutputStream dos = new FileOutputStream(file);
            tmp.compress(Bitmap.CompressFormat.JPEG, 90, dos);
            dos.flush();
            dos.close();
        } catch (Exception ex) {
            Log.d(TAG, "Failed to write template file");
            ex.printStackTrace();
        }
    }

    private void storeFaceBitmapToDb(String base64) {
//        DBHandler dbHandler=DBHandler.Companion.getInstance(this);
//        dbHandler.addFacialInBase64(base64);
    }

    private boolean writeTemplate(byte[] template) {
        // Create a save directory
        File sdCard = Environment.getExternalStorageDirectory();
        saveDir = new File(sdCard.getAbsolutePath() + savePath);

        String fileExtension = IBiometricFormats.TemplateFormat.getExtension(FORMAT_NIST);
        String fileName = String.format("Face_%03d_%d_%s%s", 101, System.currentTimeMillis(), ExportConfig.getFormat(), fileExtension);
        Log.d(TAG, "writeTemplate: called ak check file:" + ExportConfig.getFormat() + "____" + fileExtension);
        File file = new File(saveDir, fileName);
        saveDir.mkdirs();
        try {
            FileOutputStream dos = new FileOutputStream(file);
            dos.write(template);
            dos.close();
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file))); // allow file to be seen via MTP
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

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

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FACE_CAPTURE_REQUEST_CODE) {
            this.handleFaceCaptureResponse(resultCode, data);
        }
    }

    protected void onStart() {
        super.onStart();
        Tascent.INSTANCE.requestPermissions((Activity) this, REQUEST_PERMISSIONS);
        Tascent var1 = Tascent.INSTANCE;
        Boolean var3 = false;
        var1.start((Context) this, false);
    }

    protected void onDestroy() {
        super.onDestroy();
        Tascent var10000 = Tascent.INSTANCE;
        String var10001 = FaceActivity.class.getName();
        Intrinsics.checkExpressionValueIsNotNull(var10001, "FaceActivity::class.java.name");
        var10000.unregisterFromBatteryEvents(var10001);
        Tascent.INSTANCE.removeAllConnectionListeners();
    }

    private final void toast(final String text) {
        this.runOnUiThread((Runnable) (new Runnable() {
            public final void run() {
                Toast.makeText((Context) FaceActivity.this, (CharSequence) text, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    public View _$_findCachedViewById(int var1) {
        if (this._$_findViewCache == null) {
            this._$_findViewCache = new HashMap();
        }

        View var2 = (View) this._$_findViewCache.get(var1);
        if (var2 == null) {
            var2 = this.findViewById(var1);
            this._$_findViewCache.put(var1, var2);
        }

        return var2;
    }

    public void _$_clearFindViewByIdCache() {
        if (this._$_findViewCache != null) {
            this._$_findViewCache.clear();
        }

    }
}

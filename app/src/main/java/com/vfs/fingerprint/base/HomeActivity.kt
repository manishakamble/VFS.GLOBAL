package com.vfs.fingerprint.base

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import com.netxsolutions.idx.impex.NIST
import com.netxsolutions.idx.wsq.WSQWrapper
import com.vfs.fingerprint.AppClass
import com.vfs.fingerprint.R
import com.vfs.fingerprint.anylinedocumentscanner.DocScanUIMainActivity
import com.vfs.fingerprint.anylinemrz.mrz.ScanMrzActivity
import com.vfs.fingerprint.scanbotdocumentscan.PagePreviewActivity
import com.vfs.fingerprint.scanbotdocumentscan.PageRepository
import com.vfs.fingerprint.face.FaceActivity
import com.vfs.fingerprint.scanbotmrz.MRZDialogFragment
import com.vfs.fingerprint.signature.SignaturePadActivity
import com.vfs.fingerprint.veridium.veridium.MainFragmentActivity

import net.doo.snap.camera.CameraPreviewMode

import java.io.File
import java.io.FileOutputStream

import io.scanbot.mrzscanner.model.MRZRecognitionResult
import io.scanbot.sdk.persistence.Page
import io.scanbot.sdk.ui.view.camera.DocumentScannerActivity
import io.scanbot.sdk.ui.view.camera.configuration.DocumentScannerConfiguration
import io.scanbot.sdk.ui.view.mrz.MRZScannerActivity
import io.scanbot.sdk.ui.view.mrz.configuration.MRZScannerConfiguration

class HomeActivity : AppCompatActivity(), View.OnClickListener {
    private var lnrLytPassportReader: LinearLayout? = null
    private var lnrLytFingerprint: LinearLayout? = null
    private var lnrLytFace: LinearLayout? = null
    private var lnrLytSignature: LinearLayout? = null
    private var lnrLytDocumentScanner: LinearLayout? = null
    private var btnSubmit: Button? = null

    private val savePath = "/Veridium/TouchlessID_Export_Demo/"
    private var saveDir: File? = null // location to save biometric templates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        lnrLytPassportReader = findViewById(R.id.lnrLytPassportReader)
        lnrLytPassportReader!!.setOnClickListener(this)

        lnrLytFingerprint = findViewById(R.id.lnrLytFingerprint)
        lnrLytFingerprint!!.setOnClickListener(this)

        btnSubmit = findViewById(R.id.btnSubmit)
        btnSubmit!!.setOnClickListener(this)

        lnrLytFace = findViewById(R.id.lnrLytFace)
        lnrLytFace!!.setOnClickListener(this)

        lnrLytSignature = findViewById(R.id.lnrLytSignature)
        lnrLytSignature!!.setOnClickListener(this)

        lnrLytDocumentScanner = findViewById(R.id.lnrLytDocumentScanner)
        lnrLytDocumentScanner!!.setOnClickListener(this)
    }

    private fun createNist() {

        //        DBHandler dbHandler = DBHandler.Companion.getInstance(this);
        //        Log.d(TAG, "createNist: called ak chekc id:" + dbHandler.getId());
        //        dbHandler.getData(dbHandler.getId());
        Log.d(TAG, "onCreate: called ak check which 444:")
        val licenseStateWSQ = WSQWrapper.AuthenticateLicence(resources.getString(R.string.netx_idx_licenced_to), resources.getString(R.string.netx_idx_licence))
        val nist = NIST()
        val licenseStateNist = nist.AuthenticateLicence(resources.getString(R.string.netx_idx_licenced_to), resources.getString(R.string.netx_idx_licence))
        val ret = nist.NewNIST()
        Log.d(TAG, "createNist: called ak license aj:$licenseStateNist")
        //        DBHandler dbHandler= DBHandler.Companion.getInstance(this);
        //        dbHandler.getData(1);

        Log.d(TAG, "called ak check values nist is create today:$licenseStateWSQ")


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

        val sdCard = Environment.getExternalStorageDirectory()
        saveDir = File(sdCard.absolutePath + savePath)

        val appClass = AppClass.getInstance()

        Log.d(TAG,"called ak check passport name:"+appClass.passportName)

        Log.d(TAG,"called ak check passport surname:"+appClass.passportName)

        Log.d(TAG,"called ak check passport passportnumber:"+appClass.passportName)

        Log.d(TAG,"called ak check passport date:"+appClass.passportName)

        nist.AddField(2, 31, appClass.passportName)

        nist.AddField(2, 32, appClass.passportSurname)

        nist.AddField(2, 33, appClass.passportNumber)

        nist.AddField(2, 34, appClass.passportDate)

        //			outputResult();
        //saving face image as jpg in nist file
        Log.d(TAG, "createNist: called ak save dir 2:" + nist.AddFacialImage(saveDir.toString() + "/" + appClass.passportName + appClass.passportDate + "Face.jpg", "F"))
        Log.d(TAG, "createNist for signature ak: " + nist.AddType8Image(saveDir.toString() + "/" + appClass.passportName + appClass.passportDate + "Face.jpg", NIST.SignatureRepresentationType.SCANNED_NOT_COMPRESSED, NIST.SignatureType.SIGNATURE_OF_SUBJECT, 500, 500))


        //        String FilePath1 = getAssetFile("RightThumb1.bmp");
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeFile(saveDir!!.toString() + "/Finger1.jpg", options)

        //encode finger and thumb into wsq
        Log.d(TAG,"check thumbs 1:"+WSQWrapper.EncodeWSQFile(saveDir!!.toString() + "/Thumb1.jpg", "$cacheDir/Thumb1.wsq", 2.25.toFloat(), 0, 0, ""))
        Log.d(TAG,"check thumbs 2:"+WSQWrapper.EncodeWSQFile(saveDir!!.toString() + "/Thumb2.jpg", "$cacheDir/Thumb2.wsq", 2.25.toFloat(), 0, 0, ""))

        WSQWrapper.EncodeWSQFile(saveDir!!.toString() + "/Finger1.jpg", "$cacheDir/Finger1.wsq", 2.25.toFloat(), 0, 0, "")
        WSQWrapper.EncodeWSQFile(saveDir!!.toString() + "/Finger2.jpg", "$cacheDir/Finger2.wsq", 2.25.toFloat(), 0, 0, "")
        WSQWrapper.EncodeWSQFile(saveDir!!.toString() + "/Finger3.jpg", "$cacheDir/Finger3.wsq", 2.25.toFloat(), 0, 0, "")
        WSQWrapper.EncodeWSQFile(saveDir!!.toString() + "/Finger4.jpg", "$cacheDir/Finger4.wsq", 2.25.toFloat(), 0, 0, "")

        WSQWrapper.EncodeWSQFile(saveDir!!.toString() + "/Finger5.jpg", "$cacheDir/Finger5.wsq", 2.25.toFloat(), 0, 0, "")
        WSQWrapper.EncodeWSQFile(saveDir!!.toString() + "/Finger6.jpg", "$cacheDir/Finger6.wsq", 2.25.toFloat(), 0, 0, "")
        WSQWrapper.EncodeWSQFile(saveDir!!.toString() + "/Finger7.jpg", "$cacheDir/Finger7.wsq", 2.25.toFloat(), 0, 0, "")
        WSQWrapper.EncodeWSQFile(saveDir!!.toString() + "/Finger8.jpg", "$cacheDir/Finger8.wsq", 2.25.toFloat(), 0, 0, "")

        //add fingers and thumb to nist file
        Log.d(TAG,"check thumbs 3:"+nist.AddFingerprintImage("$cacheDir/Thumb1.wsq", NIST.FingerPosition.LEFT_THUMB, NIST.ImpressionType.LIVE_SCAN_PLAIN))
        Log.d(TAG,"check thumbs 4:"+nist.AddFingerprintImage("$cacheDir/Thumb2.wsq", NIST.FingerPosition.RIGHT_THUMB, NIST.ImpressionType.LIVE_SCAN_PLAIN))

        //temp
//        nist.AddF("$cacheDir/Finger1.wsq", NIST.FingerPosition.LEFT_INDEX_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN)

        nist.AddFingerprintImage("$cacheDir/Finger1.wsq", NIST.FingerPosition.LEFT_INDEX_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN)
        nist.AddFingerprintImage("$cacheDir/Finger2.wsq", NIST.FingerPosition.LEFT_MIDDLE_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN)
        nist.AddFingerprintImage("$cacheDir/Finger3.wsq", NIST.FingerPosition.LEFT_RING_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN)
        nist.AddFingerprintImage("$cacheDir/Finger4.wsq", NIST.FingerPosition.LEFT_LITTLE_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN)

        nist.AddFingerprintImage("$cacheDir/Finger5.wsq", NIST.FingerPosition.RIGHT_INDEX_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN)
        nist.AddFingerprintImage("$cacheDir/Finger6.wsq", NIST.FingerPosition.RIGHT_MIDDLE_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN)
        nist.AddFingerprintImage("$cacheDir/Finger7.wsq", NIST.FingerPosition.RIGHT_RING_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN)
        nist.AddFingerprintImage("$cacheDir/Finger8.wsq", NIST.FingerPosition.RIGHT_LITTLE_FINGER, NIST.ImpressionType.LIVE_SCAN_PLAIN)

        saveDir!!.mkdirs()

        Log.d(TAG, "createNist: called ak save dir 3:" + nist.SaveNISTFile(saveDir!!.toString() + "/Main.nist"))
        //        nist.SaveNISTFile(saveDir.toString() + "/DemoFileAk.nist");
    }

    internal fun getAssetFile(assetName: String): String {
        val f = File("$cacheDir/$assetName")
        if (!f.exists())
            try {

                val `is` = assets.open(assetName)
                val size = `is`.available()
                val buffer = ByteArray(size)
                `is`.read(buffer)
                `is`.close()


                val fos = FileOutputStream(f)
                fos.write(buffer)
                fos.close()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        return f.path
    }

    private fun createWsq() {
        val licenseState = WSQWrapper.AuthenticateLicence(resources.getString(R.string.netx_idx_licenced_to), resources.getString(R.string.netx_idx_licence))
        Log.d(TAG, "createNist: called license state wsq:$licenseState")
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.lnrLytPassportReader -> startMrzActivity()
            R.id.btnSubmit -> {
                createWsq()
                createNist()
            }
            R.id.lnrLytFingerprint -> openFingerScan()
            R.id.lnrLytFace -> openFaceCapture()
            R.id.lnrLytSignature -> openSignatureScreen()
            R.id.lnrLytDocumentScanner -> openDocumentScanner()
        }
    }

    private fun openDocumentScanner() {
        /*val cameraConfiguration = DocumentScannerConfiguration()
        cameraConfiguration.setCameraPreviewMode(CameraPreviewMode.FIT_IN)
        cameraConfiguration.setIgnoreBadAspectRatio(true)
        cameraConfiguration.setBottomBarBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        cameraConfiguration.setBottomBarButtonsColor(ContextCompat.getColor(this, R.color.fourf_default_ui_green))
        cameraConfiguration.setTopBarButtonsActiveColor(ContextCompat.getColor(this, android.R.color.white))
        cameraConfiguration.setCameraBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        cameraConfiguration.setUserGuidanceBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
        cameraConfiguration.setUserGuidanceTextColor(ContextCompat.getColor(this, android.R.color.white))
        cameraConfiguration.setMultiPageEnabled(true)
        cameraConfiguration.setPageCounterButtonTitle("%d Page(s)")
        cameraConfiguration.setTextHintOK("Don't move.\nCapturing document...")
        //cameraConfiguration.set...

        val intent = io.scanbot.sdk.ui.view.camera.DocumentScannerActivity.newIntent(this, cameraConfiguration)
        startActivityForResult(intent, CAMERA_DEFAULT_UI_REQUEST_CODE)*/
        var intent=Intent(HomeActivity@this,DocScanUIMainActivity::class.java)
        startActivity(intent)
    }

    private fun openSignatureScreen() {
        val intent = Intent(this, SignaturePadActivity::class.java)
        startActivity(intent)
    }

    private fun openFaceCapture() {
        val intent = Intent(this, FaceActivity::class.java)
        startActivity(intent)
    }

    private fun startMrzActivity() {
        /*val mrzCameraConfiguration = MRZScannerConfiguration()

        mrzCameraConfiguration.setTopBarBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        mrzCameraConfiguration.setTopBarButtonsColor(ContextCompat.getColor(this, R.color.goldColor))
        mrzCameraConfiguration.setSuccessBeepEnabled(false)

        val intent = MRZScannerActivity.newIntent(this, mrzCameraConfiguration)
        startActivityForResult(intent, MRZ_DEFAULT_UI_REQUEST_CODE)*/

        var intent = Intent(this, ScanMrzActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MRZ_DEFAULT_UI_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            showMrzDialog(data!!.getParcelableExtra(MRZScannerActivity.EXTRACTED_FIELDS_EXTRA))
        } else if (requestCode == CAMERA_DEFAULT_UI_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            //            List<Page> pages = Arrays.asList(data.getParcelableArrayExtra(DocumentScannerActivity.SNAPPED_PAGE_EXTRA));
            val pages = data!!.getParcelableArrayExtra(DocumentScannerActivity.SNAPPED_PAGE_EXTRA).toList().map {
                it as Page
            }
            Log.d(TAG, "onActivityResult: caled ak check size by ak:" + pages.size)
            PageRepository.addPages(pages)


            val intent = Intent(this@HomeActivity, PagePreviewActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showMrzDialog(mrzRecognitionResult: MRZRecognitionResult) {
        val dialogFragment = MRZDialogFragment.newInstance(mrzRecognitionResult)
        dialogFragment.show(supportFragmentManager, MRZDialogFragment.NAME)
    }

    private fun openFingerScan() {
        val intent = Intent(this, MainFragmentActivity::class.java)
        startActivity(intent)
    }

    companion object {

        private val TAG = HomeActivity::class.java.simpleName
        private val MRZ_DEFAULT_UI_REQUEST_CODE = 909
        private val CAMERA_DEFAULT_UI_REQUEST_CODE = 1111

        init {
            Log.d(TAG, "runWSQDemo: called ak check 3")
            System.loadLibrary("WSQ")
            System.loadLibrary("NIST")
        }
    }
}

package com.vfs.fingerprint;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;

import com.veridiumid.sdk.VeridiumSDK;
import com.veridiumid.sdk.activities.DefaultVeridiumSDKModelFactory;
import com.veridiumid.sdk.defaultdata.VeridiumSDKDataInitializer;
import com.veridiumid.sdk.fourfexport.VeridiumSDKFourExportFInitializer;
import com.veridiumid.sdk.model.exception.SDKInitializationException;
import com.vfs.fingerprint.base.settingActivity;
import com.vfs.fingerprint.database.FingerModel;

import java.util.ArrayList;
import java.util.List;

import io.scanbot.sdk.ScanbotSDKInitializer;

public class AppClass extends Application {
    private static final String TAG = AppClass.class.getSimpleName();
    private FingerModel fingerModel;
    private static AppClass instance;
    private FingerModel fingerModelThumb;
    private String passportName;
    private String passportSurname;
    private String passportNumber;
    private String passportDate;
    private String binaryBase64ObjectBMP;
    private String thumb2;
    private String thumb1;
    private String finger8;
    private String finger7;
    private String finger6;
    private String finger5;
    private String finger4;
    private String finger3;
    private String finger2;
    private String finger1;
    private List<String> listOfFinger = new ArrayList<>();
    private List<String> listOfThumb = new ArrayList<>();

    private static final String LICENSE_KEY = "JmTUy1NXmGHwCf71CEBgpbCkvdZwTA" +
            "BeNiXfbgjGsDPL4JYBBkw/KXvTQNd7" +
            "KbAXqe2fUbZklJxHFmdOKQe4tqAlMw" +
            "TV6xFueY1KB2kL9+MrWfHlRGDT2msj" +
            "9bJka9lpHmX0TKgoWPJSQtl1rxkZ/K" +
            "AlMYHNsM/AOJGazx+qY+zhk65/FQM9" +
            "z5Uy9TKsrmqdoeKZ0zOGPkJvyIPFgD" +
            "VK2T1jvslbHqwQoxLNwZnJNFlCmHAQ" +
            "XzCqnh0Mxbc/SsusA1oqLKjUSZpf7L" +
            "81Y4EdaC+/B0n9nVSd+MCEDZtY//oy" +
            "M1wCDmTh2XPdqzqtm+0e+k0wmtwYs9" +
            "oUCTILDCFS+Q==\nU2NhbmJvdFNESw" +
            "ogY29tLnZmcy5maW5nZXJwcmludAox" +
            "NTgxNjM4Mzk5CjEwNzEwMgoz\n";
    private byte[] imageAsBytes;
    private String subPath;
    private Bitmap signatureBitmap;
    private Bitmap faceBmp;
    private String gender;
    private String dob;
    private String docType;
    private String dateOfExpiry;
    private String nationality;
    private String personalNumber;
    private String applicationNo;
    private String txnId;
    private String endDate;
    private String signaturePath;
    private String nistPath;
    private String documentPath;
    private String biometricStatus = "WIP";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = new AppClass();


        //scanbot
        new ScanbotSDKInitializer()
                .license(this, LICENSE_KEY)
                .prepareMRZBlobs(true)
                .initialize(this);

        try {
            // TODO Complete with your license key
            String fourfLicence = "hC5tXiqzRXf75NQXa868KV9gdl9rBuXUW5eI2IOrl4PZsfjulYfAkZG2nCqFBzl+Va3Ce5bvkl3qlNO6CLlxAHsiZGV2aWNlRmluZ2VycHJpbnQiOiJETklDSWpuNzh3WDZTaTdJdmdEOElsaWN4bzVsb0wrRCtibjhoZWc4TUFrPSIsImxpY2Vuc2UiOiJrMnkvWmp5MDRVbEx4dU51KzVFOUc1TGlQQjJDTVpBSFFDci90VnpoNVBoNGpGVkRqTWQrMW80TWFSdFIvZjhxK3ZMSUIwUlRlbzVaVzRBZ21IdkFBM3NpZEhsd1pTSTZJa0pKVDB4SlFsTWlMQ0p1WVcxbElqb2lORVlpTENKc1lYTjBUVzlrYVdacFpXUWlPakUxTnpnMk5UQTNNREl6T0RZc0ltTnZiWEJoYm5sT1lXMWxJam9pVmtaVElFZHNiMkpoYkNJc0ltTnZiblJoWTNSSmJtWnZJam9pSWl3aVkyOXVkR0ZqZEVWdFlXbHNJam9pYm1sMGFXNXphRUIyWm5ObmJHOWlZV3d1WTI5dElpd2ljM1ZpVEdsalpXNXphVzVuVUhWaWJHbGpTMlY1SWpvaU5tWnZlVWgwZW1GTVJEZEZlRFoyVjBJeFNrVnZjRlJNY0VFNFUzVndMMWc0T1ZaYU9VRnlVVmhPV1QwaUxDSnpkR0Z5ZEVSaGRHVWlPakUxTnpFNU5qRTJNREF3TURBc0ltVjRjR2x5WVhScGIyNUVZWFJsSWpveE5UZ3dOREk0T0RBd01EQXdMQ0puY21GalpVVnVaRVJoZEdVaU9qRTFPREV5T1RJNE1EQXdNREFzSW5WemFXNW5VMEZOVEZSdmEyVnVJanBtWVd4elpTd2lkWE5wYm1kR2NtVmxVa0ZFU1ZWVElqcG1ZV3h6WlN3aWRYTnBibWRCWTNScGRtVkVhWEpsWTNSdmNua2lPbVpoYkhObExDSmlhVzlzYVdKR1lXTmxSWGh3YjNKMFJXNWhZbXhsWkNJNlptRnNjMlVzSW5KMWJuUnBiV1ZGYm5acGNtOXViV1Z1ZENJNmV5SnpaWEoyWlhJaU9tWmhiSE5sTENKa1pYWnBZMlZVYVdWa0lqcG1ZV3h6Wlgwc0ltWmxZWFIxY21WeklqcDdJbUpoYzJVaU9uUnlkV1VzSW5OMFpYSmxiMHhwZG1WdVpYTnpJanAwY25WbExDSmxlSEJ2Y25RaU9uUnlkV1Y5TENKbGJtWnZjbU5sWkZCeVpXWmxjbVZ1WTJWeklqcDdJbTFoYm1SaGRHOXllVXhwZG1WdVpYTnpJanBtWVd4elpYMHNJblpsY25OcGIyNGlPaUkwTGlvdUtpSjkifQ==";
            VeridiumSDK.init(this,
                    new DefaultVeridiumSDKModelFactory(this),
                    new VeridiumSDKFourExportFInitializer(fourfLicence),
                    new VeridiumSDKDataInitializer()
            );
        } catch (SDKInitializationException e) {
            e.printStackTrace();
        }
    }

    public static AppClass getInstance() {
        return instance;
    }

    public void setFingerModel(FingerModel fingerModel) {
        Log.d(TAG, "setFingerModel: called ak set fm 1:" + fingerModel);
        this.fingerModel = fingerModel;
    }

    public FingerModel getFingerModel() {
        Log.d(TAG, "setFingerModel: called ak set fm 2:" + fingerModel);
        return fingerModel;
    }

    public void setThumbModel(FingerModel fingerModelThumb) {
        this.fingerModelThumb = fingerModelThumb;
    }

    public FingerModel getFingerModelThumb() {
        return fingerModelThumb;
    }

    public void setFingerModelThumb(FingerModel fingerModelThumb) {
        this.fingerModelThumb = fingerModelThumb;
    }

    public void setPassportName(String passportName) {
        Log.d("TAG", "called ak check passport name 3:" + passportName);
        this.passportName = passportName;
    }

    public void setPassportSurname(String passportSurname) {
        this.passportSurname = passportSurname;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public void setPassportDate(String passportDate) {
        this.passportDate = passportDate;
    }

    public String getPassportName() {
        return passportName;
    }

    public String getPassportSurname() {
        return passportSurname;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public String getPassportDate() {
        return passportDate;
    }

    public void setFinger1(String finger1) {
        this.finger1 = finger1;
        listOfFinger.add(finger1);
    }

    public void setFinger2(String finger2) {
        this.finger2 = finger2;
        listOfFinger.add(finger2);
    }

    public void setFinger3(String finger3) {
        this.finger3 = finger3;
        listOfFinger.add(finger3);
    }

    public void setFinger4(String finger4) {
        this.finger4 = finger4;
        listOfFinger.add(finger4);
    }

    public void setFinger5(String finger5) {
        this.finger5 = finger5;
        listOfFinger.add(finger5);
    }

    public void setFinger6(String finger6) {
        this.finger6 = finger6;
        listOfFinger.add(finger6);
    }

    public void setFinger7(String finger7) {
        this.finger7 = finger7;
        listOfFinger.add(finger7);
    }

    public void setFinger8(String finger8) {
        this.finger8 = finger8;
        listOfFinger.add(finger8);
    }

    public void setThumb1(String thumb1) {
        this.thumb1 = thumb1;
        listOfThumb.add(thumb1);
    }

    public void setThumb2(String thumb2) {
        this.thumb2 = thumb2;
        listOfThumb.add(thumb2);
    }

    public String getThumb2() {
        return thumb2;
    }

    public String getThumb1() {
        return thumb1;
    }

    public String getFinger8() {
        return finger8;
    }

    public String getFinger7() {
        return finger7;
    }

    public String getFinger6() {
        return finger6;
    }

    public String getFinger5() {
        return finger5;
    }

    public String getFinger4() {
        return finger4;
    }

    public String getFinger3() {
        return finger3;
    }

    public String getFinger2() {
        return finger2;
    }

    public String getFinger1() {
        return finger1;
    }

    public List<String> getListOfFinger() {
        return listOfFinger;
    }

    public List<String> getListOfThumb() {
        return listOfThumb;
    }

    public void setByteArray(byte[] imageAsBytes) {
        this.imageAsBytes = imageAsBytes;
    }

    public byte[] getImageAsBytes() {
        return imageAsBytes;
    }

    public void setPath(String subPath) {
        this.subPath = subPath;
    }

    public String getSubPath() {
        return subPath;
    }

    public void setSignatureBmp(Bitmap signatureBitmap) {
        this.signatureBitmap = signatureBitmap;
    }

    public Bitmap getSignatureBitmap() {
        return signatureBitmap;
    }

    public void saveFaceImage(Bitmap faceBmp) {
        this.faceBmp = faceBmp;
    }

    public Bitmap getFaceBmp() {
        return faceBmp;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setDOB(String dob) {
        this.dob = dob;
    }

    public String getDob() {
        return dob;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocType() {
        return docType;
    }


    public void setDateOfExpiry(String dateOfExpiry) {
        this.dateOfExpiry = dateOfExpiry;
    }

    public String getDateOfExpiry() {
        return dateOfExpiry;
    }

    public void setnationality(String nationality) {
        this.nationality = nationality;
    }

    public String getNationality() {
        return nationality;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setApplicationNo(String applicationNo) {
        this.applicationNo = applicationNo;
    }


    public String getApplicationNo() {
        return applicationNo;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setSignaturePath(String signaturePath) {
        this.signaturePath = signaturePath;
    }

    public String getSignaturePath() {
        return signaturePath;
    }

    public void setNistPath(String nistPath) {
        this.nistPath = nistPath;
    }

    public String getNistPath() {
        return nistPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public String getBiometricStatus() {
        return biometricStatus;
    }

    public void setBiometricStatus(String biometricStatus) {
        this.biometricStatus = biometricStatus;
    }
}

package com.vfs.fingerprint.database

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Fingerprint_ {
    @SerializedName("CaptureDate")
    @Expose
    private var captureDate: CaptureDate_? = null
    @SerializedName("FingerImpressionImage")
    @Expose
    private var fingerImpressionImage: FingerImpressionImage_? = null
    @SerializedName("FingerPositionCode")
    @Expose
    private var fingerPositionCode: Int? = null
    @SerializedName("FingerprintImageFingerMissing")
    @Expose
    private var fingerprintImageFingerMissing: Any? = null
    @SerializedName("NFIQ")
    @Expose
    private var nFIQ: Int? = null

    fun getCaptureDate(): CaptureDate_? {
        return captureDate
    }

    fun setCaptureDate(captureDate: CaptureDate_) {
        this.captureDate = captureDate
    }

    fun getFingerImpressionImage(): FingerImpressionImage_? {
        return fingerImpressionImage
    }

    fun setFingerImpressionImage(fingerImpressionImage: FingerImpressionImage_) {
        this.fingerImpressionImage = fingerImpressionImage
    }

    fun getFingerPositionCode(): Int? {
        return fingerPositionCode
    }

    fun setFingerPositionCode(fingerPositionCode: Int?) {
        this.fingerPositionCode = fingerPositionCode
    }

    fun getFingerprintImageFingerMissing(): Any? {
        return fingerprintImageFingerMissing
    }

    fun setFingerprintImageFingerMissing(fingerprintImageFingerMissing: Any) {
        this.fingerprintImageFingerMissing = fingerprintImageFingerMissing
    }

    fun getNFIQ(): Int? {
        return nFIQ
    }

    fun setNFIQ(nFIQ: Int?) {
        this.nFIQ = nFIQ
    }
}
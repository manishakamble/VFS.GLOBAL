package com.vfs.fingerprint.database

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Fingerprint {
    @SerializedName("CaptureDate")
    @Expose
    private var captureDate: CaptureDate? = null
    @SerializedName("FingerImpressionImage")
    @Expose
    private var fingerImpressionImage: FingerImpressionImage? = null
    @SerializedName("FingerPositionCode")
    @Expose
    private var fingerPositionCode: Int? = null
    @SerializedName("FingerprintImageFingerMissing")
    @Expose
    private var fingerprintImageFingerMissing: Any? = null

    fun getCaptureDate(): CaptureDate? {
        return captureDate
    }

    fun setCaptureDate(captureDate: CaptureDate) {
        this.captureDate = captureDate
    }

    fun getFingerImpressionImage(): FingerImpressionImage? {
        return fingerImpressionImage
    }

    fun setFingerImpressionImage(fingerImpressionImage: FingerImpressionImage) {
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
}
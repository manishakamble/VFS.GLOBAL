package com.vfs.fingerprint.database

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Fingerprint__ {
    @SerializedName("CaptureDate")
    @Expose
    private var captureDate: CaptureDate__? = null
    @SerializedName("FingerImpressionImage")
    @Expose
    private var fingerImpressionImage: FingerImpressionImage__? = null
    @SerializedName("FingerPositionCode")
    @Expose
    private var fingerPositionCode: Int? = null
    @SerializedName("FingerprintImageFingerMissing")
    @Expose
    private var fingerprintImageFingerMissing: Any? = null

    fun getCaptureDate(): CaptureDate__? {
        return captureDate
    }

    fun setCaptureDate(captureDate: CaptureDate__) {
        this.captureDate = captureDate
    }

    fun getFingerImpressionImage(): FingerImpressionImage__? {
        return fingerImpressionImage
    }

    fun setFingerImpressionImage(fingerImpressionImage: FingerImpressionImage__) {
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
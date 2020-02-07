package com.vfs.fingerprint.database

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class SCALE100 {
    @SerializedName("AuditImage_Left")
    @Expose
    private var auditImageLeft: AuditImageLeft? = null
    @SerializedName("AuditImage_Right")
    @Expose
    private var auditImageRight: AuditImageRight? = null
    @SerializedName("Fingerprints")
    @Expose
    private var fingerprints: List<Fingerprint_>? = null
    @SerializedName("Status")
    @Expose
    private var status: Int? = null

    fun getAuditImageLeft(): AuditImageLeft? {
        return auditImageLeft
    }

    fun setAuditImageLeft(auditImageLeft: AuditImageLeft) {
        this.auditImageLeft = auditImageLeft
    }

    fun getAuditImageRight(): AuditImageRight? {
        return auditImageRight
    }

    fun setAuditImageRight(auditImageRight: AuditImageRight) {
        this.auditImageRight = auditImageRight
    }

    fun getFingerprints(): List<Fingerprint_>? {
        return fingerprints
    }

    fun setFingerprints(fingerprints: List<Fingerprint_>) {
        this.fingerprints = fingerprints
    }

    fun getStatus(): Int? {
        return status
    }

    fun setStatus(status: Int?) {
        this.status = status
    }
}
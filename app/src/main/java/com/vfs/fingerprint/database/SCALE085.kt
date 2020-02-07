package com.vfs.fingerprint.database

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class SCALE085 {
    @SerializedName("Fingerprints")
    @Expose
    private var fingerprints: List<Fingerprint>? = null
    @SerializedName("Status")
    @Expose
    private var status: Int? = null

    fun getFingerprints(): List<Fingerprint>? {
        return fingerprints
    }

    fun setFingerprints(fingerprints: List<Fingerprint>) {
        this.fingerprints = fingerprints
    }

    fun getStatus(): Int? {
        return status
    }

    fun setStatus(status: Int?) {
        this.status = status
    }
}
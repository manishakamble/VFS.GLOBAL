package com.vfs.fingerprint.database

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class SCALE115 {
    @SerializedName("Fingerprints")
    @Expose
    private var fingerprints: List<Fingerprint__>? = null
    @SerializedName("Status")
    @Expose
    private var status: Int? = null

    fun getFingerprints(): List<Fingerprint__>? {
        return fingerprints
    }

    fun setFingerprints(fingerprints: List<Fingerprint__>) {
        this.fingerprints = fingerprints
    }

    fun getStatus(): Int? {
        return status
    }

    fun setStatus(status: Int?) {
        this.status = status
    }

}
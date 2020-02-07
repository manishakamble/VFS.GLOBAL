package com.vfs.fingerprint.database

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class FingerModel {
    @SerializedName("SCALE085")
    @Expose
    private var sCALE085: SCALE085? = null
    @SerializedName("SCALE100")
    @Expose
    private var sCALE100: SCALE100? = null
    @SerializedName("SCALE115")
    @Expose
    private var sCALE115: SCALE115? = null

    fun getSCALE085(): SCALE085? {
        return sCALE085
    }

    fun setSCALE085(sCALE085: SCALE085) {
        this.sCALE085 = sCALE085
    }

    fun getSCALE100(): SCALE100? {
        return sCALE100
    }

    fun setSCALE100(sCALE100: SCALE100) {
        this.sCALE100 = sCALE100
    }

    fun getSCALE115(): SCALE115? {
        return sCALE115
    }

    fun setSCALE115(sCALE115: SCALE115) {
        this.sCALE115 = sCALE115
    }
}
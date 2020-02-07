package com.vfs.fingerprint.database

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class CaptureDate {
    @SerializedName("DateTime")
    @Expose
    private var dateTime: String? = null

    fun getDateTime(): String? {
        return dateTime
    }

    fun setDateTime(dateTime: String) {
        this.dateTime = dateTime
    }
}
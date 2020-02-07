package com.vfs.fingerprint.database

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class AuditImageLeft {
    @SerializedName("BinaryBase64ObjectJPG")
    @Expose
    private var binaryBase64ObjectJPG: String? = null
    @SerializedName("ImageHashValueJPG")
    @Expose
    private var imageHashValueJPG: String? = null

    fun getBinaryBase64ObjectJPG(): String? {
        return binaryBase64ObjectJPG
    }

    fun setBinaryBase64ObjectJPG(binaryBase64ObjectJPG: String) {
        this.binaryBase64ObjectJPG = binaryBase64ObjectJPG
    }

    fun getImageHashValueJPG(): String? {
        return imageHashValueJPG
    }

    fun setImageHashValueJPG(imageHashValueJPG: String) {
        this.imageHashValueJPG = imageHashValueJPG
    }
}
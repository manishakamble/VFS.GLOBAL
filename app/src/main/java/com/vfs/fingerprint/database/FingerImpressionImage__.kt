package com.vfs.fingerprint.database

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class FingerImpressionImage__ {
    @SerializedName("BinaryBase64ObjectBMP")
    @Expose
    private var binaryBase64ObjectBMP: String? = null
    @SerializedName("BinaryBase64ObjectPNG")
    @Expose
    private var binaryBase64ObjectPNG: String? = null
    @SerializedName("BinaryBase64ObjectRAW")
    @Expose
    private var binaryBase64ObjectRAW: String? = null
    @SerializedName("BinaryBase64ObjectWSQ")
    @Expose
    private var binaryBase64ObjectWSQ: String? = null
    @SerializedName("Height")
    @Expose
    private var height: Int? = null
    @SerializedName("ImageBitsPerPixelQuantity")
    @Expose
    private var imageBitsPerPixelQuantity: Int? = null
    @SerializedName("ImageHashValueBMP")
    @Expose
    private var imageHashValueBMP: String? = null
    @SerializedName("ImageHashValuePNG")
    @Expose
    private var imageHashValuePNG: String? = null
    @SerializedName("ImageHashValueRAW")
    @Expose
    private var imageHashValueRAW: String? = null
    @SerializedName("ImageHashValueWSQ")
    @Expose
    private var imageHashValueWSQ: String? = null
    @SerializedName("Resolution")
    @Expose
    private var resolution: Int? = null
    @SerializedName("Width")
    @Expose
    private var width: Int? = null

    fun getBinaryBase64ObjectBMP(): String? {
        return binaryBase64ObjectBMP
    }

    fun setBinaryBase64ObjectBMP(binaryBase64ObjectBMP: String) {
        this.binaryBase64ObjectBMP = binaryBase64ObjectBMP
    }

    fun getBinaryBase64ObjectPNG(): String? {
        return binaryBase64ObjectPNG
    }

    fun setBinaryBase64ObjectPNG(binaryBase64ObjectPNG: String) {
        this.binaryBase64ObjectPNG = binaryBase64ObjectPNG
    }

    fun getBinaryBase64ObjectRAW(): String? {
        return binaryBase64ObjectRAW
    }

    fun setBinaryBase64ObjectRAW(binaryBase64ObjectRAW: String) {
        this.binaryBase64ObjectRAW = binaryBase64ObjectRAW
    }

    fun getBinaryBase64ObjectWSQ(): String? {
        return binaryBase64ObjectWSQ
    }

    fun setBinaryBase64ObjectWSQ(binaryBase64ObjectWSQ: String) {
        this.binaryBase64ObjectWSQ = binaryBase64ObjectWSQ
    }

    fun getHeight(): Int? {
        return height
    }

    fun setHeight(height: Int?) {
        this.height = height
    }

    fun getImageBitsPerPixelQuantity(): Int? {
        return imageBitsPerPixelQuantity
    }

    fun setImageBitsPerPixelQuantity(imageBitsPerPixelQuantity: Int?) {
        this.imageBitsPerPixelQuantity = imageBitsPerPixelQuantity
    }

    fun getImageHashValueBMP(): String? {
        return imageHashValueBMP
    }

    fun setImageHashValueBMP(imageHashValueBMP: String) {
        this.imageHashValueBMP = imageHashValueBMP
    }

    fun getImageHashValuePNG(): String? {
        return imageHashValuePNG
    }

    fun setImageHashValuePNG(imageHashValuePNG: String) {
        this.imageHashValuePNG = imageHashValuePNG
    }

    fun getImageHashValueRAW(): String? {
        return imageHashValueRAW
    }

    fun setImageHashValueRAW(imageHashValueRAW: String) {
        this.imageHashValueRAW = imageHashValueRAW
    }

    fun getImageHashValueWSQ(): String? {
        return imageHashValueWSQ
    }

    fun setImageHashValueWSQ(imageHashValueWSQ: String) {
        this.imageHashValueWSQ = imageHashValueWSQ
    }

    fun getResolution(): Int? {
        return resolution
    }

    fun setResolution(resolution: Int?) {
        this.resolution = resolution
    }

    fun getWidth(): Int? {
        return width
    }

    fun setWidth(width: Int?) {
        this.width = width
    }
}
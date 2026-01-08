package com.umutsibara.patitakip.data.model

import com.google.gson.annotations.SerializedName

data class PhotoUploadResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: PhotoData?
)

data class PhotoData(
    @SerializedName("foto_id") val photoId: Int,
    @SerializedName("url") val url: String
)

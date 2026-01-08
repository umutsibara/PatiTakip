package com.umutsibara.patitakip.data.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("error") val error: String?,
    @SerializedName("message") val message: String?
)

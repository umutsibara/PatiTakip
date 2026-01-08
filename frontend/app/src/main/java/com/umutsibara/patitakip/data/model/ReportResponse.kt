package com.umutsibara.patitakip.data.model

import com.google.gson.annotations.SerializedName

// Backend response formatı
data class ReportResponse(
    @SerializedName("success") val success: Boolean = true,
    @SerializedName("message") val message: String? = null,
    @SerializedName("count") val count: Int? = null,
    @SerializedName("data") val data: List<Report> = emptyList()
)

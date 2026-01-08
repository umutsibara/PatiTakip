package com.umutsibara.patitakip.data.model

import com.google.gson.annotations.SerializedName

data class AnimalTypesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<AnimalType>?
)

data class AnimalType(
    @SerializedName("id") val id: Int,
    @SerializedName("tur_adi") val name: String
)

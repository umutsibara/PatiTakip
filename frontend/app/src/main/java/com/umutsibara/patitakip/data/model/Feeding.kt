package com.umutsibara.patitakip.data.model

import com.google.gson.annotations.SerializedName

data class Feeding(
    @SerializedName("besleme_id") val id: Int,
    @SerializedName("kullanici_id") val userId: Int?,
    @SerializedName("bolge_id") val zoneId: Int?,
    @SerializedName("mama_miktari_kg") val amountKg: Double,
    @SerializedName("mama_turu") val foodType: String?,
    @SerializedName("besleme_zamani") val feedingTime: String,
    @SerializedName("kullanici_adi") val userName: String?,
    @SerializedName("avatar_url") val userAvatarUrl: String?,
    @SerializedName("bolge_adi") val zoneName: String?
)

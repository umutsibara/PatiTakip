package com.umutsibara.patitakip.data.model

import com.google.gson.annotations.SerializedName

// Backend'e POST request için Türkçe field isimleri
data class CreateReportRequest(
    @SerializedName("kullanici_id") val userId: Int,
    @SerializedName("baslik") val title: String? = null,
    @SerializedName("aciklama") val description: String,
    @SerializedName("ihbar_turu") val reportType: String? = "Genel",
    @SerializedName("kategori") val category: String? = "REPORT",
    @SerializedName("hayvan_turu") val animalType: String? = "Kedi",
    @SerializedName("enlem") val latitude: Double,
    @SerializedName("boylam") val longitude: Double,
    @SerializedName("adres") val address: String? = null,
    @SerializedName("bolge_id") val zoneId: Int? = null,
    @SerializedName("hayvan_id") val animalId: Int? = null,
    @SerializedName("foto_id") val photoId: Int? = null
)

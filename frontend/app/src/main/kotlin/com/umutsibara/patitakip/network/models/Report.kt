package com.umutsibara.patitakip.network.models

import com.google.gson.annotations.SerializedName

// Backend location object
data class ReportLocation(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("address") val address: String?
)

// Backend user object
data class ReportUser(
    @SerializedName("id") val id: Int,
    @SerializedName("userName") val userName: String,
    @SerializedName("avatarUrl") val avatarUrl: String?,
    @SerializedName("rank") val rank: Int,
    @SerializedName("points") val points: Int
)

// Backend stats object
data class ReportStats(
    @SerializedName("likes") val likes: Int = 0,
    @SerializedName("comments") val comments: Int = 0,
    @SerializedName("shares") val shares: Int = 0
)

data class Report(
    // Backend uses camelCase format
    @SerializedName("id") val ihbarId: Int,
    @SerializedName("title") val baslik: String?,
    @SerializedName("description") val aciklama: String,
    @SerializedName("category") val kategori: String,
    @SerializedName("type") val ihbarTuru: String?,
    @SerializedName("animalType") val hayvanTuru: String?,
    @SerializedName("location") val location: ReportLocation,
    @SerializedName("user") val user: ReportUser,
    @SerializedName("stats") val stats: ReportStats,
    @SerializedName("imageUrls") val imageUrls: List<String>?,
    @SerializedName("createdAt") val olusturmaTarihi: String
) {
    // Convenience properties for adapter compatibility
    val enlem: Double get() = location.latitude
    val boylam: Double get() = location.longitude
    val adres: String? get() = location.address
    val begeniSayisi: Int get() = stats.likes
    val yorumSayisi: Int get() = stats.comments
    val paylasimSayisi: Int get() = stats.shares
    val kullaniciAdi: String get() = user.userName
    val avatarUrl: String? get() = user.avatarUrl
    val rutbe: Int get() = user.rank
    val fotografUrl: String? get() = imageUrls?.firstOrNull()
}

data class CreateReportRequest(
    @SerializedName("kullanici_id") val kullaniciId: Int,
    @SerializedName("baslik") val baslik: String?,
    @SerializedName("aciklama") val aciklama: String,
    @SerializedName("kategori") val kategori: String,
    @SerializedName("ihbar_turu") val ihbarTuru: String?,
    @SerializedName("hayvan_turu") val hayvanTuru: String?,
    @SerializedName("enlem") val enlem: Double,
    @SerializedName("boylam") val boylam: Double,
    @SerializedName("adres") val adres: String?,
    @SerializedName("foto_id") val fotoId: Int?
)

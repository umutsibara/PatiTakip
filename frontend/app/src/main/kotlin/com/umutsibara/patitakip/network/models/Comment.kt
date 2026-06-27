package com.umutsibara.patitakip.network.models

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("yorum_id") val yorumId: Int,
    @SerializedName("ihbar_id") val ihbarId: Int,
    @SerializedName("kullanici_id") val kullaniciId: Int,
    @SerializedName("yorum_metni") val yorumMetni: String,
    @SerializedName("begeni_sayisi") val begeniSayisi: Int = 0,
    @SerializedName("olusturulma_tarihi") val olusturmaTarihi: String,
    @SerializedName("ust_yorum_id") val ustYorumId: Int?,
    @SerializedName("cevap_sayisi") val cevapSayisi: Int = 0,
    
    // Kullanıcı bilgileri
    @SerializedName("kullanici_adi") val kullaniciAdi: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("rutbe") val rutbe: Int?
)

data class CreateCommentRequest(
    @SerializedName("kullanici_id") val kullaniciId: Int,
    @SerializedName("yorum_metni") val yorumMetni: String,
    @SerializedName("ust_yorum_id") val ustYorumId: Int? = null
)

data class LikeToggleRequest(
    @SerializedName("kullanici_id") val kullaniciId: Int,
    @SerializedName("hedef_turu") val hedefTuru: String, // ihbar, yorum, hayvan
    @SerializedName("ihbar_id") val ihbarId: Int?,
    @SerializedName("yorum_id") val yorumId: Int?,
    @SerializedName("hayvan_id") val hayvanId: Int?
)

data class LikeResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("action") val action: String, // liked or unliked
    @SerializedName("message") val message: String,
    @SerializedName("begeni_sayisi") val begeniSayisi: Int
)

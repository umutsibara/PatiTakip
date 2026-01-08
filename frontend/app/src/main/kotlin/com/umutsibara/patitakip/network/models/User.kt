package com.umutsibara.patitakip.network.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("kullanici_id") val kullaniciId: Int? = null,
    @SerializedName("id") val id: Int? = null,  // Backend "id" kullanıyor
    @SerializedName("kullanici_adi") val kullaniciAdi: String? = null,
    @SerializedName("isim") val isim: String? = null,  // Backend "isim" kullanıyor
    @SerializedName("eposta") val eposta: String?,
    @SerializedName("tam_isim") val tamIsim: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("puan") val puan: Int = 0,
    @SerializedName("rutbe") val rutbe: Int = 0,
    @SerializedName("toplam_ihbar_sayisi") val toplamIhbarSayisi: Int = 0,
    @SerializedName("toplam_besleme_sayisi") val toplamBeslemeSayisi: Int = 0,
    @SerializedName("toplam_yorum_sayisi") val toplamYorumSayisi: Int = 0,
    @SerializedName("olusturulma_tarihi") val olusturmaTarihi: String?,
    @SerializedName("token") val token: String? = null  // Backend login'de token da data içinde
) {
    // Helper: ID almak için (backend'e göre)
    fun getUserId(): Int = kullaniciId ?: id ?: -1
    fun getUsername(): String = kullaniciAdi ?: isim ?: "Anonim"
}

data class LoginRequest(
    @SerializedName("eposta") val eposta: String,
    @SerializedName("sifre") val sifre: String
)

data class RegisterRequest(
    @SerializedName("kullanici_adi") val kullaniciAdi: String,
    @SerializedName("eposta") val eposta: String,
    @SerializedName("sifre") val sifre: String,
    @SerializedName("tam_isim") val tamIsim: String?
)

data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: User?  // Backend data objesi içinde user + token döndürüyor
)

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: T?,
    @SerializedName("error") val error: String?
)

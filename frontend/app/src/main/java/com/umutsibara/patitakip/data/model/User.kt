package com.umutsibara.patitakip.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
        @SerializedName("eposta") val email: String,
        @SerializedName("sifre") val password: String
)

data class RegisterRequest(
        @SerializedName("kullanici_adi") val username: String,
        @SerializedName("eposta") val email: String,
        @SerializedName("sifre") val password: String
)

data class AuthResponse(
        @SerializedName("success") val success: Boolean,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: AuthData?
)

data class AuthData(
        @SerializedName("token") val token: String?,
        @SerializedName("id") val id: Int,
        @SerializedName("isim") val username: String,
        @SerializedName("eposta") val email: String,
        @SerializedName("rol") val role: String?,
        @SerializedName("puan") val score: Int? = 0,
        @SerializedName("avatar_url") val avatarUrl: String? = null,
        @SerializedName("rutbe") val rank: Int? = 0
)

data class User(
        @SerializedName("id") val id: Int,
        @SerializedName("kullanici_adi") val username: String,
        @SerializedName("eposta") val email: String,
        @SerializedName("rol") val role: String? = "gonullu",
        @SerializedName("puan") val score: Int? = 0,
        @SerializedName("rozetler") val badges: List<String>? = null
)

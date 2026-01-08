package com.umutsibara.patitakip.data.model

import com.google.gson.annotations.SerializedName

// Backend dönüş formatına göre düzeltildi
data class Report(
        @SerializedName("id") val id: Int? = null,
        @SerializedName("title") val title: String? = null,
        @SerializedName("description") val description: String? = null,
        @SerializedName("category") val category: String? = null,
        @SerializedName("type") val reportType: String? = null,
        @SerializedName("animalType") val animalType: String? = null,
        @SerializedName("location") val location: ReportLocation? = null,
        @SerializedName("user") val user: ReportUser? = null,
        @SerializedName("stats") val stats: ReportStats? = null,
        @SerializedName("imageUrls") val imageUrls: List<String>? = null,
        @SerializedName("createdAt") val createdAt: String? = null
) {
    // Helper properties for backward compatibility
    val photoUrl: String?
        get() = imageUrls?.firstOrNull()
    
    val latitude: Double?
        get() = location?.latitude
    
    val longitude: Double?
        get() = location?.longitude
        
    val creatorName: String?
        get() = user?.userName
        
    val userId: Int?
        get() = user?.id
}

data class ReportLocation(
        @SerializedName("latitude") val latitude: Double,
        @SerializedName("longitude") val longitude: Double,
        @SerializedName("address") val address: String? = null
)

data class ReportUser(
        @SerializedName("id") val id: Int,
        @SerializedName("userName") val userName: String,
        @SerializedName("avatarUrl") val avatarUrl: String? = null,
        @SerializedName("rank") val rank: Int? = null,
        @SerializedName("points") val points: Int? = null
)

data class ReportStats(
        @SerializedName("likes") val likes: Int = 0,
        @SerializedName("comments") val comments: Int = 0,
        @SerializedName("shares") val shares: Int = 0
)

data class Location(
        @SerializedName("x") val latitude: Double,
        @SerializedName("y") val longitude: Double
)

package com.umutsibara.patitakip.network.models

import com.google.gson.annotations.SerializedName

data class Feeding(
    @SerializedName("besleme_id")
    val id: Int,
    
    @SerializedName("kullanici_id")
    val userId: Int,
    
    @SerializedName("kullanici_adi")
    val username: String?,
    
    @SerializedName("profil_resmi")
    val profileImage: String?,
    
    @SerializedName("bolge_id")
    val regionId: Int?,
    
    @SerializedName("bolge_adi")
    val regionName: String?,
    
    @SerializedName("mama_miktari_kg")
    val foodAmount: Double?,
    
    @SerializedName("besleme_zamani")
    val timestamp: String,
    
    @SerializedName("fotograf_url")
    val photoUrl: String?,
    
    @SerializedName("aciklama")
    val description: String?,
    
    @SerializedName("hayvan_turu")
    val animalType: String?,
    
    @SerializedName("begeni_sayisi")
    val likeCount: Int = 0,
    
    @SerializedName("yorum_sayisi")
    val commentCount: Int = 0,
    
    @SerializedName("begenildi_mi")
    val isLiked: Boolean = false,
    
    @SerializedName("etiketler")
    val tags: List<String>? = null
)

data class CreateFeedingRequest(
    @SerializedName("kullanici_id")
    val userId: Int,
    
    @SerializedName("bolge_id")
    val regionId: Int?,
    
    @SerializedName("miktar")
    val amount: Double,
    
    @SerializedName("aciklama")
    val description: String?,
    
    @SerializedName("fotograf_url")
    val photoUrl: String?,
    
    @SerializedName("hayvan_turu")
    val animalType: String?
)

package com.umutsibara.patitakip.network.models

import com.google.gson.annotations.SerializedName

data class Chat(
    @SerializedName("sohbet_id") val sohbetId: Int,
    @SerializedName("sohbet_turu") val sohbetTuru: String = "bireysel",
    @SerializedName("goruntuleme_adi") val goruntulemeAdi: String?,
    @SerializedName("goruntuleme_resmi") val goruntulemeResmi: String?,
    @SerializedName("son_mesaj") val sonMesaj: String?,
    @SerializedName("son_mesaj_tarihi") val sonMesajTarihi: String?,
    @SerializedName("okunmamis_mesaj_sayisi") val okunmamisMesajSayisi: Int = 0,
    @SerializedName("diger_kullanici_id") val digerKullaniciId: Int?,
    @SerializedName("olusturulma_tarihi") val olusturmaTarihi: String
)

data class Message(
    @SerializedName("mesaj_id") val mesajId: Int,
    @SerializedName("sohbet_id") val sohbetId: Int,
    @SerializedName("gonderen_id") val gonderenId: Int,
    @SerializedName("mesaj_metni") val mesajMetni: String?,
    @SerializedName("mesaj_turu") val mesajTuru: String = "metin",
    @SerializedName("dosya_url") val dosyaUrl: String?,
    @SerializedName("gonderildi") val gonderildi: String,
    @SerializedName("okundu") val okundu: String?,
    @SerializedName("cevaplanan_mesaj_id") val cevaplananMesajId: Int?,
    
    // Gönderen bilgileri
    @SerializedName("kullanici_adi") val kullaniciAdi: String?,
    @SerializedName("avatar_url") val avatarUrl: String?
)

data class CreateChatRequest(
    @SerializedName("kullanici_id_1") val kullaniciId1: Int,
    @SerializedName("kullanici_id_2") val kullaniciId2: Int
)

data class SendMessageRequest(
    @SerializedName("gonderen_id") val gonderenId: Int,
    @SerializedName("mesaj_metni") val mesajMetni: String?,
    @SerializedName("mesaj_turu") val mesajTuru: String = "metin",
    @SerializedName("dosya_url") val dosyaUrl: String? = null
)

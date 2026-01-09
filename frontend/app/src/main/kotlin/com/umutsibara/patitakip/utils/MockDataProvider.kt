package com.umutsibara.patitakip.utils

import com.google.android.gms.maps.model.LatLng
import com.umutsibara.patitakip.network.models.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Centralized mock data provider for demonstration purposes
 */
object MockDataProvider {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    
    // Istanbul area coordinates for realistic location data
    private val istanbulCenter = LatLng(41.0082, 28.9784)
    
    /**
     * Generate mock feeding posts
     */
    fun getMockFeedings(): List<Feeding> {
        val feedings = mutableListOf<Feeding>()
        val currentTime = System.currentTimeMillis()
        
        feedings.add(
            Feeding(
                id = 1,
                userId = 101,
                username = "Ayşe Yılmaz",
                profileImage = null,
                regionId = 1,
                regionName = "Kadıköy - Moda",
                foodAmount = 2.5,
                timestamp = formatDate(currentTime - 2 * 60 * 60 * 1000), // 2 hours ago
                photoUrl = "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=800&q=80",
                description = "Parkta 5 köpeğe mama bıraktım. Hepsi sağlıklı görünüyor! 🐕",
                animalType = "Köpek",
                likeCount = 24,
                commentCount = 5,
                isLiked = false,
                tags = listOf("sokak_hayvanlari", "mama")
            )
        )
        
        feedings.add(
            Feeding(
                id = 2,
                userId = 102,
                username = "Mehmet Demir",
                profileImage = null,
                regionId = 2,
                regionName = "Beşiktaş - Ortaköy",
                foodAmount = 1.5,
                timestamp = formatDate(currentTime - 5 * 60 * 60 * 1000), // 5 hours ago
                photoUrl = "https://images.unsplash.com/photo-1574158622682-e40e69881006?w=800&q=80",
                description = "Sahildeki kedilere mama verdim. 3 kedi geldi 🐱",
                animalType = "Kedi",
                likeCount = 18,
                commentCount = 3,
                isLiked = true,
                tags = listOf("kedi", "sahil")
            )
        )
        
        feedings.add(
            Feeding(
                id = 3,
                userId = 103,
                username = "Zeynep Kaya",
                profileImage = null,
                regionId = 3,
                regionName = "Şişli - Mecidiyeköy",
                foodAmount = 3.0,
                timestamp = formatDate(currentTime - 8 * 60 * 60 * 1000), // 8 hours ago
                photoUrl = "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=800&q=80",
                description = "İş yerinin yanında düzenli beslediğim hayvanlar için bugünkü mama bıraktım",
                animalType = "Köpek",
                likeCount = 32,
                commentCount = 7,
                isLiked = false,
                tags = listOf("rutin_besleme")
            )
        )
        
        feedings.add(
            Feeding(
                id = 4,
                userId = 104,
                username = "Can Öztürk",
                profileImage = null,
                regionId = 4,
                regionName = "Beyoğlu - Taksim",
                foodAmount = 2.0,
                timestamp = formatDate(currentTime - 12 * 60 * 60 * 1000), // 12 hours ago
                photoUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=800&q=80",
                description = "Meydanda aç görünen kedilere yardım ettim",
                animalType = "Kedi",
                likeCount = 15,
                commentCount = 2,
                isLiked = false,
                tags = listOf("kedi", "acil")
            )
        )
        
        feedings.add(
            Feeding(
                id = 5,
                userId = 105,
                username = "Elif Yıldız",
                profileImage = null,
                regionId = 5,
                regionName = "Üsküdar - Çengelköy",
                foodAmount = 4.0,
                timestamp = formatDate(currentTime - 24 * 60 * 60 * 1000), // 1 day ago
                photoUrl = "https://images.unsplash.com/photo-1568572933382-74d440642117?w=800&q=80",
                description = "Büyük bir köpek sürüsü için mama bıraktım. Kış aylarında daha çok ihtiyaçları var ❄️",
                animalType = "Köpek",
                likeCount = 45,
                commentCount = 12,
                isLiked = true,
                tags = listOf("kis", "surü")
            )
        )
        
        feedings.add(
            Feeding(
                id = 6,
                userId = 106,
                username = "Ahmet Şahin",
                profileImage = null,
                regionId = 6,
                regionName = "Sarıyer - Tarabya",
                foodAmount = 1.0,
                timestamp = formatDate(currentTime - 36 * 60 * 60 * 1000), // 1.5 days ago
                photoUrl = "https://images.unsplash.com/photo-1573865526739-10c1d3b1f196?w=800&q=80",
                description = "Sahil yolundaki kedilere mama",
                animalType = "Kedi",
                likeCount = 8,
                commentCount = 1,
                isLiked = false,
                tags = listOf("sahil", "kedi")
            )
        )
        
        feedings.add(
            Feeding(
                id = 7,
                userId = 107,
                username = "Selin Arslan",
                profileImage = null,
                regionId = 7,
                regionName = "Bakırköy - Ataköy",
                foodAmount = 2.5,
                timestamp = formatDate(currentTime - 48 * 60 * 60 * 1000), // 2 days ago
                photoUrl = "https://images.unsplash.com/photo-1561037404-61cd46aa615b?w=800&q=80",
                description = "Parkta yaşayan hayvanlar için günlük besleme",
                animalType = "Köpek",
                likeCount = 21,
                commentCount = 4,
                isLiked = false,
                tags = listOf("park", "rutin")
            )
        )
        
        feedings.add(
            Feeding(
                id = 8,
                userId = 108,
                username = "Burak Aydın",
                profileImage = null,
                regionId = 8,
                regionName = "Maltepe - İdealtepe",
                foodAmount = 3.5,
                timestamp = formatDate(currentTime - 60 * 60 * 60 * 1000), // 2.5 days ago
                photoUrl = "https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?w=800&q=80",
                description = "Mahallemizdeki tüm sokak hayvanları için mama bıraktım 🐾",
                animalType = "Köpek",
                likeCount = 28,
                commentCount = 6,
                isLiked = true,
                tags = listOf("mahalle", "topluluk")
            )
        )
        
        return feedings
    }
    
    /**
     * Generate mock reports
     */
    fun getMockReports(categoryFilter: String? = null): List<Report> {
        val allReports = mutableListOf<Report>()
        val currentTime = System.currentTimeMillis()
        
        // FEEDING category reports
        allReports.add(
            Report(
                ihbarId = 1,
                baslik = "Aç Köpek Sürüsü",
                aciklama = "Parkta yaklaşık 10 köpek var, aç görünüyorlar. Mama desteği gerekli.",
                kategori = "FEEDING",
                ihbarTuru = "Mama İhtiyacı",
                hayvanTuru = "Köpek",
                location = ReportLocation(41.0138, 28.9650, "Kadıköy Parkı"),
                user = ReportUser(201, "Fatma Koç", null, 3, 450),
                stats = ReportStats(likes = 34, comments = 8, shares = 2),
                imageUrls = listOf("https://images.unsplash.com/photo-1477884213360-7e9d7dcc1e48?w=800&q=80"),
                olusturmaTarihi = formatDate(currentTime - 3 * 60 * 60 * 1000)
            )
        )
        
        allReports.add(
            Report(
                ihbarId = 2,
                baslik = "Kedi Kolonisi",
                aciklama = "Üniversite kampüsünde yaşayan 15+ kedi var. Mama ve su ihtiyacı var.",
                kategori = "FEEDING",
                ihbarTuru = "Mama İhtiyacı",
                hayvanTuru = "Kedi",
                location = ReportLocation(41.1053, 29.0250, "Boğaziçi Üniversitesi"),
                user = ReportUser(202, "Ali Çelik", null, 5, 820),
                stats = ReportStats(likes = 56, comments = 15, shares = 4),
                imageUrls = listOf("https://images.unsplash.com/photo-1526336024174-e58f5cdd8e13?w=800&q=80"),
                olusturmaTarihi = formatDate(currentTime - 6 * 60 * 60 * 1000)
            )
        )
        
        // REPORT category reports
        allReports.add(
            Report(
                ihbarId = 3,
                baslik = "Yaralı Köpek",
                aciklama = "Yolun kenarında yaralı bir köpek var. Arka bacağı kırık gibi görünüyor. Acil veteriner müdahalesi gerekli!",
                kategori = "REPORT",
                ihbarTuru = "Acil İhbar",
                hayvanTuru = "Köpek",
                location = ReportLocation(41.0351, 28.9833, "Beşiktaş Meydanı"),
                user = ReportUser(203, "Deniz Bulut", null, 2, 180),
                stats = ReportStats(likes = 89, comments = 23, shares = 12),
                imageUrls = listOf("https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=800&q=80"),
                olusturmaTarihi = formatDate(currentTime - 1 * 60 * 60 * 1000)
            )
        )
        
        allReports.add(
            Report(
                ihbarId = 4,
                baslik = "Kayıp Kedi",
                aciklama = "Beyaz kedi, mavi gözlü. Boynunda kırmızı tasma var. Son görüldüğü yer Nişantaşı.",
                kategori = "REPORT",
                ihbarTuru = "Kayıp Hayvan",
                hayvanTuru = "Kedi",
                location = ReportLocation(41.0461, 28.9948, "Nişantaşı"),
                user = ReportUser(204, "Cem Yalçın", null, 1, 50),
                stats = ReportStats(likes = 67, comments = 18, shares = 25),
                imageUrls = listOf("https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?w=800&q=80"),
                olusturmaTarihi = formatDate(currentTime - 4 * 60 * 60 * 1000)
            )
        )
        
        allReports.add(
            Report(
                ihbarId = 5,
                baslik = "Hasta Köpek",
                aciklama = "Parkta sağlığı kötü görünen bir köpek var. Hareket etmiyor, veteriner kontrolü gerekli.",
                kategori = "REPORT",
                ihbarTuru = "Sağlık Sorunu",
                hayvanTuru = "Köpek",
                location = ReportLocation(41.0082, 28.9784, "Taksim Gezi Parkı"),
                user = ReportUser(205, "Pınar Avcı", null, 4, 620),
                stats = ReportStats(likes = 45, comments = 11, shares = 6),
                imageUrls = listOf("https://images.unsplash.com/photo-1530281700549-e82e7bf110d6?w=800&q=80"),
                olusturmaTarihi = formatDate(currentTime - 10 * 60 * 60 * 1000)
            )
        )
        
        // ADOPTION category reports
        allReports.add(
            Report(
                ihbarId = 6,
                baslik = "Sevimli Yavru Kediler",
                aciklama = "4 yavru kedi sahiplendirme için hazır. Hepsi sağlıklı ve aşıları yapıldı. 🐱",
                kategori = "ADOPTION",
                ihbarTuru = "Sahiplendirme",
                hayvanTuru = "Kedi",
                location = ReportLocation(41.0280, 28.9745, "Kabataş"),
                user = ReportUser(206, "Gül Tekin", null, 6, 950),
                stats = ReportStats(likes = 124, comments = 45, shares = 18),
                imageUrls = listOf("https://images.unsplash.com/photo-1415369629372-26f2fe60c467?w=800&q=80"),
                olusturmaTarihi = formatDate(currentTime - 12 * 60 * 60 * 1000)
            )
        )
        
        allReports.add(
            Report(
                ihbarId = 7,
                baslik = "Ev Arayan Köpek",
                aciklama = "2 yaşında, eğitimli golden retriever. Ailevi sebeplerden dolayı bakmak zorlaştı. İyi bir aile arıyoruz.",
                kategori = "ADOPTION",
                ihbarTuru = "Sahiplendirme",
                hayvanTuru = "Köpek",
                location = ReportLocation(41.0603, 29.0176, "Çengelköy"),
                user = ReportUser(207, "Kemal Erdoğan", null, 3, 380),
                stats = ReportStats(likes = 98, comments = 32, shares = 15),
                imageUrls = listOf("https://images.unsplash.com/photo-1552053831-71594a27632d?w=800&q=80"),
                olusturmaTarihi = formatDate(currentTime - 18 * 60 * 60 * 1000)
            )
        )
        
        allReports.add(
            Report(
                ihbarId = 8,
                baslik = "Terkedilmiş Yavrular",
                aciklama = "3 yavru köpek terkedilmiş halde bulundu. Acil sahiplendirme gerekli!",
                kategori = "ADOPTION",
                ihbarTuru = "Sahiplendirme",
                hayvanTuru = "Köpek",
                location = ReportLocation(41.0422, 28.9933, "Şişli"),
                user = ReportUser(208, "Aylin Sönmez", null, 5, 710),
                stats = ReportStats(likes = 156, comments = 67, shares = 34),
                imageUrls = listOf("https://images.unsplash.com/photo-1544568100-847a948585b9?w=800&q=80"),
                olusturmaTarihi = formatDate(currentTime - 24 * 60 * 60 * 1000)
            )
        )
        
        // More FEEDING reports
        allReports.add(
            Report(
                ihbarId = 9,
                baslik = "Mama Noktası Kurulmalı",
                aciklama = "Bu bölgede çok fazla sokak hayvanı var. Düzenli mama noktası kurulmasını öneriyorum.",
                kategori = "FEEDING",
                ihbarTuru = "Öneri",
                hayvanTuru = "Köpek",
                location = ReportLocation(41.0753, 28.9948, "Sarıyer"),
                user = ReportUser(209, "Hakan Yurt", null, 4, 560),
                stats = ReportStats(likes = 43, comments = 19, shares = 7),
                imageUrls = listOf("https://images.unsplash.com/photo-1558788353-f76d92427f16?w=800&q=80"),
                olusturmaTarihi = formatDate(currentTime - 30 * 60 * 60 * 1000)
            )
        )
        
        allReports.add(
            Report(
                ihbarId = 10,
                baslik = "Kış Ayı Mama Desteği",
                aciklama = "Kışın yaklaşmasıyla mama ihtiyacı arttı. Bölgedeki hayvanlara destek gerekli.",
                kategori = "FEEDING",
                ihbarTuru = "Mama İhtiyacı",
                hayvanTuru = "Kedi",
                location = ReportLocation(40.9929, 28.8978, "Bakırköy"),
                user = ReportUser(210, "Esra Güneş", null, 6, 890),
                stats = ReportStats(likes = 72, comments = 21, shares = 9),
                imageUrls = listOf("https://images.unsplash.com/photo-1472491235688-bdc81a63246e?w=800&q=80"),
                olusturmaTarihi = formatDate(currentTime - 36 * 60 * 60 * 1000)
            )
        )
        
        // More REPORT reports
        allReports.add(
            Report(
                ihbarId = 11,
                baslik = "Kedi Kuyuya Düştü",
                aciklama = "Eski bir su kuyusuna kedi düşmüş. İtfaiye ya da yardım gerekli!",
                kategori = "REPORT",
                ihbarTuru = "Acil İhbar",
                hayvanTuru = "Kedi",
                location = ReportLocation(41.0921, 29.0253, "Beykoz"),
                user = ReportUser(211, "Murat Kara", null, 2, 220),
                stats = ReportStats(likes = 112, comments = 34, shares = 18),
                imageUrls = listOf("https://images.unsplash.com/photo-1606214174585-fe31582dc6ee?w=800&q=80"),
                olusturmaTarihi = formatDate(currentTime - 2 * 60 * 60 * 1000)
            )
        )
        
        allReports.add(
            Report(
                ihbarId = 12,
                baslik = "Trafik Kazası",
                aciklama = "Köpek arabaya çarptı. Yolun kenarında bekliyor, acil veteriner gerekli!",
                kategori = "REPORT",
                ihbarTuru = "Acil İhbar",
                hayvanTuru = "Köpek",
                location = ReportLocation(40.9664, 29.0778, "Maltepe"),
                user = ReportUser(212, "Yasemin Bal", null, 3, 410),
                stats = ReportStats(likes = 95, comments = 28, shares = 14),
                imageUrls = listOf("https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=800&q=80"),
                olusturmaTarihi = formatDate(currentTime - 5 * 60 * 60 * 1000)
            )
        )
        
        // More ADOPTION reports
        allReports.add(
            Report(
                ihbarId = 13,
                baslik = "Engelli Kedi Sahiplendirme",
                aciklama = "Üç ayağı olan kedi sahiplendirme için hazır. Çok sevimli ve uyumlu bir karakter.",
                kategori = "ADOPTION",
                ihbarTuru = "Sahiplendirme",
                hayvanTuru = "Kedi",
                location = ReportLocation(41.0332, 29.1267, "Ümraniye"),
                user = ReportUser(213, "Leyla Öz", null, 7, 1120),
                stats = ReportStats(likes = 201, comments = 78, shares = 42),
                imageUrls = listOf("https://images.unsplash.com/photo-1545529468-42764ef8c85f?w=800&q=80"),
                olusturmaTarihi = formatDate(currentTime - 48 * 60 * 60 * 1000)
            )
        )
        
        allReports.add(
            Report(
                ihbarId = 14,
                baslik = "Yaşlı Köpek Yuva Arıyor",
                aciklama = "10 yaşında husky cinsi köpek. Sahibi vefat etti, yeni bir yuva gerekli.",
                kategori = "ADOPTION",
                ihbarTuru = "Sahiplendirme",
                hayvanTuru = "Köpek",
                location = ReportLocation(41.0156, 28.9388, "Zeytinburnu"),
                user = ReportUser(214, "Serkan Aktaş", null, 4, 530),
                stats = ReportStats(likes = 167, comments = 56, shares = 29),
                imageUrls = listOf("https://images.unsplash.com/photo-1614027164847-1b28cfe1df60?w=800&q=80"),
                olusturmaTarihi = formatDate(currentTime - 60 * 60 * 60 * 1000)
            )
        )
        
        allReports.add(
            Report(
                ihbarId = 15,
                baslik = "Yavru Köpek Sahiplendirme",
                aciklama = "Karışık ırk 6 yavru köpek. Anneleri sokakta bulmuştuk, sahiplendirme yapacağız.",
                kategori = "ADOPTION",
                ihbarTuru = "Sahiplendirme",
                hayvanTuru = "Köpek",
                location = ReportLocation(41.1086, 28.9967, "Sarıyer - İstinye"),
                user = ReportUser(215, "Özgür Topal", null, 5, 775),
                stats = ReportStats(likes = 143, comments = 51, shares = 22),
                imageUrls = listOf("https://images.unsplash.com/photo-1450778869180-41d0601e046e?w=800&q=80"),
                olusturmaTarihi = formatDate(currentTime - 72 * 60 * 60 * 1000)
            )
        )
        
        // Filter by category if specified
        return if (categoryFilter != null) {
            allReports.filter { it.kategori == categoryFilter }
        } else {
            allReports
        }
    }
    
    /**
     * Generate mock map marker data
     */
    fun getMockMapMarkers(): List<MapMarkerData> {
        val markers = mutableListOf<MapMarkerData>()
        
        // Get reports and create markers from them
        val reports = getMockReports()
        reports.forEach { report ->
            markers.add(
                MapMarkerData(
                    location = LatLng(report.enlem, report.boylam),
                    emoji = getEmojiForAnimal(report.hayvanTuru),
                    category = report.kategori,
                    title = report.baslik ?: "İhbar",
                    description = report.aciklama
                )
            )
        }
        
        return markers
    }
    
    /**
     * Get appropriate emoji for animal type
     */
    private fun getEmojiForAnimal(animalType: String?): String {
        return when (animalType?.lowercase()) {
            "köpek" -> "🐕"
            "kedi" -> "🐱"
            else -> "🐾"
        }
    }
    
    /**
     * Format timestamp
     */
    private fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
    
    /**
     * Data class for map markers
     */
    data class MapMarkerData(
        val location: LatLng,
        val emoji: String,
        val category: String,
        val title: String,
        val description: String
    )
}

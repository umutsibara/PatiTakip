# 🐾 PatiTakip Backend API Dökümantasyonu (v2.2)

Bu döküman, PatiTakip uygulamasının backend API'sini (v2.2) kullanmak için gerekli **TÜM** bilgileri eksiksiz içerir. 

**Base URL**: `http://localhost:3000/api`

---

## 🔐 Kimlik Doğrulama (Authentication)

### 1. Kullanıcı Kaydı (Register)
*   **Method**: `POST`
*   **URL**: `/users/register`
*   **Erişim**: Herkes
*   **Body**:
    ```json
    {
      "kullanici_adi": "umut_yildiz",
      "eposta": "umut@example.com",
      "sifre": "GucluSifre123!"
    }
    ```
*   **Başarılı Yanıt (201)**:
    ```json
    {
      "success": true,
      "message": "Kullanıcı başarıyla oluşturuldu.",
      "data": { "kullanici_id": 1, "kullanici_adi": "umut_yildiz", "rol": "gonullu", "puan": 0, "rutbe": 0, "konum": "", "avatar_url": "" }
    }
    ```

### 2. Giriş Yap (Login)
*   **Method**: `POST`
*   **URL**: `/users/login`
*   **Erişim**: Herkes
*   **Body**:
    ```json
    {
      "eposta": "umut@example.com",
      "sifre": "GucluSifre123!"
    }
    ```
*   **Başarılı Yanıt (200)**:
    ```json
    {
      "success": true,
      "message": "Giriş başarılı.",
      "data": {
        "id": 1,
        "isim": "umut_yildiz",
        "rol": "gonullu",
        "eposta": "umut@example.com",
        "avatar_url": "https://...",
        "puan": 150,
        "rutbe": 5,
        "konum": "Kadıköy, İstanbul",
        "token": "eyJhbGciOiJIUzI1NiIsInR..." 
      }
    }
    ```

### 3. Profil Görüntüle
*   **Method**: `GET`
*   **URL**: `/users/:id/profile` (Örn: `/users/1/profile`)
*   **Erişim**: Authenticated (Token Gerekli, Opsiyonel olabilir)
*   **Başarılı Yanıt (200)**:
    ```json
    {
      "success": true,
      "user": {
        "kullanici_id": 1,
        "kullanici_adi": "umut_yildiz",
        "puan": 150,
        "rutbe": 5,
        "konum": "Kadıköy",
        "badges": [ { "rozet_id": 1, "isim": "İlk İhbar", "ikon": "🌟" } ],
        ...
      },
      "stats": { "ihbar_sayisi": 5, "besleme_sayisi": 12, "toplam_katki": 17 }
    }
    ```

### 4. Liderlik Tablosu (Leaderboard) (YENİ)
*   **Method**: `GET`
*   **URL**: `/users/leaderboard`
*   **Erişim**: Herkes
*   **Yanıt**: En yüksek puanlı 20 kullanıcıyı listeler.
    ```json
    [
      { "kullanici_id": 1, "kullanici_adi": "Ayşe", "puan": 1250, "rutbe": 1, "avatar_url": "..." },
      ...
    ]
    ```

---

## 📢 Gönderiler / İhbarlar (Unified Posts) (GÜNCELLENDİ)

Sistemdeki tüm paylaşımlar (İhbar, Besleme, Sahiplendirme) bu yapı üzerinden yönetilir. 
*Eski `/reports` endpoint'i genişletilmiştir.*

### 1. Gönderileri Listele
*   **Method**: `GET`
*   **URL**: `/reports`
*   **Parametreler (Query String)**:
    *   `category`: `REPORT`, `FEEDING`, `ADOPTION`, `SERVICE`, `DONATION`
    *   `animal_type`: `STREET`, `PET`
    *   `lat`, `lng`: Konum bazlı filtreleme için (Örn: `?lat=41.0&lng=29.0`).
*   **Erişim**: Herkes
*   **Yanıt (Frontend JSON Formatında)**:
    ```json
    {
      "success": true,
      "count": 5,
      "data": [
        {
          "id": 10,
          "title": "Yaralı Kedi",
          "description": "...",
          "category": "REPORT",
          "type": "Yaralanma", // (ihbar_turu)
          "animalType": "STREET",
          "location": { "latitude": 41.0, "longitude": 29.0, "address": "Moda, Kadıköy" },
          "user": { "id": 1, "userName": "Umut", "avatarUrl": "...", "rank": 5, "points": 150 },
          "stats": { "likes": 5, "comments": 2, "shares": 1 },
          "createdAt": "2024-..."
        }
      ]
    }
    ```

### 2. Tekil Gönderi Detayı
*   **Method**: `GET`
*   **URL**: `/reports/:id`
*   **Erişim**: Herkes
*   **Yanıt**: Tek bir gönderinin detaylarını döner.

### 3. Yeni Gönderi Oluştur
*   **Method**: `POST`
*   **URL**: `/reports`
*   **Header**: `Authorization: Bearer <TOKEN>`
*   **Body**:
    ```json
    {
      "kullanici_id": 1,
      "baslik": "Mama Bağışı Lazım",
      "aciklama": "Parktaki kediler aç.",
      "kategori": "FEEDING",     // REPORT, FEEDING, ADOPTION, SERVICE, DONATION
      "ihbar_turu": "Aclik",       // Alt kategori (Opsiyonel: Aclik, Saglik, Kayip)
      "hayvan_turu": "STREET",     // STREET, PET
      "enlem": 41.0,
      "boylam": 29.0,
      "adres": "Moda Parkı",
      "foto_id": 25,               // (Opsiyonel - /upload/photo'dan gelen ID)
      "bolge_id": 2                // (Opsiyonel)
    }
    ```
*   **Dikkat**: Gönderi oluşturulduğunda kullanıcı +10 puan kazanır.

### 4. İhbarı Çözüldü İşaretle
*   **Method**: `PUT`
*   **URL**: `/reports/:id/resolve`
*   **Body**: `{ "notlar": "Çözüldü açıklaması..." }`

---

## ❤️ Bağışlar (Donations) (YENİ)

### 1. Bağış Kampanyalarını Listele
*   **Method**: `GET`
*   **URL**: `/donations`
*   **Yanıt**:
    ```json
    {
      "success": true,
      "data": [
        {
          "id": 1,
          "title": "Kış İçin Kedi Evi",
          "description": "...",
          "requiredPoints": 5000,
          "category": "SHELTER", // FOOD, MEDICAL, SHELTER
          "icon": "🏠",
          "active": true
        }
      ]
    }
    ```

### 2. Yeni Bağış Kampanyası Oluştur
*   **Method**: `POST`
*   **URL**: `/donations`
*   **Header**: `Authorization: Bearer <TOKEN>`
*   **Body**: `{ "baslik": "...", "aciklama": "...", "gerekli_puan": 1000, "kategori": "FOOD" }`

---

## 🛠️ Hizmetler (Services) (YENİ)

### 1. Hizmetleri Listele
*   **Method**: `GET`
*   **URL**: `/services`
*   **Yanıt**:
    ```json
    {
      "success": true,
      "data": [
        {
          "id": 1,
          "provider": { "id": 5, "name": "Veli", "avatarUrl": "..." },
          "title": "Haftasonu Köpek Gezdirme",
          "description": "...",
          "pricePerDay": 200.0,
          "category": "PET_SITTING", // PET_SITTING, TRAINING, GROOMING
          "rating": 4.8
        }
      ]
    }
    ```

### 2. Hizmet İlanı Ver
*   **Method**: `POST`
*   **URL**: `/services`
*   **Header**: `Authorization: Bearer <TOKEN>`
*   **Body**: `{ "saglayici_id": 1, "baslik": "...", "aciklama": "...", "fiyat_gunluk": 150, "kategori": "GROOMING" }`

---

## 🍖 Besleme İşlemleri (Feedings - Legacy & Unified)
*Not: Yeni sistemde Beslemeler `/reports` (kategori=FEEDING) altında tutulabilir, ancak eski endpointler de aktiftir.*

### 1. Besleme Yap
*   **Method**: `POST`
*   **URL**: `/feedings` (veya `api/reports` kullanın)
*   **Body**: `{ "kullanici_id": 1, "bolge_id": 3, "miktar": 2.5 }`

### 2. Beslemeleri Listele
*   **Method**: `GET`
*   **URL**: `/feedings`

---

## 🏥 Sağlık Kayıtları (Health Records)

### 1. Bir Hayvanın Geçmişini Gör
*   **Method**: `GET`
*   **URL**: `/health-records/animal/:animalId`
*   **Header**: `Authorization: Bearer <TOKEN>`

### 2. Sağlık Kaydı Ekle
*   **Method**: `POST`
*   **URL**: `/health-records`
*   **Header**: `Authorization: Bearer <TOKEN>`
*   **Body**:
    ```json
    {
      "hayvan_id": 5,
      "mudahale_turu": "Aşı",
      "veteriner_notu": "Kuduz ve Karma aşıları yapıldı."
    }
    ```

### 3. Kayıt Güncelle & Sil
*   **PUT**: `/health-records/:id` -> Body: `{ "veteriner_notu": "..." }`
*   **DELETE**: `/health-records/:id`

---

## 🐶 Hayvanlar & İstatistikler

### 1. Hayvanlar (Animals)
*   **Listele (GET)**: `/animals`
*   **Ekle (POST)**: `/animals`

### 2. İstatistikler
*   **Genel Stats**: `GET /stats/general`
*   **Bölge Risk**: `GET /stats/risk/:id`

---

## 🖼️ Dosya Yükleme (File Upload)

### Fotoğraf Yükle
*   **Method**: `POST`
*   **URL**: `/upload/photo`
*   **Header**: `Authorization: Bearer <TOKEN>`
*   **Format**: `multipart/form-data`
*   **Form Key**: `photo` (Dosya seçimi)
*   **Yanıt**:
    ```json
    {
      "success": true,
      "data": {
        "foto_id": 22,
        "url": "/uploads/photos/foto-123456789.webp"
      }
    }
    ```

---

## 🌍 Yönetim & Referans Veriler (Admin & Zones)

### 1. Bölgeler (Zones)
*   **Listele (GET)**: `/zones`
*   **Ekle/Güncelle/Sil**: `/zones` (Sadece Admin)

### 2. Hayvan Türleri (Types)
*   **Listele (GET)**: `/animal-types`
*   **Ekle/Güncelle/Sil**: `/animal-types` (Sadece Admin)

### 3. Sistem Logları (Admin)
*   **URL**: `/admin/logs`
*   **Header**: `Authorization: Bearer <TOKEN>` (Rol: 'yonetici' olmalı)

---

## ⚠️ Hata Kodları

*   **200/201**: Başarılı.
*   **400**: Hatalı İstek (Eksik parametre, vb.).
*   **401**: Yetkisiz (Token yok veya geçersiz).
*   **403**: Yasaklı (Yönetici yetkisi gerekiyor).
*   **404**: Bulunamadı (Endpoint veya Kayıt yok).
*   **500**: Sunucu Hatası (Veritabanı hatası vb.).

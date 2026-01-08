# Backend Geliştirme Gereksinimleri

Frontend uygulamasının ("PatiTakip") şu anda kullandığı `MockDataProvider` (sahte veri sağlayıcısı) ile mevcut Backend yapısı arasında önemli farklar bulunmaktadır. Uygulamanın tam fonksiyonel çalışabilmesi için Backend tarafında aşağıdaki tabloların, endpoint'lerin ve veri yapılarının eklenmesi gerekmektedir.

> [!NOTE]
> Bu belge, Frontend'deki "Dumb Data" (Mock Data) yapısına sadık kalınarak hazırlanmıştır.

---

## 1. Veritabanı Tabloları (SQL Şeması Önerisi)

Mevcut `kullanicilar` ve `ihbarlar` tablolarına ek olarak veya bunların güncellenmesiyle şu yapıların oluşturulması gerekir.

### 1.1. Kullanıcılar (Users)
Kullanıcı profili, seviye sistemi ve rozetler için genişletilmeli.
- `id` (INT, PK)
- `kullanici_adi` (VARCHAR)
- `eposta` (VARCHAR)
- `sifre` (VARCHAR, Hash'lenmiş)
- `avatar_url` (VARCHAR, NULLABLE) - *Frontend'de kullanılıyor*
- `puan` (INT, Default: 0) - *Gamification puanı*
- `rutbe` (INT, Default: 0) - *Liderlik tablosu sıralaması için*
- `konum` (VARCHAR) - *Örn: "Kadıköy, İstanbul"*
- `olusturulma_tarihi` (DATETIME)

### 1.2. İhbarlar / Gönderiler (Posts)
Hem acil ihbarlar hem de sosyal paylaşımlar (besleme, sahiplendirme) için tek bir yapı veya `type` ayrımı.
- `id` (INT, PK)
- `kullanici_id` (INT, FK -> Users.id)
- `baslik` (VARCHAR)
- `aciklama` (TEXT)
- `kategori` (ENUM: 'REPORT', 'FEEDING', 'ADOPTION', 'SERVICE', 'DONATION')
- `hayvan_turu` (ENUM: 'STREET', 'PET')
- `durum` (ENUM: 'HEALTHY', 'INJURED', 'HUNGRY', 'LOST')
- `enlem` (DOUBLE)
- `boylam` (DOUBLE)
- `adres` (VARCHAR) - *Ters geocoding sonucu veya manuel*
- `begeni_sayisi` (INT)
- `yorum_sayisi` (INT)
- `paylasim_sayisi` (INT)
- `olusturulma_tarihi` (DATETIME)

### 1.3. Rozetler (Badges)
Kullanıcıların kazandığı başarımlar.
- `id` (INT, PK)
- `isim` (VARCHAR) - *Örn: "İlk İhbar"*
- `ikon` (VARCHAR) - *Örn: "🌟"*
- `aciklama` (VARCHAR)
- `gerekli_puan` (INT)

### 1.4. Kullanıcı Rozetleri (UserBadges)
Çoka-çok ilişki tablosu.
- `kullanici_id` (INT)
- `rozet_id` (INT)
- `kazanilma_tarihi` (DATETIME)

### 1.5. Bağışlar (Donations)
Bağış kampanyaları.
- `id` (INT, PK)
- `baslik` (VARCHAR)
- `aciklama` (TEXT)
- `gerekli_puan` (INT)
- `kategori` (ENUM: 'FOOD', 'MEDICAL', 'SHELTER')
- `ikon` (VARCHAR)

### 1.6. Hizmetler (Services)
Kullanıcıların sunduğu hizmetler (Pet Sitting, vb.).
- `id` (INT, PK)
- `saglayici_id` (INT, FK -> Users.id)
- `baslik` (VARCHAR)
- `aciklama` (TEXT)
- `fiyat_gunluk` (DOUBLE)
- `kategori` (ENUM: 'PET_SITTING', 'TRAINING', 'GROOMING')
- `puan` (FLOAT)

---

## 2. Gerekli API Endpoint'leri

Frontend'in beklediği JSON formatlarına uygun endpoint'ler.

### 2.1. Kimlik Doğrulama (Auth)
- `POST /api/auth/register`
- `POST /api/auth/login`
  - **Geri Dönüş (Response):** Token'a ek olarak kullanıcının temel bilgilerini (`avatar_url`, `puan`, `rutbe` vb.) dönmeli. Mevcut `AuthResponse` sadece `success` ve `UserDto` (id, isim, rol) dönüyor. Bu **yetersiz**.

### 2.2. Gönderiler (Posts/Reports)
- `GET /api/posts`
  - Filtreleme parametreleri: `?category=REPORT`, `?animal_type=STREET`, `?lat=...&lng=...&radius=...`
- `POST /api/posts` (veya `/api/reports`)
  - Yeni ihbar/gönderi oluşturma.
- `GET /api/posts/{id}`

### 2.3. Liderlik Tablosu (Leaderboard)
- `GET /api/leaderboard`
  - En yüksek puana sahip kullanıcıları listeler.
  - JSON Örneği:
    ```json
    [
      {
        "rank": 1,
        "user": { "id": 2, "name": "Ayşe", "avatar_url": "...", "points": 1250 },
        "stats": { "total_reports": 25, "total_feedings": 50 }
      }
    ]
    ```

### 2.4. Kullanıcı Profili (Profile)
- `GET /api/users/{id}`
  - Kullanıcının istatistikleri, rozetleri ve geçmiş gönderileriyle birlikte detaylı bilgi.

### 2.5. Bağışlar ve Hizmetler
- `GET /api/donations`
- `GET /api/services`

---

## 3. JSON Veri Modelleri (Frontend Beklentisi)

Frontend (`MockDataProvider`) şu veri yapılarını beklemektedir. Backend bu key'lere sadık kalmalı veya Frontend'de mapping (dönüştürme) katmanı güncellenmelidir.

### User Object
```json
{
  "id": 1,
  "userName": "Umut Yıldız",
  "email": "umut@example.com",
  "avatarUrl": "https://...",
  "points": 450,
  "rank": 24,
  "location": "Kadıköy, İstanbul",
  "badges": [ ... ],
  "createdAt": "2024-01-15"
}
```

### Post Object
```json
{
  "id": 1,
  "title": "Yaralı Köpek",
  "description": "...",
  "category": "REPORT",
  "photoUrls": ["https://..."],
  "location": {
      "latitude": 40.99,
      "longitude": 29.02,
      "address": "Kadıköy"
  },
  "user": { ... }, // Gönderiyi paylaşan kullanıcı
  "stats": {
      "likes": 12,
      "comments": 5,
      "shares": 3
  }
}
```

## Özet: Yapılması Gerekenler

1.  **Backend DTO'larını Genişlet:** `UserDto` içine `email`, `avatar_url`, `puan` ekle.
2.  **Yeni Endpointleri Aç:** Özellikle Liderlik Tablosu ve Profil detayları için.
3.  **Kategori Yönetimi:** Sadece "İhbar" değil, "Besleme", "Sahiplendirme" gibi kategorileri de destekle.
4.  **Resim Upload:** Multipart upload endpoint'inin sağlamlığını test et ve URL döndürdüğünden emin ol.

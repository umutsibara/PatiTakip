# 🐾 PatiTakip API - Detaylı Kullanım Dokümantasyonu

## 📋 İçindekiler
1. [Genel Bilgiler](#genel-bilgiler)
2. [Authentication (Kimlik Doğrulama)](#authentication)
3. [Kullanıcı İşlemleri](#kullanıcı-işlemleri)
4. [İhbar/Gönderi İşlemleri](#ihbar-gönderi-işlemleri)
5. [Besleme İşlemleri](#besleme-işlemleri)
6. [Hayvan İşlemleri](#hayvan-işlemleri)
7. [İstatistik İşlemleri](#istatistik-işlemleri)
8. [Dosya Yükleme](#dosya-yükleme)
9. [Referans Veriler (Bölgeler & Türler)](#referans-veriler)
10. [Sağlık Kayıtları](#sağlık-kayıtları)
11. [Admin İşlemleri](#admin-işlemleri)
12. [Bağış İşlemleri](#bağış-işlemleri)
13. [Hizmet İşlemleri](#hizmet-işlemleri)
14. [Hata Kodları](#hata-kodları)

---

## Genel Bilgiler

### Base URL
```
http://192.168.1.4:3000
```

### İstek Formatı
- **Content-Type**: `application/json`
- **Authorization**: `Bearer <TOKEN>` (Korumalı endpoint'ler için)

### Yanıt Formatı
Tüm API yanıtları JSON formatındadır ve genellikle şu yapıyı takip eder:

**Başarılı Yanıt:**
```json
{
  "success": true,
  "data": { ... },
  "message": "İşlem başarılı"
}
```

**Hata Yanıtı:**
```json
{
  "success": false,
  "error": "Hata mesajı"
}
```

---

## Authentication

### Kimlik Doğrulama Sistemi
API, JWT (JSON Web Token) tabanlı kimlik doğrulama kullanır. Token'lar 24 saat geçerlidir.

#### Korumalı Endpoint'lere Erişim
Token gerektiren endpoint'ler için header'a şu bilgiyi ekleyin:
```
Authorization: Bearer <your_token_here>
```

### Rol Bazlı Yetkilendirme
- **gonullu**: Normal kullanıcı, temel işlemler
- **yonetici**: Admin yetkisi, tüm işlemler + admin panel

---

## Kullanıcı İşlemleri

### 1. Kullanıcı Kaydı

**Endpoint:** `POST /api/users/register`

**Açıklama:** Yeni kullanıcı kaydı oluşturur.

**Request Body:**
```json
{
  "kullanici_adi": "umut_sibara",
  "eposta": "umut@example.com",
  "sifre": "guvenli_sifre_123"
}
```

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "kullanici_id": 1,
    "kullanici_adi": "umut_sibara",
    "rol": "gonullu",
    "puan": 0,
    "rutbe": 0,
    "konum": "",
    "avatar_url": ""
  },
  "message": "Kullanıcı başarıyla oluşturuldu."
}
```

**Hata Durumları:**
- `400`: Eksik alan
- `409`: E-posta veya kullanıcı adı zaten kayıtlı
- `500`: Sunucu hatası

---

### 2. Giriş Yapma (Login)

**Endpoint:** `POST /api/users/login`

**Açıklama:** Kullanıcı girişi yapar ve JWT token döner.

**Request Body:**
```json
{
  "eposta": "umut@example.com",
  "sifre": "guvenli_sifre_123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Giriş başarılı.",
  "data": {
    "id": 1,
    "isim": "umut_sibara",
    "rol": "gonullu",
    "eposta": "umut@example.com",
    "avatar_url": "",
    "puan": 150,
    "rutbe": 2,
    "konum": "İzmir",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Önemli:** `token` değerini saklayın ve sonraki isteklerde kullanIn.

**Hata Durumları:**
- `404`: Kullanıcı bulunamadı
- `401`: Hatalı şifre
- `500`: Sunucu hatası

---

### 3. Kullanıcı Profili Görüntüleme

**Endpoint:** `GET /api/users/:id/profile`

**Açıklama:** Belirtilen kullanıcının profil bilgilerini, rozetlerini ve istatistiklerini getirir.

**Parametreler:**
- `:id` - Kullanıcı ID (integer)

**Request Örneği:**
```
GET /api/users/1/profile
```

**Response (200 OK):**
```json
{
  "success": true,
  "user": {
    "kullanici_id": 1,
    "kullanici_adi": "umut_sibara",
    "eposta": "umut@example.com",
    "puan": 150,
    "rutbe": 2,
    "konum": "İzmir",
    "avatar_url": "",
    "olusturulma_tarihi": "2025-01-15T10:30:00.000Z",
    "badges": [
      {
        "rozet_id": 1,
        "rozet_adi": "İlk Adım",
        "aciklama": "İlk ihbarını oluşturdu",
        "kazanilma_tarihi": "2025-01-16T12:00:00.000Z"
      }
    ]
  },
  "stats": {
    "ihbar_sayisi": 5,
    "besleme_sayisi": 3,
    "toplam_katki": 8
  }
}
```

**Hata Durumları:**
- `404`: Kullanıcı bulunamadı
- `500`: Sunucu hatası

---

### 4. Liderlik Tablosu

**Endpoint:** `GET /api/users/leaderboard`

**Açıklama:** En yüksek puana sahip 20 kullanıcıyı listeler.

**Request Örneği:**
```
GET /api/users/leaderboard
```

**Response (200 OK):**
```json
[
  {
    "kullanici_id": 5,
    "kullanici_adi": "ali_yilmaz",
    "puan": 350,
    "rutbe": 5,
    "avatar_url": "/uploads/photos/avatar_5.webp",
    "konum": "İstanbul"
  },
  {
    "kullanici_id": 1,
    "kullanici_adi": "umut_sibara",
    "puan": 150,
    "rutbe": 2,
    "avatar_url": "",
    "konum": "İzmir"
  }
]
```

---

## İhbar Gönderi İşlemleri

### 1. Tüm İhbarları Listeleme

**Endpoint:** `GET /api/reports`

**Açıklama:** Aktif durumda olan tüm ihbarları/gönderileri listeler. Filtreleme desteği vardır.

**Query Parametreleri (Opsiyonel):**
- `category` - Kategori filtresi (REPORT, FEED, INJURY, vb.)
- `animal_type` - Hayvan türü filtresi (Kedi, Köpek, vb.)
- `lat` - Enlem (konum filtresi için)
- `lng` - Boylam (konum filtresi için)
- `radius` - Yarıçap (konum filtresi için, varsayılan ±0.1)

**Request Örnekleri:**
```
GET /api/reports
GET /api/reports?category=REPORT
GET /api/reports?animal_type=Kedi
GET /api/reports?lat=38.4237&lng=27.1428
GET /api/reports?category=INJURY&animal_type=Köpek
```

**Response (200 OK):**
```json
{
  "success": true,
  "count": 2,
  "data": [
    {
      "id": 15,
      "title": "Yaralı kedi",
      "description": "Bacağında yara var, acil müdahale gerekiyor",
      "category": "REPORT",
      "type": "Saglik",
      "animalType": "Kedi",
      "location": {
        "latitude": 38.4237,
        "longitude": 27.1428,
        "address": "Alsancak, İzmir"
      },
      "user": {
        "id": 1,
        "userName": "umut_sibara",
        "avatarUrl": "",
        "rank": 2,
        "points": 150
      },
      "stats": {
        "likes": 0,
        "comments": 0,
        "shares": 0
      },
      "imageUrls": [],
      "createdAt": "2025-01-20T14:30:00.000Z"
    }
  ]
}
```

---

### 2. Tekil İhbar Görüntüleme

**Endpoint:** `GET /api/reports/:id`

**Açıklama:** Belirtilen ID'ye sahip ihbarın detaylarını getirir.

**Parametreler:**
- `:id` - İhbar ID (integer)

**Request Örneği:**
```
GET /api/reports/15
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 15,
    "title": "Yaralı kedi",
    "description": "Bacağında yara var, acil müdahale gerekiyor",
    "category": "REPORT",
    "type": "Saglik",
    "animalType": "Kedi",
    "location": {
      "latitude": 38.4237,
      "longitude": 27.1428,
      "address": "Alsancak, İzmir"
    },
    "user": {
      "id": 1,
      "userName": "umut_sibara",
      "avatarUrl": "",
      "rank": 2,
      "points": 150
    },
    "stats": {
      "likes": 0,
      "comments": 0,
      "shares": 0
    },
    "imageUrls": [],
    "createdAt": "2025-01-20T14:30:00.000Z"
  }
}
```

**Hata Durumları:**
- `404`: Gönderi bulunamadı
- `500`: Sunucu hatası

---

### 3. Yeni İhbar/Gönderi Oluşturma

**Endpoint:** `POST /api/reports`

**Açıklama:** Yeni bir ihbar veya gönderi oluşturur. Kullanıcıya +10 puan verilir.

**Request Body:**
```json
{
  "kullanici_id": 1,
  "baslik": "Yaralı kedi",
  "aciklama": "Bacağında yara var, acil müdahale gerekiyor",
  "ihbar_turu": "Saglik",
  "kategori": "REPORT",
  "hayvan_turu": "Kedi",
  "enlem": 38.4237,
  "boylam": 27.1428,
  "adres": "Alsancak, İzmir",
  "bolge_id": 1,
  "hayvan_id": null,
  "foto_id": null
}
```

**Zorunlu Alanlar:**
- `kullanici_id`
- `aciklama`
- `enlem`
- `boylam`

**Opsiyonel Alanlar:**
- `baslik` (varsayılan: "Başlıksız Gönderi")
- `ihbar_turu` (varsayılan: "Genel")
- `kategori` (varsayılan: "REPORT")
- `hayvan_turu` (varsayılan: "Kedi")
- `adres`
- `bolge_id`
- `hayvan_id`
- `foto_id`

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "ihbar_id": 15,
    "kullanici_id": 1,
    "baslik": "Yaralı kedi",
    "aciklama": "Bacağında yara var, acil müdahale gerekiyor",
    "ihbar_turu": "Saglik",
    "kategori": "REPORT",
    "hayvan_turu": "Kedi",
    "enlem": 38.4237,
    "boylam": 27.1428,
    "durum": "Acik",
    "begeni_sayisi": 0,
    "yorum_sayisi": 0,
    "paylasim_sayisi": 0,
    "olusturulma_tarihi": "2025-01-20T14:30:00.000Z"
  },
  "message": "Gönderi başarıyla oluşturuldu."
}
```

**Hata Durumları:**
- `400`: Eksik zorunlu alan
- `500`: Sunucu hatası

---

### 4. İhbarı Çözüldü Olarak İşaretleme

**Endpoint:** `PUT /api/reports/:id/resolve`

**Açıklama:** Belirtilen ihbarı "Çözüldü" olarak işaretler ve çözüm notunu ekler.

**Parametreler:**
- `:id` - İhbar ID (integer)

**Request Body:**
```json
{
  "notlar": "Veteriner müdahale etti, hayvan şu an güvende"
}
```

**Request Örneği:**
```
PUT /api/reports/15/resolve
Content-Type: application/json

{
  "notlar": "Veteriner müdahale etti, hayvan şu an güvende"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "İhbar ID 15 başarıyla çözüldü."
}
```

**Hata Durumları:**
- `404`: İhbar bulunamadı
- `500`: Sunucu hatası

---

## Besleme İşlemleri

### 1. Besleme Kaydı Ekleme

**Endpoint:** `POST /api/feedings`

**Açıklama:** Yeni bir besleme kaydı oluşturur.

**Request Body:**
```json
{
  "kullanici_id": 1,
  "bolge_id": 2,
  "miktar": 2.5
}
```

**Zorunlu Alanlar:**
- `kullanici_id` - Besleyen kullanıcının ID'si
- `bolge_id` - Besleme yapılan bölge ID'si
- `miktar` - Mama miktarı (kg)

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Besleme kaydı başarıyla eklendi."
}
```

**Hata Durumları:**
- `400`: Eksik veri
- `500`: Sunucu hatası

---

### 2. Beslemeleri Listeleme

**Endpoint:** `GET /api/feedings`

**Açıklama:** Son 50 besleme kaydını listeler (en yeniler önce).

**Request Örneği:**
```
GET /api/feedings
```

**Response (200 OK):**
```json
{
  "success": true,
  "count": 2,
  "data": [
    {
      "besleme_id": 10,
      "kullanici_id": 1,
      "bolge_id": 2,
      "mama_miktari_kg": 2.5,
      "besleme_zamani": "2025-01-20T09:00:00.000Z",
      "deleted_at": null
    },
    {
      "besleme_id": 9,
      "kullanici_id": 3,
      "bolge_id": 1,
      "mama_miktari_kg": 1.0,
      "besleme_zamani": "2025-01-19T18:30:00.000Z",
      "deleted_at": null
    }
  ]
}
```

---

## Hayvan İşlemleri

### 1. Hayvanları Listeleme

**Endpoint:** `GET /api/animals`

**Açıklama:** Sistemdeki tüm hayvanları ve detaylarını listeler.

**Request Örneği:**
```
GET /api/animals
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "hayvan_id": 1,
      "isim": "Minnoş",
      "tur_adi": "Kedi",
      "tahmini_yas": 2,
      "saglik_mudahale_sayisi": 3
    },
    {
      "hayvan_id": 2,
      "isim": "Karabaş",
      "tur_adi": "Köpek",
      "tahmini_yas": 5,
      "saglik_mudahale_sayisi": 1
    }
  ]
}
```

---

### 2. Yeni Hayvan Ekleme

**Endpoint:** `POST /api/animals`

**Açıklama:** Sisteme yeni bir hayvan kaydeder.

**Request Body:**
```json
{
  "tur_id": 1,
  "isim": "Pamuk",
  "yas": 1
}
```

**Zorunlu Alanlar:**
- `tur_id` - Hayvan türü ID'si

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "hayvan_id": 3,
    "tur_id": 1,
    "isim": "Pamuk",
    "tahmini_yas": 1,
    "deleted_at": null
  }
}
```

---

## İstatistik İşlemleri

### 1. Genel İstatistikler

**Endpoint:** `GET /api/stats/general`

**Açıklama:** Günlük aktivite ve bölgesel açlık durumu istatistiklerini getirir.

**Request Örneği:**
```
GET /api/stats/general
```

**Response (200 OK):**
```json
{
  "success": true,
  "bugun": {
    "tarih": "2025-01-20",
    "yeni_ihbarlar": 5,
    "bugunku_beslemeler": 8
  },
  "aclik_durumu": [
    {
      "bolge_adi": "Bornova",
      "aclik_ihbar_sayisi": 3,
      "toplam_besleme": 12
    },
    {
      "bolge_adi": "Karşıyaka",
      "aclik_ihbar_sayisi": 1,
      "toplam_besleme": 25
    }
  ]
}
```

---

### 2. Bölge Risk Analizi

**Endpoint:** `GET /api/stats/risk/:id`

**Açıklama:** Belirtilen bölgenin risk seviyesini hesaplar (açık sağlık ihbarlarına göre).

**Parametreler:**
- `:id` - Bölge ID (integer)

**Risk Seviyeleri:**
- **YUKSEK**: 5'ten fazla açık sağlık ihbarı
- **ORTA**: 2-5 arası açık sağlık ihbarı
- **DUSUK**: 2'den az açık sağlık ihbarı

**Request Örneği:**
```
GET /api/stats/risk/1
```

**Response (200 OK):**
```json
{
  "success": true,
  "bolge_id": "1",
  "risk_seviyesi": "ORTA"
}
```

---

## Dosya Yükleme

### Fotoğraf Yükleme

**Endpoint:** `POST /api/upload/photo`

**Açıklama:** Fotoğraf yükler, Sharp kütüphanesi ile işler (boyutlandırma, sıkıştırma, WebP dönüştürme) ve veritabanına kaydeder.

**Authentication:** Gerekli (Bearer Token)

**Request Type:** `multipart/form-data`

**Request Body:**
```
photo: [DOSYA] (form-data)
```

**cURL Örneği:**
```bash
curl -X POST http://192.168.1.4:3000/api/upload/photo \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -F "photo=@/path/to/image.jpg"
```

**Axios Örneği (JavaScript):**
```javascript
const formData = new FormData();
formData.append('photo', fileInput.files[0]);

const response = await axios.post('http://192.168.1.4:3000/api/upload/photo', formData, {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'multipart/form-data'
  }
});
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Fotoğraf başarıyla yüklendi.",
  "data": {
    "foto_id": 5,
    "url": "/uploads/photos/1737385729483-pamuk.webp"
  }
}
```

**Tam URL Oluşturma:**
```
http://192.168.1.4:3000/uploads/photos/1737385729483-pamuk.webp
```

**Hata Durumları:**
- `401`: Token yok veya geçersiz
- `403`: Token süresi dolmuş
- `500`: Dosya yükleme hatası

**Notlar:**
- Fotoğraf otomatik olarak WebP formatına dönüştürülür
- Maksimum genişlik/yükseklik: 1920px
- Kalite: %80
- Dosyalar `uploads/photos/` dizinine kaydedilir

---

## Referans Veriler

### Bölgeler (Zones)

#### 1. Bölgeleri Listeleme

**Endpoint:** `GET /api/zones`

**Açıklama:** Tüm aktif bölgeleri listeler.

**Request Örneği:**
```
GET /api/zones
```

**Response (200 OK):**
```json
{
  "success": true,
  "count": 3,
  "data": [
    {
      "bolge_id": 1,
      "bolge_adi": "Bornova",
      "ilce": "Bornova",
      "merkez_enlem": 38.4621,
      "merkez_boylam": 27.2145,
      "deleted_at": null
    },
    {
      "bolge_id": 2,
      "bolge_adi": "Karşıyaka",
      "ilce": "Karşıyaka",
      "merkez_enlem": 38.4598,
      "merkez_boylam": 27.1091,
      "deleted_at": null
    }
  ]
}
```

---

#### 2. Bölge Ekleme

**Endpoint:** `POST /api/zones`

**Authentication:** Gerekli (Admin)

**Request Body:**
```json
{
  "bolge_adi": "Çiğli",
  "ilce": "Çiğli",
  "merkez_enlem": 38.4981,
  "merkez_boylam": 27.0506
}
```

**Zorunlu Alanlar:**
- `bolge_adi`
- `ilce`

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "bolge_id": 4,
    "bolge_adi": "Çiğli",
    "ilce": "Çiğli",
    "merkez_enlem": 38.4981,
    "merkez_boylam": 27.0506,
    "deleted_at": null
  }
}
```

**Hata Durumları:**
- `401`: Token yok
- `403`: Yönetici yetkisi yok
- `400`: Eksik alan
- `500`: Sunucu hatası

---

#### 3. Bölge Güncelleme

**Endpoint:** `PUT /api/zones/:id`

**Authentication:** Gerekli (Admin)

**Parametreler:**
- `:id` - Bölge ID (integer)

**Request Body:**
```json
{
  "bolge_adi": "Yeni Bölge Adı",
  "merkez_enlem": 38.5000
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "bolge_id": 4,
    "bolge_adi": "Yeni Bölge Adı",
    "ilce": "Çiğli",
    "merkez_enlem": 38.5000,
    "merkez_boylam": 27.0506,
    "deleted_at": null
  }
}
```

**Hata Durumları:**
- `404`: Bölge bulunamadı
- `403`: Yönetici yetkisi yok

---

#### 4. Bölge Silme (Soft Delete)

**Endpoint:** `DELETE /api/zones/:id`

**Authentication:** Gerekli (Admin)

**Parametreler:**
- `:id` - Bölge ID (integer)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Bölge başarıyla silindi."
}
```

**Hata Durumları:**
- `404`: Bölge bulunamadı veya zaten silinmiş
- `403`: Yönetici yetkisi yok

---

### Hayvan Türleri (Animal Types)

#### 1. Hayvan Türlerini Listeleme

**Endpoint:** `GET /api/animal-types`

**Açıklama:** Tüm hayvan türlerini listeler.

**Request Örneği:**
```
GET /api/animal-types
```

**Response (200 OK):**
```json
{
  "success": true,
  "count": 6,
  "data": [
    {
      "tur_id": 1,
      "tur_adi": "Kedi",
      "aciklama": "Evcil kedi türleri"
    },
    {
      "tur_id": 2,
      "tur_adi": "Köpek",
      "aciklama": "Evcil köpek türleri"
    },
    {
      "tur_id": 3,
      "tur_adi": "Kuş",
      "aciklama": "Sokak ve evcil kuşlar"
    }
  ]
}
```

---

#### 2. Hayvan Türü Ekleme

**Endpoint:** `POST /api/animal-types`

**Authentication:** Gerekli (Admin)

**Request Body:**
```json
{
  "tur_adi": "Tavşan",
  "aciklama": "Evcil tavşan türleri"
}
```

**Zorunlu Alanlar:**
- `tur_adi`

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "tur_id": 7,
    "tur_adi": "Tavşan",
    "aciklama": "Evcil tavşan türleri"
  }
}
```

---

#### 3. Hayvan Türü Güncelleme

**Endpoint:** `PUT /api/animal-types/:id`

**Authentication:** Gerekli (Admin)

**Parametreler:**
- `:id` - Tür ID (integer)

**Request Body:**
```json
{
  "aciklama": "Güncellenmiş açıklama"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "tur_id": 7,
    "tur_adi": "Tavşan",
    "aciklama": "Güncellenmiş açıklama"
  }
}
```

---

#### 4. Hayvan Türü Silme (Hard Delete)

**Endpoint:** `DELETE /api/animal-types/:id`

**Authentication:** Gerekli (Admin)

**Parametreler:**
- `:id` - Tür ID (integer)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Tür başarıyla silindi."
}
```

**Hata Durumları:**
- `404`: Tür bulunamadı veya zaten silinmiş
- `403`: Yönetici yetkisi yok

---

## Sağlık Kayıtları

### 1. Hayvana Ait Sağlık Kayıtlarını Listeleme

**Endpoint:** `GET /api/health-records/animal/:animalId`

**Authentication:** Gerekli

**Açıklama:** Belirtilen hayvana ait sağlık kayıtlarını getirir.

**Parametreler:**
- `:animalId` - Hayvan ID (integer)

**Request Örneği:**
```
GET /api/health-records/animal/1
Authorization: Bearer YOUR_TOKEN
```

**Response (200 OK):**
```json
{
  "success": true,
  "count": 2,
  "data": [
    {
      "kayit_id": 5,
      "hayvan_id": 1,
      "mudahale_turu": "Aşılama",
      "veteriner_notu": "Kuduz aşısı yapıldı",
      "tarih": "2025-01-15T10:00:00.000Z",
      "deleted_at": null
    },
    {
      "kayit_id": 3,
      "hayvan_id": 1,
      "mudahale_turu": "Kontrol",
      "veteriner_notu": "Genel sağlık durumu iyi",
      "tarih": "2025-01-10T14:30:00.000Z",
      "deleted_at": null
    }
  ]
}
```

---

### 2. Sağlık Kaydı Ekleme

**Endpoint:** `POST /api/health-records`

**Authentication:** Gerekli

**Request Body:**
```json
{
  "hayvan_id": 1,
  "mudahale_turu": "Tedavi",
  "veteriner_notu": "Bacak yarasına pansuman yapıldı"
}
```

**Zorunlu Alanlar:**
- `hayvan_id`
- `mudahale_turu`

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "kayit_id": 6,
    "hayvan_id": 1,
    "mudahale_turu": "Tedavi",
    "veteriner_notu": "Bacak yarasına pansuman yapıldı",
    "tarih": "2025-01-20T16:00:00.000Z",
    "deleted_at": null
  }
}
```

---

### 3. Sağlık Kaydı Güncelleme

**Endpoint:** `PUT /api/health-records/:id`

**Authentication:** Gerekli

**Parametreler:**
- `:id` - Kayıt ID (integer)

**Request Body:**
```json
{
  "veteriner_notu": "Güncellenmiş not: İyileşme görülüyor"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "kayit_id": 6,
    "hayvan_id": 1,
    "mudahale_turu": "Tedavi",
    "veteriner_notu": "Güncellenmiş not: İyileşme görülüyor",
    "tarih": "2025-01-20T16:00:00.000Z",
    "deleted_at": null
  }
}
```

---

### 4. Sağlık Kaydı Silme (Soft Delete)

**Endpoint:** `DELETE /api/health-records/:id`

**Authentication:** Gerekli

**Parametreler:**
- `:id` - Kayıt ID (integer)

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Sağlık kaydı silindi."
}
```

---

## Admin İşlemleri

### 1. Sistem Loglarını Listeleme

**Endpoint:** `GET /api/admin/logs`

**Authentication:** Gerekli (Admin)

**Açıklama:** Sistem kullanıcı loglarını listeler.

**Query Parametreleri:**
- `limit` - Limit (varsayılan: 100)

**Request Örneği:**
```
GET /api/admin/logs?limit=50
Authorization: Bearer ADMIN_TOKEN
```

**Response (200 OK):**
```json
{
  "success": true,
  "count": 50,
  "data": [
    {
      "log_id": 150,
      "kullanici_id": 1,
      "islem_turu": "LOGIN",
      "aciklama": "Kullanıcı giriş yaptı",
      "ip_adresi": "192.168.1.100",
      "created_at": "2025-01-20T10:30:00.000Z",
      "kullanici_adi": "umut_sibara",
      "rol": "gonullu"
    }
  ]
}
```

**Hata Durumları:**
- `403`: Yönetici yetkisi yok

---

### 2. Log İstatistikleri

**Endpoint:** `GET /api/admin/logs/stats`

**Authentication:** Gerekli (Admin)

**Açıklama:** İşlem türlerine göre log istatistikleri.

**Request Örneği:**
```
GET /api/admin/logs/stats
Authorization: Bearer ADMIN_TOKEN
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "islem_turu": "LOGIN",
      "sayi": "245"
    },
    {
      "islem_turu": "REPORT_CREATE",
      "sayi": "123"
    },
    {
      "islem_turu": "FEED_CREATE",
      "sayi": "89"
    }
  ]
}
```

---

## Bağış İşlemleri

### 1. Bağışları Listeleme

**Endpoint:** `GET /api/donations`

**Açıklama:** Aktif bağış kampanyalarını listeler.

**Request Örneği:**
```
GET /api/donations
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "1 Kg Mama Bağışı",
      "description": "Sokak hayvanları için mama bağışı",
      "requiredPoints": 50,
      "category": "FOOD",
      "icon": "🍗",
      "active": true
    },
    {
      "id": 2,
      "title": "Veteriner Muayene",
      "description": "Hasta hayvan için veteriner muayenesi",
      "requiredPoints": 100,
      "category": "MEDICAL",
      "icon": "💉",
      "active": true
    }
  ]
}
```

---

### 2. Bağış Kampanyası Oluşturma

**Endpoint:** `POST /api/donations`

**Request Body:**
```json
{
  "baslik": "Barınak İnşaatı",
  "aciklama": "Hayvanlar için yeni barınak",
  "gerekli_puan": 500,
  "kategori": "SHELTER"
}
```

**Zorunlu Alanlar:**
- `baslik`
- `gerekli_puan`

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Bağış kampanyası oluşturuldu.",
  "data": {
    "bagis_id": 3,
    "baslik": "Barınak İnşaatı",
    "aciklama": "Hayvanlar için yeni barınak",
    "gerekli_puan": 500,
    "kategori": "SHELTER",
    "aktif": true,
    "olusturulma_tarihi": "2025-01-20T17:00:00.000Z"
  }
}
```

---

## Hizmet İşlemleri

### 1. Hizmetleri Listeleme

**Endpoint:** `GET /api/services`

**Açıklama:** Tüm hizmet ilanlarını listeler.

**Request Örneği:**
```
GET /api/services
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "provider": {
        "id": 5,
        "name": "ahmet_vet",
        "avatarUrl": "/uploads/photos/ahmet.webp"
      },
      "title": "Evde Bakım Hizmeti",
      "description": "Tatile giderken evcil hayvanlarınıza bakıyorum",
      "pricePerDay": 150,
      "category": "PET_SITTING",
      "rating": 4.5
    },
    {
      "id": 2,
      "provider": {
        "id": 7,
        "name": "zeynep_grooming",
        "avatarUrl": ""
      },
      "title": "Profesyonel Tıraş",
      "description": "Köpek ve kedi tıraşı",
      "pricePerDay": 200,
      "category": "GROOMING",
      "rating": 5.0
    }
  ]
}
```

---

### 2. Hizmet İlanı Oluşturma

**Endpoint:** `POST /api/services`

**Request Body:**
```json
{
  "saglayici_id": 5,
  "baslik": "Veteriner Danışmanlık",
  "aciklama": "Online veteriner danışmanlığı",
  "fiyat_gunluk": 100,
  "kategori": "VETERINARY"
}
```

**Zorunlu Alanlar:**
- `saglayici_id`
- `baslik`
- `fiyat_gunluk`

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Hizmet ilanı oluşturuldu.",
  "data": {
    "hizmet_id": 3,
    "saglayici_id": 5,
    "baslik": "Veteriner Danışmanlık",
    "aciklama": "Online veteriner danışmanlığı",
    "fiyat_gunluk": 100,
    "kategori": "VETERINARY",
    "ort_puan": 0,
    "olusturulma_tarihi": "2025-01-20T18:00:00.000Z"
  }
}
```

---

## Hata Kodları

### HTTP Status Kodları

| Kod | Açıklama | Örnek Durum |
|-----|----------|-------------|
| **200** | OK | İşlem başarılı |
| **201** | Created | Kayıt oluşturuldu |
| **400** | Bad Request | Eksik veya hatalı parametre |
| **401** | Unauthorized | Token yok veya geçersiz |
| **403** | Forbidden | Yetki yok (Admin gerekli) |
| **404** | Not Found | Kayıt bulunamadı |
| **409** | Conflict | Duplicate kayıt (e-posta, kullanıcı adı) |
| **500** | Internal Server Error | Sunucu hatası |

### Yaygın Hata Mesajları

**401 Unauthorized:**
```json
{
  "success": false,
  "error": "Erişim reddedildi. Token bulunamadı."
}
```

**403 Forbidden (Token Süresi Dolmuş):**
```json
{
  "success": false,
  "error": "Geçersiz veya süresi dolmuş token."
}
```

**403 Forbidden (Yönetici Yetkisi):**
```json
{
  "success": false,
  "error": "Bu işlem için yetkiniz yok (Yönetici yetkisi gerekir)."
}
```

**404 Not Found:**
```json
{
  "success": false,
  "error": "Kullanıcı bulunamadı."
}
```

**409 Conflict:**
```json
{
  "success": false,
  "error": "Bu kullanıcı adı veya e-posta zaten kayıtlı."
}
```

---

## Örnek Kullanım Akışları

### 1. Yeni Kullanıcı Kaydı ve İhbar Oluşturma

```javascript
// 1. Kayıt ol
const registerResponse = await fetch('http://192.168.1.4:3000/api/users/register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    kullanici_adi: 'yeni_kullanici',
    eposta: 'yeni@example.com',
    sifre: 'sifre123'
  })
});

// 2. Giriş yap ve token al
const loginResponse = await fetch('http://192.168.1.4:3000/api/users/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    eposta: 'yeni@example.com',
    sifre: 'sifre123'
  })
});
const { data: { token, id } } = await loginResponse.json();

// 3. Fotoğraf yükle
const formData = new FormData();
formData.append('photo', fileInput.files[0]);

const photoResponse = await fetch('http://192.168.1.4:3000/api/upload/photo', {
  method: 'POST',
  headers: { 'Authorization': `Bearer ${token}` },
  body: formData
});
const { data: { foto_id } } = await photoResponse.json();

// 4. İhbar oluştur
const reportResponse = await fetch('http://192.168.1.4:3000/api/reports', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    kullanici_id: id,
    baslik: 'Yaralı kedi',
    aciklama: 'Acil yardım gerekiyor',
    ihbar_turu: 'Saglik',
    kategori: 'REPORT',
    hayvan_turu: 'Kedi',
    enlem: 38.4237,
    boylam: 27.1428,
    adres: 'Alsancak, İzmir',
    foto_id: foto_id
  })
});
```

---

### 2. Liderlik Tablosunu Görüntüleme

```javascript
const response = await fetch('http://192.168.1.4:3000/api/users/leaderboard');
const leaderboard = await response.json();

console.log('Top 20 Kullanıcı:', leaderboard);
```

---

### 3. Bölge Riskine Göre Filtreleme

```javascript
// 1. Tüm bölgeleri al
const zonesResponse = await fetch('http://192.168.1.4:3000/api/zones');
const { data: zones } = await zonesResponse.json();

// 2. Her bölge için risk analizi yap
for (const zone of zones) {
  const riskResponse = await fetch(`http://192.168.1.4:3000/api/stats/risk/${zone.bolge_id}`);
  const { risk_seviyesi } = await riskResponse.json();
  
  if (risk_seviyesi === 'YUKSEK') {
    console.log(`⚠️ ${zone.bolge_adi} - YÜKSEK RİSK`);
  }
}
```

---

## Notlar ve İpuçları

### 🔐 Güvenlik
- Token'ları güvenli bir şekilde saklayın (LocalStorage veya SecureStorage)
- Token'lar 24 saat geçerlidir, süresi dolduğunda tekrar giriş yapın
- Admin endpoint'leri sadece yönetici rolüne sahip kullanıcılar erişebilir

### 📊 Performans
- Büyük listelerde pagination uygulamayı düşünün
- Konum filtreleri kullanarak gereksiz veri transferini azaltın
- Fotoğraflar otomatik olarak optimize edilir (WebP, %80 kalite)

### 🐛 Hata Yönetimi
- Tüm API yanıtlarında `success` alanını kontrol edin
- HTTP status kodlarına göre uygun hata mesajları gösterin
- Rate limiting olmadığı için dikkatli kullanın

### 📱 Mobil Uygulama İpuçları
- Token'ı her istekte header'a ekleyin
- Network hatalarını gracefully handle edin
- Offline mode için caching stratejisi uygulayın

---

## Destek ve İletişim

Herhangi bir sorunuz veya öneriniz için lütfen iletişime geçin.

**API Versiyonu:** 1.0.0  
**Son Güncelleme:** 24 Aralık 2025  
**Backend Framework:** Express.js 5.2.1  
**Database:** PostgreSQL

---

**🐾 PatiTakip ile sokak hayvanlarına daha iyi bir hayat!**

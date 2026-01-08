# 🐾 PatiTakip Backend API Dokümantasyonu

## 📋 Genel Bakış
PatiTakip Backend API - Sokak hayvanları takip sistemi için RESTful API.

**Base URL:** `http://localhost:3000/api`

---

## 🔐 Authentication
API, korumalı endpoint'ler için JWT (JSON Web Token) kullanır.

**Header formatı:**
```
Authorization: Bearer <your_jwt_token>
```

---

## 📚 API Endpoint'leri

### 1️⃣ KULLANICI YÖNETİMİ (`/api/users`)

#### Kayıt Ol
```http
POST /api/users/register
Content-Type: application/json

{
  "kullanici_adi": "umut_yildiz",
  "eposta": "umut@example.com",
  "sifre": "GucluSifre123!",
  "tam_isim": "Umut Yıldız"
}
```

#### Giriş Yap
```http
POST /api/users/login
Content-Type: application/json

{
  "eposta": "umut@example.com",
  "sifre": "GucluSifre123!"
}
```

#### Profil Görüntüle
```http
GET /api/users/:kullanici_id/profile
```

#### Liderlik Tablosu
```http
GET /api/users/leaderboard?limit=100
```

---

### 2️⃣ İHBAR/GÖNDERİ SİSTEMİ (`/api/reports`)

#### İhbarları Listele (Feed/Timeline)
```http
GET /api/reports?limit=20&offset=0&kategori=REPORT&enlem=41.0&boylam=29.0&yaricap=5
```

**Query Parametreleri:**
- `limit`: Sayfa başına kayıt sayısı (default: 20)
- `offset`: Sayfa offset (pagination)
- `kategori`: REPORT, FEEDING, ADOPTION, SERVICE, DONATION
- `enlem`, `boylam`, `yaricap`: Konum bazlı filtreleme (km)

#### Yeni İhbar Oluştur
```http
POST /api/reports
Authorization: Bearer <token>
Content-Type: application/json

{
  "kullanici_id": 1,
  "baslik": "Yaralı Kedi",
  "aciklama": "Bacağında yara var, acil yardım gerekli",
  "kategori": "REPORT",
  "ihbar_turu": "Saglik",
  "hayvan_turu": "STREET_CAT",
  "enlem": 41.0082,
  "boylam": 28.9784,
  "adres": "Kadıköy, İstanbul",
  "foto_id": 25
}
```

#### İhbar Detayı
```http
GET /api/reports/:ihbar_id
```

#### İhbar Güncelle
```http
PUT /api/reports/:ihbar_id
Authorization: Bearer <token>
```

#### İhbar Sil (Soft Delete)
```http
DELETE /api/reports/:ihbar_id
Authorization: Bearer <token>
```

---

### 3️⃣ YORUM SİSTEMİ (`/api/comments`) ⭐ YENİ

#### İhbara Yorum Ekle
```http
POST /api/reports/:ihbar_id/comments
Content-Type: application/json

{
  "kullanici_id": 1,
  "yorum_metni": "Harika bir paylaşım, teşekkürler!",
  "ust_yorum_id": null  // Cevap için üst yorum ID
}
```

#### İhbarın Yorumlarını Listele
```http
GET /api/reports/:ihbar_id/comments?limit=50&offset=0
```

#### Yoruma Cevapları Getir (Alt Yorumlar)
```http
GET /api/comments/:yorum_id/replies
```

#### Yorum Güncelle
```http
PUT /api/comments/:yorum_id
Content-Type: application/json

{
  "kullanici_id": 1,
  "yorum_metni": "Güncellenmiş yorum metni"
}
```

#### Yorum Sil
```http
DELETE /api/comments/:yorum_id
Content-Type: application/json

{
  "kullanici_id": 1
}
```

#### Kullanıcının Yorumları
```http
GET /api/users/:kullanici_id/comments?limit=20&offset=0
```

---

### 4️⃣ BEĞENİ SİSTEMİ (`/api/likes`) ⭐ YENİ

#### Beğeni Ekle/Kaldır (Toggle)
```http
POST /api/likes/toggle
Content-Type: application/json

{
  "kullanici_id": 1,
  "hedef_turu": "ihbar",  // ihbar, yorum, hayvan
  "ihbar_id": 123,         // hedef_turu'na göre doldur
  "yorum_id": null,
  "hayvan_id": null
}
```

**Yanıt:**
```json
{
  "success": true,
  "action": "liked",  // veya "unliked"
  "message": "Beğenildi",
  "begeni_sayisi": 42
}
```

#### Kullanıcının Beğendikleri
```http
GET /api/users/:kullanici_id/likes?hedef_turu=ihbar&limit=50
```

#### İçeriği Beğenenler
```http
GET /api/likes/:hedef_turu/:hedef_id/users
# Örnek: GET /api/likes/ihbar/123/users
```

#### Beğeni Kontrolü (Tek İçerik)
```http
GET /api/likes/check/:kullanici_id/:hedef_turu/:hedef_id
# Örnek: GET /api/likes/check/1/ihbar/123
```

#### Toplu Beğeni Kontrolü (Feed Optimizasyonu)
```http
POST /api/likes/check-multiple
Content-Type: application/json

{
  "kullanici_id": 1,
  "hedef_turu": "ihbar",
  "hedef_ids": [123, 124, 125, 126]
}
```

**Yanıt:**
```json
{
  "success": true,
  "data": {
    "123": true,
    "124": false,
    "125": true,
    "126": false
  }
}
```

---

### 5️⃣ CHAT/MESAJLAŞMA SİSTEMİ (`/api/chats`) ⭐ YENİ

#### Sohbet Oluştur veya Var Olanı Getir
```http
POST /api/chats/create-or-get
Content-Type: application/json

{
  "kullanici_id_1": 1,
  "kullanici_id_2": 5
}
```

#### Kullanıcının Sohbetlerini Listele
```http
GET /api/users/:kullanici_id/chats?limit=50&offset=0
```

**Yanıt:**
```json
{
  "success": true,
  "data": [
    {
      "sohbet_id": 3,
      "sohbet_turu": "bireysel",
      "goruntuleme_adi": "ahmet_yilmaz",
      "goruntuleme_resmi": "/uploads/avatars/ahmet.jpg",
      "son_mesaj": "Teşekkürler!",
      "son_mesaj_tarihi": "2026-01-08T14:20:00Z",
      "okunmamis_mesaj_sayisi": 2,
      "diger_kullanici_id": 5
    }
  ]
}
```

#### Mesaj Gönder
```http
POST /api/chats/:sohbet_id/messages
Content-Type: application/json

{
  "gonderen_id": 1,
  "mesaj_metni": "Merhaba, nasılsın?",
  "mesaj_turu": "metin",  // metin, resim, konum, dosya
  "cevaplanan_mesaj_id": null  // Opsiyonel
}
```

#### Sohbetin Mesajlarını Getir
```http
GET /api/chats/:sohbet_id/messages?kullanici_id=1&limit=50&offset=0
```

#### Mesajları Okundu İşaretle
```http
PUT /api/chats/:sohbet_id/mark-read
Content-Type: application/json

{
  "kullanici_id": 1
}
```

#### Mesaj Sil
```http
DELETE /api/messages/:mesaj_id
Content-Type: application/json

{
  "kullanici_id": 1
}
```

#### Kullanıcı Ara (Yeni Sohbet İçin)
```http
GET /api/chats/search-users?arama_metni=ahmet&current_user_id=1
```

#### Okunmamış Mesaj Sayısı (Tüm Sohbetler)
```http
GET /api/users/:kullanici_id/unread-count
```

**Yanıt:**
```json
{
  "success": true,
  "data": {
    "toplam_okunmamis": 5
  }
}
```

---

### 6️⃣ FOTOĞRAF YÜKLEME (`/api/upload`)

#### Fotoğraf Yükle
```http
POST /api/upload/photo
Authorization: Bearer <token>
Content-Type: multipart/form-data

photo: [DOSYA]
```

**Yanıt:**
```json
{
  "success": true,
  "data": {
    "foto_id": 22,
    "url": "/uploads/photos/foto-1704723456789.webp"
  }
}
```

---

### 7️⃣ İSTATİSTİKLER (`/api/stats`)

#### Dashboard İstatistikleri
```http
GET /api/stats/dashboard
```

#### Bölgesel Risk Analizi
```http
GET /api/stats/risk-analysis?bolge_id=1
```

---

## 📊 Standart Yanıt Formatı

### Başarılı Yanıt
```json
{
  "success": true,
  "message": "İşlem başarılı",
  "data": { ... },
  "pagination": {  // Varsa
    "total": 100,
    "limit": 20,
    "offset": 0,
    "hasMore": true
  }
}
```

### Hata Yanıtı
```json
{
  "success": false,
  "message": "Hata açıklaması",
  "error": "Detaylı hata mesajı"
}
```

---

## 🔒 HTTP Durum Kodları

- `200 OK` - İşlem başarılı
- `201 Created` - Kaynak oluşturuldu
- `400 Bad Request` - Geçersiz istek
- `401 Unauthorized` - Kimlik doğrulama gerekli
- `403 Forbidden` - Yetki yok
- `404 Not Found` - Kaynak bulunamadı
- `500 Internal Server Error` - Sunucu hatası

---

## 🚀 Örnek Kullanım Senaryoları

### Senaryo 1: Feed/Timeline Yükleme
```javascript
// 1. İhbarları getir
GET /api/reports?limit=20&offset=0

// 2. Her ihbar için beğeni durumunu kontrol et
POST /api/likes/check-multiple
{
  "kullanici_id": 1,
  "hedef_turu": "ihbar",
  "hedef_ids": [123, 124, 125]
}

// 3. Yorumları lazy load
GET /api/reports/123/comments?limit=5
```

### Senaryo 2: İhbar Detay Sayfası
```javascript
// 1. İhbar detayını getir
GET /api/reports/123

// 2. Yorumları getir
GET /api/reports/123/comments?limit=50

// 3. Beğeni durumunu kontrol et
GET /api/likes/check/1/ihbar/123
```

### Senaryo 3: Mesajlaşma
```javascript
// 1. Kullanıcı ara
GET /api/chats/search-users?arama_metni=ahmet&current_user_id=1

// 2. Sohbet oluştur veya getir
POST /api/chats/create-or-get
{
  "kullanici_id_1": 1,
  "kullanici_id_2": 5
}

// 3. Mesajları getir
GET /api/chats/3/messages?kullanici_id=1

// 4. Mesaj gönder
POST /api/chats/3/messages
{
  "gonderen_id": 1,
  "mesaj_metni": "Merhaba!"
}
```

---

## 🔧 Veritabanı Özellikleri

### Auto-Trigger'lar (Otomatik Güncelleme)
- ✅ Yorum eklendiğinde → İhbar yorum sayısı artırılır + Kullanıcı puanı +2
- ✅ Beğeni eklendiğinde → İçerik beğeni sayısı artırılır
- ✅ İhbar oluşturulduğunda → Kullanıcı ihbar sayısı + puanı güncellenir (+10)
- ✅ Mesaj gönderildiğinde → Sohbet son mesaj bilgisi + karşı taraf okunmamış sayısı güncellenir

### Performans Optimizasyonu
- ✅ 25+ Index (konum, kategori, tarih bazlı)
- ✅ View'ler (feed_detayli, kullanici_profil, liderlik_tablosu)
- ✅ Soft Delete (veriler silinmez, `silindi` flag'i)

---

## 📝 Notlar

- Tüm tarih/saat değerleri **ISO 8601** formatında (`2026-01-08T14:20:00Z`)
- Koordinatlar **Decimal(9,6)** formatında (örn: `41.008240`)
- Fotoğraflar **WebP** formatına otomatik dönüştürülür
- Yorum maksimum **1000 karakter**
- Mesaj metni opsiyonel (resim/konum gönderilebilir)

---

## 🎯 Yeni Eklenen Özellikler (v2.0)

⭐ **Yorum Sistemi** - İhbarlara yorum ve cevap
⭐ **Beğeni Sistemi** - Toggle like/unlike, toplu kontrol
⭐ **Chat/Mesajlaşma** - Kullanıcılar arası mesajlaşma
⭐ **Okundu/İletildi** - Mesaj durumu takibi
⭐ **Konum Bazlı Filtreleme** - Harita için yarıçap arama

---

**Son Güncelleme:** 2026-01-08  
**Versiyon:** 2.0  
**Backend:** Node.js + Express + PostgreSQL

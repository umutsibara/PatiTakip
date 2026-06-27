# 🐾 PatiTakip - Sokak Hayvanları Takip ve Yönetim Sistemi

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Backend](https://img.shields.io/badge/backend-Node.js-green.svg)
![Frontend](https://img.shields.io/badge/frontend-Android-brightgreen.svg)
![Database](https://img.shields.io/badge/database-PostgreSQL-blue.svg)
![License](https://img.shields.io/badge/license-MIT-orange.svg)

## 📋 İçindekiler

- [Proje Hakkında](#-proje-hakkında)
- [Özellikler](#-özellikler)
- [Teknoloji Stack](#-teknoloji-stack)
- [Proje Yapısı](#-proje-yapısı)
- [Kurulum](#-kurulum)
  - [Backend Kurulumu](#backend-kurulumu)
  - [Frontend Kurulumu](#frontend-kurulumu)
- [Kullanım](#-kullanım)
- [API Dokümantasyonu](#-api-dokümantasyonu)
- [Veritabanı Şeması](#-veritabanı-şeması)
- [Katkıda Bulunma](#-katkıda-bulunma)
- [Lisans](#-lisans)

---

## 🎯 Proje Hakkında

**PatiTakip**, sokak hayvanlarının takibi, beslenmesi, sağlık durumlarının izlenmesi ve acil müdahalelerin koordine edilmesi için geliştirilmiş kapsamlı bir sosyal sorumluluk platformudur. 

### 🌟 Misyon

Sokak hayvanlarına yardım etmek isteyen gönüllüleri bir araya getirerek:
- 🚨 Acil sağlık ve beslenme ihtiyaçlarının hızlıca bildirilmesini
- 📍 Konum bazlı gerçek zamanlı takibi
- 🤝 Topluluk destekli yardımlaşmayı
- 📊 Bölgesel risk analizini
- 🏆 Gamification ile kullanıcı motivasyonunu

sağlamayı amaçlar.

### 🎮 Gamification Sistemi

Uygulama, kullanıcıları motive etmek için kapsamlı bir gamification sistemi içerir:
- **Puan Sistemi**: Her katkı için puan kazanma
- **Rütbe Sistemi**: Kullanıcı seviyesi ve unvanları
- **Rozet Sistemi**: Başarılar için özel rozetler
- **Liderlik Tablosu**: En aktif gönüllülerin sıralaması

---

## ✨ Özellikler

### 🔐 Kullanıcı Yönetimi
- ✅ Kullanıcı kaydı ve girişi (JWT Authentication)
- ✅ Profil yönetimi (Avatar, konum, istatistikler)
- ✅ Rol bazlı yetkilendirme (Gönüllü / Yönetici)
- ✅ Puan ve rütbe sistemi

### 📢 İhbar ve Gönderi Sistemi
- ✅ Çoklu kategori desteği:
  - 🚨 **REPORT**: Acil ihbarlar (yaralanma, hastalık, kayıp)
  - 🍖 **FEEDING**: Besleme kayıtları
  - 🏠 **ADOPTION**: Sahiplendirme ilanları
  - 🛠️ **SERVICE**: Hizmet ilanları (pet-sitting, veteriner)
  - ❤️ **DONATION**: Bağış kampanyaları
- ✅ Harita tabanlı konum paylaşımı
- ✅ Fotoğraf yükleme (WebP optimizasyonu ile)
- ✅ Sosyal etkileşim (beğeni, yorum, paylaşım)
- ✅ Durum takibi (Açık, İşlemde, Çözüldü, Kapalı)

### 🗺️ Harita ve Konum Özellikleri
- ✅ Google Maps entegrasyonu
- ✅ Gerçek zamanlı konum takibi
- ✅ Konum bazlı filtreleme (enlem, boylam, yarıçap)
- ✅ Bölge bazlı risk analizi
- ✅ Adres geocoding/reverse geocoding

### 🏥 Sağlık Kayıtları
- ✅ Hayvan sağlık geçmişi
- ✅ Veteriner müdahale kayıtları
- ✅ Aşı ve tedavi takibi
- ✅ Sağlık durumu raporlama

### 📊 İstatistik ve Analiz
- ✅ Günlük aktivite raporları
- ✅ Bölgesel açlık durumu analizi
- ✅ Risk seviyesi hesaplama (Düşük, Orta, Yüksek)
- ✅ Kullanıcı katkı istatistikleri

### 🎯 Bağış ve Hizmet Yönetimi
- ✅ Bağış kampanyaları oluşturma
- ✅ Kategori bazlı bağışlar (Mama, Sağlık, Barınak)
- ✅ Hizmet ilanları (Pet-sitting, Eğitim, Bakım)
- ✅ Puan bazlı değerlendirme sistemi

### 🏆 Rozet ve Başarım Sistemi
- ✅ Aktivite bazlı rozet kazanma
- ✅ Liderlik tablosu
- ✅ Kullanıcı profil sayfası
- ✅ İstatistik göstergeleri

---

## 🛠️ Teknoloji Stack

### Backend

| Teknoloji | Versiyon | Açıklama |
|-----------|----------|----------|
| **Node.js** | - | JavaScript runtime |
| **Express.js** | ^5.2.1 | Web framework |
| **PostgreSQL** | - | İlişkisel veritabanı |
| **JWT** | ^9.0.3 | Kimlik doğrulama |
| **bcrypt** | ^6.0.0 | Şifre hashleme |
| **Multer** | ^2.0.2 | Dosya yükleme |
| **Sharp** | ^0.34.5 | Görsel işleme (WebP dönüşümü) |
| **Helmet** | ^8.1.0 | Güvenlik middleware |
| **CORS** | ^2.8.5 | Cross-origin resource sharing |
| **dotenv** | ^17.2.3 | Çevre değişkenleri yönetimi |
| **pg** | ^8.16.3 | PostgreSQL client |

### Frontend

| Teknoloji | Versiyon | Açıklama |
|-----------|----------|----------|
| **Android** | API 24-36 | Minimum/Target SDK |
| **Kotlin** | - | Programlama dili |
| **Retrofit** | 2.9.0 | HTTP client |
| **Gson** | 2.9.0 | JSON serialization |
| **Coroutines** | 1.7.3 | Asenkron programlama |
| **Lifecycle** | 2.6.2 | ViewModel & LiveData |
| **Glide** | 4.16.0 | Görsel yükleme |
| **Google Maps** | 18.2.0 | Harita servisi |
| **Play Services Location** | 21.1.0 | Konum servisleri |
| **Material Design** | - | UI komponenler |
| **View Binding** | - | View bağlama |

### Veritabanı

| Tablo | Açıklama |
|-------|----------|
| `kullanicilar` | Kullanıcı bilgileri, rol ve istatistikler |
| `ihbarlar` | İhbar ve gönderiler (koordinat bazlı) |
| `hayvanlar` | Hayvan kayıtları |
| `hayvan_turleri` | Hayvan türleri (Kedi, Köpek, vb.) |
| `bolgeler` | Coğrafi bölge tanımları |
| `beslemeler` | Besleme kayıtları |
| `saglik_kayitlari` | Sağlık müdahale kayıtları |
| `fotograflar` | Yüklenen fotoğraf meta verileri |
| `rozetler` | Başarım rozetleri |
| `kullanici_rozetleri` | Kullanıcı-rozet ilişkileri |
| `bagislar` | Bağış kampanyaları |
| `hizmetler` | Hizmet ilanları |
| `sistem_loglari` | Sistem logları |

---

## 📁 Proje Yapısı

```
PatiTakip/
├── backend/                    # Node.js Backend
│   ├── src/
│   │   ├── app.js             # Ana uygulama dosyası
│   │   ├── config/
│   │   │   └── db.js          # PostgreSQL bağlantısı
│   │   ├── controllers/       # İş mantığı
│   │   │   ├── reportController.js
│   │   │   ├── userController.js
│   │   │   ├── feedingController.js
│   │   │   ├── healthRecordController.js
│   │   │   ├── donationController.js
│   │   │   ├── serviceController.js
│   │   │   └── ...
│   │   ├── middleware/        # Middleware fonksiyonları
│   │   │   ├── authMiddleware.js
│   │   │   ├── loggerMiddleware.js
│   │   │   ├── uploadMiddleware.js
│   │   │   └── errorHandler.js
│   │   └── routes/           # API route tanımları
│   │       ├── reportRoutes.js
│   │       ├── userRoutes.js
│   │       ├── uploadRoutes.js
│   │       └── ...
│   ├── database/             # SQL migration dosyaları
│   │   ├── 01_tables.sql
│   │   ├── 02_indexes.sql
│   │   ├── 03_views.sql
│   │   └── ...
│   ├── uploads/              # Yüklenen dosyalar
│   │   └── photos/
│   ├── .env                  # Çevre değişkenleri (DB, JWT secret)
│   ├── package.json          # NPM bağımlılıkları
│   └── API_DOCUMENTATION.md  # API referans dokümantasyonu
│
└── frontend/                 # Android Uygulaması
    ├── app/
    │   ├── src/
    │   │   ├── main/
    │   │   │   ├── java/com/umutsibara/patitakip/
    │   │   │   │   ├── MainActivity.java
    │   │   │   │   ├── ui/           # UI katmanları
    │   │   │   │   ├── data/         # Data modelleri
    │   │   │   │   ├── network/      # API servisleri
    │   │   │   │   └── utils/        # Yardımcı sınıflar
    │   │   │   ├── res/              # Android kaynaklar
    │   │   │   └── AndroidManifest.xml
    │   └── build.gradle.kts    # Gradle build yapılandırması
    └── settings.gradle.kts
```

---

## 🚀 Kurulum

### Gereksinimler

**Backend:**
- Node.js (v16 veya üzeri)
- PostgreSQL (v12 veya üzeri)
- npm veya yarn

**Frontend:**
- Android Studio (Arctic Fox veya üzeri)
- JDK 8 veya üzeri
- Android SDK (API 24-36)
- Google Maps API Key

---

### Backend Kurulumu

#### 1. Projeyi Klonlayın
```bash
git clone https://github.com/yourusername/PatiTakip.git
cd PatiTakip/backend
```

#### 2. Bağımlılıkları Yükleyin
```bash
npm install
```

#### 3. PostgreSQL Veritabanını Oluşturun
```bash
# PostgreSQL'e bağlanın
psql -U postgres

# Veritabanını oluşturun
CREATE DATABASE patitakip;

# Kullanıcı oluşturun (opsiyonel)
CREATE USER patitakip_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE patitakip TO patitakip_user;
```

#### 4. Veritabanı Şemasını Oluşturun
```bash
# database/ klasöründeki SQL dosyalarını sırasıyla çalıştırın
psql -U postgres -d patitakip -f database/01_tables.sql
psql -U postgres -d patitakip -f database/02_indexes.sql
psql -U postgres -d patitakip -f database/03_views.sql
psql -U postgres -d patitakip -f database/04_programmability.sql
psql -U postgres -d patitakip -f database/05_security_masking.sql
psql -U postgres -d patitakip -f database/06_seed_data.sql
psql -U postgres -d patitakip -f database/07_soft_delete_migration.sql
psql -U postgres -d patitakip -f database/08_seed_zones.sql
psql -U postgres -d patitakip -f database/09_seed_animal_types.sql
psql -U postgres -d patitakip -f database/10_update_views_soft_delete.sql
psql -U postgres -d patitakip -f database/11_gamification_and_features.sql
psql -U postgres -d patitakip -f database/12_sync_schema_with_code.sql
```

#### 5. Çevre Değişkenlerini Ayarlayın
`.env` dosyasını oluşturun ve düzenleyin:

```env
# Veritabanı Ayarları
DB_HOST=localhost
DB_PORT=5432
DB_NAME=patitakip
DB_USER=postgres
DB_PASSWORD=your_password

# JWT Ayarları
JWT_SECRET=your_super_secret_jwt_key_here
JWT_EXPIRES_IN=24h

# Server Ayarları
PORT=3000
NODE_ENV=development
```

#### 6. Sunucuyu Başlatın

**Geliştirme Modu (nodemon ile):**
```bash
npm run dev
```

**Prodüksiyon Modu:**
```bash
npm start
```

Sunucu başarıyla başladığında şu çıktıyı görmelisiniz:
```
✅ PostgreSQL veritabanına bağlanıldı!
Sunucu 3000 portunda çalışıyor...
```

#### 7. API'yi Test Edin
```bash
curl http://localhost:3000/
# Beklenen yanıt: {"message":"PatiTakip API Çalışıyor! 🐾"}
```

---

### Frontend Kurulumu

#### 1. Android Studio'yu Açın
- `File > Open` ile `PatiTakip/frontend` klasörünü açın

#### 2. Google Maps API Key Ayarlayın

`local.properties` dosyasını oluşturun/düzenleyin:
```properties
sdk.dir=C\:\\Users\\YourUser\\AppData\\Local\\Android\\sdk
MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY_HERE
```

> **Not:** Google Maps API Key almak için [Google Cloud Console](https://console.cloud.google.com/) üzerinden Maps SDK for Android'i etkinleştirmeniz gerekir.

#### 3. Backend URL'ini Ayarlayın

API base URL'ini ayarlamak için `ApiClient.java` veya ilgili konfigürasyon dosyasını düzenleyin:

```java
private static final String BASE_URL = "http://YOUR_IP:3000/api/";
```

> **Dikkat:** Android Emulator kullanıyorsanız `localhost` yerine `10.0.2.2` kullanın. Fiziksel cihaz kullanıyorsanız bilgisayarınızın IP adresini kullanın.

#### 4. Gradle Sync
- Android Studio'da `File > Sync Project with Gradle Files` seçeneğini çalıştırın

#### 5. Uygulamayı Çalıştırın
- Emulator veya fiziksel cihazı seçin
- `Run > Run 'app'` (Shift+F10)

---

## 📱 Kullanım

### Kullanıcı Kaydı ve Girişi

1. **Kayıt Olma:**
   - Uygulama açıldığında "Kayıt Ol" butonuna tıklayın
   - Kullanıcı adı, e-posta ve şifre bilgilerinizi girin
   - Başarılı kayıt sonrası otomatik olarak giriş yapılır

2. **Giriş Yapma:**
   - E-posta ve şifrenizi girin
   - "Giriş Yap" butonuna tıklayın

### İhbar Oluşturma

1. Ana ekranda "+" (Floating Action Button) butonuna tıklayın
2. İhbar kategorisini seçin:
   - 🚨 İhbar (Acil Durumlar)
   - 🍖 Besleme
   - 🏠 Sahiplendirme
3. Gerekli bilgileri doldurun:
   - Başlık ve açıklama
   - Hayvan türü
   - Konum (haritadan seçin veya mevcut konumunuzu kullanın)
   - Fotoğraf (opsiyonel)
4. "Gönder" butonuna tıklayın
5. **+10 puan** kazanırsınız!

### Harita Kullanımı

1. Ana ekrandaki harita görünümünde tüm ihbarları görebilirsiniz
2. Markerlara tıklayarak detayları görün
3. Filtreleme seçenekleri:
   - Kategori bazlı filtreleme
   - Hayvan türü bazlı filtreleme
   - Konum bazlı arama (yakınımdakiler)

### Profil ve İstatistikler

1. Sağ üst köşedeki profil simgesine tıklayın
2. Görüntüleyebileceğiniz bilgiler:
   - Toplam puanınız
   - Rütbeniz ve sıralamanız
   - Kazandığınız rozetler
   - İhbar, besleme ve katkı istatistikleriniz

### Liderlik Tablosu

1. Menüden "Liderlik Tablosu" seçeneğine gidin
2. En aktif gönüllüleri ve katkılarını görün
3. Motive olun ve puanınızı artırın!

---

## 📚 API Dokümantasyonu

### Base URL
```
http://localhost:3000/api
```

### Authentication
API, korumalı endpoint'ler için JWT (JSON Web Token) kullanır.

**Header formatı:**
```
Authorization: Bearer <your_jwt_token>
```

### Temel Endpoint'ler

#### Kullanıcı İşlemleri

**Kayıt Olma**
```http
POST /users/register
Content-Type: application/json

{
  "kullanici_adi": "umut_yildiz",
  "eposta": "umut@example.com",
  "sifre": "GucluSifre123!"
}
```

**Giriş Yapma**
```http
POST /users/login
Content-Type: application/json

{
  "eposta": "umut@example.com",
  "sifre": "GucluSifre123!"
}
```

**Profil Görüntüleme**
```http
GET /users/:id/profile
```

**Liderlik Tablosu**
```http
GET /users/leaderboard
```

#### İhbar/Gönderi İşlemleri

**İhbarları Listeleme**
```http
GET /reports
GET /reports?category=REPORT
GET /reports?animal_type=STREET
GET /reports?lat=41.0&lng=29.0
```

**Yeni İhbar Oluşturma**
```http
POST /reports
Authorization: Bearer <token>
Content-Type: application/json

{
  "kullanici_id": 1,
  "baslik": "Yaralı Kedi",
  "aciklama": "Bacağında yara var",
  "kategori": "REPORT",
  "ihbar_turu": "Saglik",
  "hayvan_turu": "STREET",
  "enlem": 41.0082,
  "boylam": 28.9784,
  "adres": "Kadıköy, İstanbul",
  "foto_id": 25
}
```

**İhbarı Çözüldü İşaretle**
```http
PUT /reports/:id/resolve
Authorization: Bearer <token>
Content-Type: application/json

{
  "notlar": "Veteriner müdahale etti"
}
```

#### Fotoğraf Yükleme

```http
POST /upload/photo
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
    "url": "/uploads/photos/foto-123456789.webp"
  }
}
```

> **Detaylı API dokümantasyonu için:** [`backend/API_DOCUMENTATION.md`](./backend/API_DOCUMENTATION.md) ve [`backend/API_DOKUMANTASYONU_DETAYLI.md`](./backend/API_DOKUMANTASYONU_DETAYLI.md) dosyalarına bakınız.

---

## 🗄️ Veritabanı Şeması

### Ana Tablolar

#### kullanicilar
```sql
CREATE TABLE kullanicilar (
    kullanici_id SERIAL PRIMARY KEY,
    kullanici_adi VARCHAR(50) NOT NULL UNIQUE,
    eposta VARCHAR(100) NOT NULL UNIQUE,
    sifre_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) DEFAULT 'gonullu',
    puan INT DEFAULT 0,
    rutbe INT DEFAULT 0,
    konum VARCHAR(100),
    avatar_url TEXT,
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### ihbarlar
```sql
CREATE TABLE ihbarlar (
    ihbar_id SERIAL PRIMARY KEY,
    kullanici_id INT REFERENCES kullanicilar(kullanici_id),
    hayvan_id INT REFERENCES hayvanlar(hayvan_id),
    bolge_id INT REFERENCES bolgeler(bolge_id),
    baslik VARCHAR(200),
    aciklama TEXT NOT NULL,
    kategori VARCHAR(50),
    ihbar_turu VARCHAR(50),
    hayvan_turu VARCHAR(50),
    durum VARCHAR(20) DEFAULT 'Acik',
    enlem DECIMAL(9,6) NOT NULL,
    boylam DECIMAL(9,6) NOT NULL,
    adres VARCHAR(200),
    begeni_sayisi INT DEFAULT 0,
    yorum_sayisi INT DEFAULT 0,
    paylasim_sayisi INT DEFAULT 0,
    foto_id INT REFERENCES fotograflar(foto_id),
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### İlişkiler

- `kullanicilar` ↔ `ihbarlar` (1:N)
- `kullanicilar` ↔ `beslemeler` (1:N)
- `hayvanlar` ↔ `saglik_kayitlari` (1:N)
- `bolgeler` ↔ `ihbarlar` (1:N)
- `kullanicilar` ↔ `kullanici_rozetleri` ↔ `rozetler` (N:M)

### Indexes

Performans için optimize edilmiş indexler:
- `idx_reports_location` - Konum bazlı sorgular için
- `idx_reports_user` - Kullanıcı ihbarları için
- `idx_reports_status` - Durum filtrelemeleri için
- `idx_users_points` - Liderlik tablosu için

> **Tüm migration dosyaları için:** [`backend/database/`](./backend/database/) klasörüne bakınız.

---

## 🤝 Katkıda Bulunma

Katkılarınızı bekliyoruz! Lütfen aşağıdaki adımları izleyin:

1. Bu repository'yi fork edin
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'feat: Add amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request açın

### Commit Mesajı Formatı

Conventional Commits standardını kullanıyoruz:
- `feat:` - Yeni özellik
- `fix:` - Hata düzeltme
- `docs:` - Dokümantasyon
- `style:` - Kod formatı
- `refactor:` - Kod yeniden yapılandırma
- `test:` - Test ekleme/düzenleme
- `chore:` - Bakım işleri

---

## 📄 Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına bakınız.

---

## 👤 İletişim

**Proje Sahibi:** Muhammed Umut Sibara

- Email: umut@example.com
- GitHub: [@yourusername](https://github.com/yourusername)

---

## 🙏 Teşekkürler

Bu proje, sokak hayvanlarına yardım eden tüm gönüllülere adanmıştır. 🐾

### Kullanılan Açık Kaynak Projeler

- [Express.js](https://expressjs.com/)
- [PostgreSQL](https://www.postgresql.org/)
- [Android](https://developer.android.com/)
- [Google Maps Platform](https://developers.google.com/maps)
- [Retrofit](https://square.github.io/retrofit/)
- [Glide](https://github.com/bumptech/glide)

---

## 📸 Ekran Görüntüleri

> **Not:** Ekran görüntülerini `screenshots/` klasörüne ekleyerek README'yi zenginleştirebilirsiniz.

---

## 🔮 Gelecek Planlar

- [ ] Push notification desteği
- [ ] Offline mod
- [ ] Video yükleme desteği
- [ ] Sosyal medya entegrasyonu
- [ ] iOS uygulaması
- [ ] Web dashboard
- [ ] Çoklu dil desteği
- [ ] Dark mode

---

<div align="center">
  
**🐾 Hayvanları Seviyoruz, Onlar İçin Varız 🐾**

[![Stars](https://img.shields.io/github/stars/yourusername/PatiTakip?style=social)](https://github.com/yourusername/PatiTakip)
[![Forks](https://img.shields.io/github/forks/yourusername/PatiTakip?style=social)](https://github.com/yourusername/PatiTakip/fork)

</div>

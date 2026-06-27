-- 01_kullanicilar_tablolar.sql
-- Proje: PatiTakip
-- Amaç: Tabloların oluşturulması (Türkçe ve Konum Destekli)
-- Güncelleme: İhbarlar artık koordinat (Harita) bazlıdır.

-- 1. Kullanıcılar Tablosu
CREATE TABLE kullanicilar (
    kullanici_id SERIAL PRIMARY KEY,
    kullanici_adi VARCHAR(50) NOT NULL UNIQUE,
    eposta VARCHAR(100) NOT NULL UNIQUE,
    sifre_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) CHECK (rol IN ('yonetici', 'gonullu')) DEFAULT 'gonullu',
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Hayvan Türleri Tablosu
CREATE TABLE hayvan_turleri (
    tur_id SERIAL PRIMARY KEY,
    tur_adi VARCHAR(50) NOT NULL UNIQUE,
    aciklama TEXT
);

-- 3. Bölgeler Tablosu (Konum Merkezli Referans Noktaları)
CREATE TABLE bolgeler (
    bolge_id SERIAL PRIMARY KEY,
    bolge_adi VARCHAR(100) NOT NULL,
    sehir VARCHAR(50) DEFAULT 'Istanbul',
    ilce VARCHAR(50) NOT NULL,
    merkez_enlem DECIMAL(9,6), -- Bölgenin orta noktası
    merkez_boylam DECIMAL(9,6)
);

-- 4. Hayvanlar Tablosu
CREATE TABLE hayvanlar (
    hayvan_id SERIAL PRIMARY KEY,
    tur_id INT REFERENCES hayvan_turleri(tur_id) ON DELETE SET NULL,
    isim VARCHAR(50),
    tahmini_yas INT CHECK (tahmini_yas >= 0),
    fotograf_url TEXT,
    aciklama TEXT,
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. İhbarlar Tablosu (Harita Konum Bazlı)
CREATE TABLE ihbarlar (
    ihbar_id SERIAL PRIMARY KEY,
    kullanici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE CASCADE,
    hayvan_id INT REFERENCES hayvanlar(hayvan_id) ON DELETE SET NULL,
    -- Bölge ID opsiyonel yapıldı (Konumdan bulunabilir)
    bolge_id INT REFERENCES bolgeler(bolge_id) ON DELETE SET NULL, 
    ihbar_turu VARCHAR(50) NOT NULL CHECK (ihbar_turu IN ('Aclik', 'Saglik', 'Kayip', 'Genel')),
    aciklama TEXT NOT NULL,
    durum VARCHAR(20) DEFAULT 'Acik' CHECK (durum IN ('Acik', 'Islemde', 'Cozuldu', 'Kapali')),
    -- Harita Koordinatları (Zorunlu)
    enlem DECIMAL(9,6) NOT NULL CHECK (enlem BETWEEN -90 AND 90),
    boylam DECIMAL(9,6) NOT NULL CHECK (boylam BETWEEN -180 AND 180),
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. Sağlık Kayıtları Tablosu
CREATE TABLE saglik_kayitlari (
    kayit_id SERIAL PRIMARY KEY,
    hayvan_id INT REFERENCES hayvanlar(hayvan_id) ON DELETE CASCADE,
    mudahale_turu VARCHAR(50) NOT NULL,
    veteriner_notu TEXT,
    tarih DATE NOT NULL DEFAULT CURRENT_DATE
);

-- 7. Beslemeler Tablosu
CREATE TABLE beslemeler (
    besleme_id SERIAL PRIMARY KEY,
    kullanici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE SET NULL,
    bolge_id INT REFERENCES bolgeler(bolge_id) ON DELETE SET NULL, -- Opsiyonel olabilir
    mama_miktari_kg DECIMAL(4,2) NOT NULL CHECK (mama_miktari_kg > 0),
    mama_turu VARCHAR(50),
    besleme_zamani TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

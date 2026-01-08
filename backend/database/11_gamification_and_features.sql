-- 11_gamification_and_features.sql
-- Amaç: Gamification (Puan, Rozet), Bağışlar, Hizmetler ve Gelişmiş İhbar/Gönderi Yapısı

-- 1. Kullanıcılar Tablosunu Genişletme
-- Mevcut kullanicilar tablosuna puan, rutbe, konum ve avatar ekleniyor.
ALTER TABLE kullanicilar ADD COLUMN IF NOT EXISTS puan INT DEFAULT 0;
ALTER TABLE kullanicilar ADD COLUMN IF NOT EXISTS rutbe INT DEFAULT 0;
ALTER TABLE kullanicilar ADD COLUMN IF NOT EXISTS konum VARCHAR(255);
ALTER TABLE kullanicilar ADD COLUMN IF NOT EXISTS avatar_url TEXT;

-- 2. İhbarlar Tablosunu Genişletme (Gönderiler/Posts Yapısına Dönüşüm)
-- Mevcut ihbarlar tablosunu genel bir 'Gönderi' yapısına evriltiyoruz.
ALTER TABLE ihbarlar ADD COLUMN IF NOT EXISTS baslik VARCHAR(255);
ALTER TABLE ihbarlar ADD COLUMN IF NOT EXISTS kategori VARCHAR(50); -- 'REPORT', 'FEEDING', 'ADOPTION', 'SERVICE', 'DONATION'
ALTER TABLE ihbarlar ADD COLUMN IF NOT EXISTS hayvan_turu VARCHAR(20) DEFAULT 'STREET'; -- 'STREET', 'PET'
ALTER TABLE ihbarlar ADD COLUMN IF NOT EXISTS begeni_sayisi INT DEFAULT 0;
ALTER TABLE ihbarlar ADD COLUMN IF NOT EXISTS yorum_sayisi INT DEFAULT 0;
ALTER TABLE ihbarlar ADD COLUMN IF NOT EXISTS paylasim_sayisi INT DEFAULT 0;
ALTER TABLE ihbarlar ADD COLUMN IF NOT EXISTS adres TEXT;

-- ihbar_turu check constraint'ini kaldırıyoruz ki daha esnek olabilsin veya kategori ile yönetilsin.
-- Ancak eski verileri bozmamak için constraint'i sadece gevşetebiliriz veya DROP edebiliriz.
-- Postgres'te constraint ismi genellikle tablename_columnname_check formatındadır.
ALTER TABLE ihbarlar DROP CONSTRAINT IF EXISTS ihbarlar_ihbar_turu_check;

-- 3. Rozetler (Badges) Tablosu
CREATE TABLE IF NOT EXISTS rozetler (
    rozet_id SERIAL PRIMARY KEY,
    isim VARCHAR(100) NOT NULL,
    ikon VARCHAR(255),
    aciklama TEXT,
    gerekli_puan INT DEFAULT 0
);

-- 4. Kullanıcı Rozetleri (UserBadges) Tablosu
CREATE TABLE IF NOT EXISTS kullanici_rozetleri (
    id SERIAL PRIMARY KEY,
    kullanici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE CASCADE,
    rozet_id INT REFERENCES rozetler(rozet_id) ON DELETE CASCADE,
    kazanilma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(kullanici_id, rozet_id)
);

-- 5. Bağışlar (Donations) Tablosu
CREATE TABLE IF NOT EXISTS bagislar (
    bagis_id SERIAL PRIMARY KEY,
    baslik VARCHAR(255) NOT NULL,
    aciklama TEXT,
    gerekli_puan INT,
    kategori VARCHAR(50) CHECK (kategori IN ('FOOD', 'MEDICAL', 'SHELTER')),
    ikon VARCHAR(255),
    aktif BOOLEAN DEFAULT TRUE,
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. Hizmetler (Services) Tablosu
CREATE TABLE IF NOT EXISTS hizmetler (
    hizmet_id SERIAL PRIMARY KEY,
    saglayici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE CASCADE,
    baslik VARCHAR(255) NOT NULL,
    aciklama TEXT,
    fiyat_gunluk DECIMAL(10, 2),
    kategori VARCHAR(50) CHECK (kategori IN ('PET_SITTING', 'TRAINING', 'GROOMING')),
    ort_puan DECIMAL(3, 2) DEFAULT 0,
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

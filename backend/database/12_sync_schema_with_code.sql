-- 12_sync_schema_with_code.sql
-- Amaç: Backend kodunda kullanılan ancak veritabanı şemalarında eksik olan tabloları ve sütunları oluşturmak.
-- Bu dosya, kodun hatasız çalışması için gereklidir.

-- 1. Kullanıcı Logları Tablosu (loggerMiddleware.js için)
-- Eğer tablo zaten varsa hata vermez (IF NOT EXISTS)
CREATE TABLE IF NOT EXISTS kullanici_loglari (
    log_id SERIAL PRIMARY KEY,
    kullanici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE SET NULL,
    islem_turu VARCHAR(50), -- GORUNTULEME, EKLEME, GUNCELLEME, SILME, GIRIS
    aciklama TEXT,
    ip_adresi VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Fotoğraflar Tablosu (uploadMiddleware.js ve reportController.js için)
-- Eğer tablo zaten varsa hata vermez (IF NOT EXISTS)
CREATE TABLE IF NOT EXISTS fotograflar (
    foto_id SERIAL PRIMARY KEY,
    kullanici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE SET NULL,
    dosya_adi VARCHAR(255) NOT NULL,
    dosya_yolu VARCHAR(500) NOT NULL,
    dosya_boyutu INT, -- Byte cinsinden
    genislik INT,
    yukseklik INT,
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL -- Soft delete desteği
);

-- 3. İhbarlar Tablosuna Fotoğraf ID Sütunu Ekleme (reportController.js için)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'ihbarlar' AND column_name = 'foto_id') THEN
        ALTER TABLE ihbarlar ADD COLUMN foto_id INT REFERENCES fotograflar(foto_id) ON DELETE SET NULL;
    END IF;
    
    -- Hayvan türü alanını genişlet (Diğer seçeneği için)
    ALTER TABLE ihbarlar ALTER COLUMN hayvan_turu TYPE VARCHAR(50);

    -- Hayvan Türleri tablosundan deleted_at sütununu kaldır (İstek üzerine)
    -- Önce bağımlı view'ı kaldırıyoruz (CASCADE ile varsa buna bağlı diğer objeler de silinir)
    DROP VIEW IF EXISTS vi_hayvan_detay CASCADE;
    
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'hayvan_turleri' AND column_name = 'deleted_at') THEN
        ALTER TABLE hayvan_turleri DROP COLUMN deleted_at;
    END IF;
END $$;

-- Bildirim
DO $$
BEGIN
    RAISE NOTICE 'Eksik tablolar (kullanici_loglari, fotograflar) ve sütunlar (ihbarlar.foto_id) kontrol edildi ve eksikler tamamlandı.';
END $$;

-- ============================================================================
-- 🐾 PatiTakip - Tam PostgreSQL Veritabanı Şeması
-- ============================================================================
-- Proje: Sokak Hayvanları Takip ve Yönetim Sistemi
-- Versiyon: 2.0
-- Tarih: 2026-01-08
-- Yazar: Muhammed Umut Sibara
-- ============================================================================
-- Özellikler:
-- ✅ Kullanıcı Yönetimi (JWT Auth)
-- ✅ İhbar/Gönderi Sistemi (Timeline/Feed)
-- ✅ Yorum Sistemi
-- ✅ Beğeni İnteraksiyon Sistemi
-- ✅ Chat/Mesajlaşma Sistemi
-- ✅ Harita ve Konum Desteği
-- ✅ Gamification (Puan, Rozet, Rütbe)
-- ✅ Fotoğraf Yönetimi
-- ============================================================================

-- Veritabanını oluştur (pgAdmin'de manuel oluşturun, Query Tool'da çalışmaz)
-- CREATE DATABASE "PatiTakipDB";

-- Veritabanına bağlan (zaten -d parametresiyle bağlanıyorsunuz, bu satır gereksiz)
-- \c PatiTakipDB;

-- ============================================================================
-- TABLOLARI OLUŞTUR
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. KULLANICILAR TABLOSU
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS kullanicilar (
    kullanici_id SERIAL PRIMARY KEY,
    kullanici_adi VARCHAR(50) NOT NULL UNIQUE,
    eposta VARCHAR(100) NOT NULL UNIQUE,
    sifre_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) CHECK (rol IN ('gonullu', 'yonetici')) DEFAULT 'gonullu',
    
    -- Profil Bilgileri
    tam_isim VARCHAR(100),
    telefon VARCHAR(20),
    avatar_url TEXT,
    bio TEXT,
    konum VARCHAR(100),
    sehir VARCHAR(50) DEFAULT 'Istanbul',
    
    -- Gamification
    puan INT DEFAULT 0,
    rutbe INT DEFAULT 0,
    rozet_sayisi INT DEFAULT 0,
    
    -- İstatistikler
    toplam_ihbar_sayisi INT DEFAULT 0,
    toplam_besleme_sayisi INT DEFAULT 0,
    toplam_yorum_sayisi INT DEFAULT 0,
    toplam_begeni_sayisi INT DEFAULT 0,
    
    -- Durum
    aktif BOOLEAN DEFAULT TRUE,
    son_giris_tarihi TIMESTAMP,
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    guncelleme_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 2. HAYVAN TÜRLERİ TABLOSU
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS hayvan_turleri (
    tur_id SERIAL PRIMARY KEY,
    tur_adi VARCHAR(50) NOT NULL UNIQUE,
    tur_kodu VARCHAR(20) NOT NULL UNIQUE, -- 'STREET_CAT', 'STREET_DOG', etc.
    aciklama TEXT,
    ikon_url TEXT,
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 3. BÖLGELER TABLOSU
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bolgeler (
    bolge_id SERIAL PRIMARY KEY,
    bolge_adi VARCHAR(100) NOT NULL,
    sehir VARCHAR(50) DEFAULT 'Istanbul',
    ilce VARCHAR(50) NOT NULL,
    merkez_enlem DECIMAL(9,6),
    merkez_boylam DECIMAL(9,6),
    yaricap_metre INT DEFAULT 5000,
    aktif BOOLEAN DEFAULT TRUE,
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 4. HAYVANLAR TABLOSU
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS hayvanlar (
    hayvan_id SERIAL PRIMARY KEY,
    tur_id INT REFERENCES hayvan_turleri(tur_id) ON DELETE SET NULL,
    isim VARCHAR(50),
    tahmini_yas INT CHECK (tahmini_yas >= 0),
    cinsiyet VARCHAR(10) CHECK (cinsiyet IN ('Erkek', 'Disi', 'Bilinmiyor')),
    renk VARCHAR(50),
    ozel_isaretler TEXT,
    fotograf_url TEXT,
    aciklama TEXT,
    durum VARCHAR(20) CHECK (durum IN ('Sokakta', 'Sahiplendi', 'Kayip', 'Veterinerde')) DEFAULT 'Sokakta',
    
    -- Konum Bilgisi
    son_gorulme_enlem DECIMAL(9,6),
    son_gorulme_boylam DECIMAL(9,6),
    son_gorulme_adres VARCHAR(200),
    son_gorulme_tarihi TIMESTAMP,
    
    -- İstatistikler
    goruntuleme_sayisi INT DEFAULT 0,
    begeni_sayisi INT DEFAULT 0,
    
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    guncelleme_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 5. FOTOGRAFLAR TABLOSU
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS fotograflar (
    foto_id SERIAL PRIMARY KEY,
    yukleyen_kullanici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE CASCADE,
    dosya_adi VARCHAR(255) NOT NULL,
    dosya_yolu TEXT NOT NULL,
    dosya_boyutu_kb INT,
    genislik INT,
    yukseklik INT,
    format VARCHAR(10) DEFAULT 'webp',
    aciklama TEXT,
    etiketler TEXT[], -- Array of tags
    yuklenme_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 6. İHBARLAR/GÖNDERİLER TABLOSU (Ana Feed/Timeline)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ihbarlar (
    ihbar_id SERIAL PRIMARY KEY,
    kullanici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE CASCADE,
    hayvan_id INT REFERENCES hayvanlar(hayvan_id) ON DELETE SET NULL,
    bolge_id INT REFERENCES bolgeler(bolge_id) ON DELETE SET NULL,
    foto_id INT REFERENCES fotograflar(foto_id) ON DELETE SET NULL,
    
    -- İçerik Bilgileri
    baslik VARCHAR(200),
    aciklama TEXT NOT NULL,
    
    -- Kategoriler (Ekran görüntüsünde görülen)
    kategori VARCHAR(50) NOT NULL CHECK (kategori IN ('REPORT', 'FEEDING', 'ADOPTION', 'SERVICE', 'DONATION', 'GENERAL')),
    ihbar_turu VARCHAR(50) CHECK (ihbar_turu IN ('Aclik', 'Saglik', 'Kayip', 'Genel', 'Sahiplendirme')),
    
    -- Hayvan Bilgisi
    hayvan_turu VARCHAR(50), -- 'STREET_CAT', 'STREET_DOG', etc.
    hayvan_cinsiyet VARCHAR(10),
    hayvan_yas_tahmini INT,
    
    -- Konum Bilgileri (Harita için zorunlu)
    enlem DECIMAL(9,6) NOT NULL CHECK (enlem BETWEEN -90 AND 90),
    boylam DECIMAL(9,6) NOT NULL CHECK (boylam BETWEEN -180 AND 180),
    adres VARCHAR(200),
    
    -- Durum
    durum VARCHAR(20) DEFAULT 'Acik' CHECK (durum IN ('Acik', 'Islemde', 'Cozuldu', 'Kapali')),
    oncelik VARCHAR(10) CHECK (oncelik IN ('Dusuk', 'Orta', 'Yuksek', 'Acil')) DEFAULT 'Orta',
    
    -- Sosyal Etkileşim Sayaçları
    begeni_sayisi INT DEFAULT 0,
    yorum_sayisi INT DEFAULT 0,
    paylasim_sayisi INT DEFAULT 0,
    goruntuleme_sayisi INT DEFAULT 0,
    
    -- Zaman Bilgileri
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    guncelleme_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cozum_tarihi TIMESTAMP,
    
    -- Soft Delete
    silindi BOOLEAN DEFAULT FALSE,
    silinme_tarihi TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 7. YORUMLAR TABLOSU (Yeni!)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS yorumlar (
    yorum_id SERIAL PRIMARY KEY,
    ihbar_id INT REFERENCES ihbarlar(ihbar_id) ON DELETE CASCADE,
    kullanici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE CASCADE,
    ust_yorum_id INT REFERENCES yorumlar(yorum_id) ON DELETE CASCADE, -- Cevap için
    
    -- İçerik
    yorum_metni TEXT NOT NULL,
    
    -- Etkileşim
    begeni_sayisi INT DEFAULT 0,
    
    -- Durum
    onaylandi BOOLEAN DEFAULT TRUE,
    silindi BOOLEAN DEFAULT FALSE,
    
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    guncelleme_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT yorum_uzunluk CHECK (LENGTH(yorum_metni) > 0 AND LENGTH(yorum_metni) <= 1000)
);

-- ----------------------------------------------------------------------------
-- 8. KULLANICI BEĞENİLER TABLOSU (Yeni!)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS kullanici_begeniler (
    begeni_id SERIAL PRIMARY KEY,
    kullanici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE CASCADE,
    
    -- Beğenilen içerik türü
    hedef_turu VARCHAR(20) NOT NULL CHECK (hedef_turu IN ('ihbar', 'yorum', 'hayvan')),
    ihbar_id INT REFERENCES ihbarlar(ihbar_id) ON DELETE CASCADE,
    yorum_id INT REFERENCES yorumlar(yorum_id) ON DELETE CASCADE,
    hayvan_id INT REFERENCES hayvanlar(hayvan_id) ON DELETE CASCADE,
    
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Bir kullanıcı aynı içeriği sadece bir kez beğenebilir
    CONSTRAINT unique_user_like UNIQUE (kullanici_id, hedef_turu, ihbar_id, yorum_id, hayvan_id),
    
    -- En az birisi dolu olmalı
    CONSTRAINT check_target_exists CHECK (
        (hedef_turu = 'ihbar' AND ihbar_id IS NOT NULL) OR
        (hedef_turu = 'yorum' AND yorum_id IS NOT NULL) OR
        (hedef_turu = 'hayvan' AND hayvan_id IS NOT NULL)
    )
);

-- ----------------------------------------------------------------------------
-- 9. SOHBETLER TABLOSU (Chat - Yeni!)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sohbetler (
    sohbet_id SERIAL PRIMARY KEY,
    
    -- İki kullanıcı arasında veya grup
    sohbet_turu VARCHAR(20) CHECK (sohbet_turu IN ('bireysel', 'grup')) DEFAULT 'bireysel',
    sohbet_adi VARCHAR(100), -- Grup sohbetleri için
    sohbet_resmi TEXT, -- Grup resmi
    
    -- Son mesaj bilgisi (performans için)
    son_mesaj TEXT,
    son_mesaj_tarihi TIMESTAMP,
    son_mesaj_gonderen_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE SET NULL,
    
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    aktif BOOLEAN DEFAULT TRUE
);

-- ----------------------------------------------------------------------------
-- 10. SOHBET KATILIMCILAR TABLOSU
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sohbet_katilimcilar (
    katilimci_id SERIAL PRIMARY KEY,
    sohbet_id INT REFERENCES sohbetler(sohbet_id) ON DELETE CASCADE,
    kullanici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE CASCADE,
    
    -- Durum
    katilim_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    son_goruntuleme_tarihi TIMESTAMP,
    okunmamis_mesaj_sayisi INT DEFAULT 0,
    bildirimler_acik BOOLEAN DEFAULT TRUE,
    ayrıldi BOOLEAN DEFAULT FALSE,
    
    CONSTRAINT unique_chat_participant UNIQUE (sohbet_id, kullanici_id)
);

-- ----------------------------------------------------------------------------
-- 11. MESAJLAR TABLOSU
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS mesajlar (
    mesaj_id SERIAL PRIMARY KEY,
    sohbet_id INT REFERENCES sohbetler(sohbet_id) ON DELETE CASCADE,
    gonderen_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE CASCADE,
    
    -- İçerik
    mesaj_metni TEXT,
    mesaj_turu VARCHAR(20) CHECK (mesaj_turu IN ('metin', 'resim', 'konum', 'dosya')) DEFAULT 'metin',
    dosya_url TEXT, -- Resim/dosya için
    konum_enlem DECIMAL(9,6),
    konum_boylam DECIMAL(9,6),
    
    -- Cevap
    cevaplanan_mesaj_id INT REFERENCES mesajlar(mesaj_id) ON DELETE SET NULL,
    
    -- Durum
    gonderildi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    iletildi TIMESTAMP,
    okundu TIMESTAMP,
    silindi BOOLEAN DEFAULT FALSE,
    
    CONSTRAINT mesaj_iceriği_gecerli CHECK (
        mesaj_metni IS NOT NULL OR dosya_url IS NOT NULL OR 
        (konum_enlem IS NOT NULL AND konum_boylam IS NOT NULL)
    )
);

-- ----------------------------------------------------------------------------
-- 12. BESLEMELER TABLOSU
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS beslemeler (
    besleme_id SERIAL PRIMARY KEY,
    kullanici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE SET NULL,
    bolge_id INT REFERENCES bolgeler(bolge_id) ON DELETE SET NULL,
    ihbar_id INT REFERENCES ihbarlar(ihbar_id) ON DELETE CASCADE, -- Feed'de göstermek için
    
    mama_miktari_kg DECIMAL(5,2) NOT NULL CHECK (mama_miktari_kg > 0),
    mama_turu VARCHAR(50) CHECK (mama_turu IN ('Kuru Mama', 'Yas Mama', 'Ev Yemegi', 'Diger')),
    
    -- Konum
    enlem DECIMAL(9,6),
    boylam DECIMAL(9,6),
    adres VARCHAR(200),
    
    besleme_zamani TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notlar TEXT,
    
    foto_id INT REFERENCES fotograflar(foto_id) ON DELETE SET NULL
);

-- ----------------------------------------------------------------------------
-- 13. SAĞLIK KAYITLARI TABLOSU
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS saglik_kayitlari (
    kayit_id SERIAL PRIMARY KEY,
    hayvan_id INT REFERENCES hayvanlar(hayvan_id) ON DELETE CASCADE,
    veteriner_kullanici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE SET NULL,
    
    mudahale_turu VARCHAR(50) NOT NULL CHECK (mudahale_turu IN ('Asi', 'Tedavi', 'Ameliyat', 'Kontrol', 'Kisirlaştirma', 'Diger')),
    tani TEXT,
    ilaclar TEXT,
    veteriner_notu TEXT,
    
    tarih DATE NOT NULL DEFAULT CURRENT_DATE,
    sonraki_kontrol_tarihi DATE,
    maliyet DECIMAL(10,2),
    
    foto_id INT REFERENCES fotograflar(foto_id) ON DELETE SET NULL,
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 14. ROZETLER TABLOSU (Gamification)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS rozetler (
    rozet_id SERIAL PRIMARY KEY,
    rozet_adi VARCHAR(100) NOT NULL UNIQUE,
    rozet_kodu VARCHAR(50) NOT NULL UNIQUE,
    aciklama TEXT,
    ikon_url TEXT,
    gerekli_puan INT DEFAULT 0,
    gerekli_aktivite_sayisi INT,
    rozet_turu VARCHAR(30) CHECK (rozet_turu IN ('ihbar', 'besleme', 'yorum', 'ozel', 'rutbe')),
    renk VARCHAR(20),
    aktif BOOLEAN DEFAULT TRUE,
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 15. KULLANICI ROZETLERİ TABLOSU
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS kullanici_rozetleri (
    kullanici_rozet_id SERIAL PRIMARY KEY,
    kullanici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE CASCADE,
    rozet_id INT REFERENCES rozetler(rozet_id) ON DELETE CASCADE,
    kazanilma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT unique_user_badge UNIQUE (kullanici_id, rozet_id)
);

-- ----------------------------------------------------------------------------
-- 16. PUAN GEÇMİŞİ TABLOSU
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS puan_gecmisi (
    puan_kayit_id SERIAL PRIMARY KEY,
    kullanici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE CASCADE,
    puan_miktari INT NOT NULL,
    islem_turu VARCHAR(50) NOT NULL CHECK (islem_turu IN ('ihbar_olustur', 'besleme_yap', 'yorum_yap', 'begeni_al', 'rozet_kazan', 'diger')),
    aciklama TEXT,
    iliskili_ihbar_id INT REFERENCES ihbarlar(ihbar_id) ON DELETE SET NULL,
    iliskili_yorum_id INT REFERENCES yorumlar(yorum_id) ON DELETE SET NULL,
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 17. SİSTEM LOGLARI TABLOSU
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sistem_loglari (
    log_id SERIAL PRIMARY KEY,
    kullanici_id INT REFERENCES kullanicilar(kullanici_id) ON DELETE SET NULL,
    islem_turu VARCHAR(50) NOT NULL,
    endpoint VARCHAR(200),
    http_metodu VARCHAR(10),
    ip_adresi VARCHAR(50),
    user_agent TEXT,
    durum_kodu INT,
    hata_detayi TEXT,
    islem_suresi_ms INT,
    olusturulma_tarihi TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- İNDEKSLER (Performans İyileştirme)
-- ============================================================================

-- Kullanıcılar
CREATE INDEX idx_kullanicilar_eposta ON kullanicilar(eposta);
CREATE INDEX idx_kullanicilar_kullanici_adi ON kullanicilar(kullanici_adi);
CREATE INDEX idx_kullanicilar_puan ON kullanicilar(puan DESC);
CREATE INDEX idx_kullanicilar_aktif ON kullanicilar(aktif);

-- İhbarlar (En önemli tablo - çok sorgu)
CREATE INDEX idx_ihbarlar_kullanici ON ihbarlar(kullanici_id);
CREATE INDEX idx_ihbarlar_kategori ON ihbarlar(kategori);
CREATE INDEX idx_ihbarlar_durum ON ihbarlar(durum);
CREATE INDEX idx_ihbarlar_tarih ON ihbarlar(olusturulma_tarihi DESC);
CREATE INDEX idx_ihbarlar_konum ON ihbarlar(enlem, boylam);
CREATE INDEX idx_ihbarlar_silindi ON ihbarlar(silindi);
CREATE INDEX idx_ihbarlar_hayvan_turu ON ihbarlar(hayvan_turu);

-- Composite index (Feed için optimizasyon)
CREATE INDEX idx_ihbarlar_feed ON ihbarlar(silindi, durum, olusturulma_tarihi DESC);
CREATE INDEX idx_ihbarlar_category_feed ON ihbarlar(kategori, silindi, olusturulma_tarihi DESC);

-- Yorumlar
CREATE INDEX idx_yorumlar_ihbar ON yorumlar(ihbar_id);
CREATE INDEX idx_yorumlar_kullanici ON yorumlar(kullanici_id);
CREATE INDEX idx_yorumlar_tarih ON yorumlar(olusturulma_tarihi DESC);
CREATE INDEX idx_yorumlar_ust_yorum ON yorumlar(ust_yorum_id);

-- Beğeniler
CREATE INDEX idx_begeniler_kullanici ON kullanici_begeniler(kullanici_id);
CREATE INDEX idx_begeniler_ihbar ON kullanici_begeniler(ihbar_id);
CREATE INDEX idx_begeniler_yorum ON kullanici_begeniler(yorum_id);

-- Mesajlar
CREATE INDEX idx_mesajlar_sohbet ON mesajlar(sohbet_id, gonderildi DESC);
CREATE INDEX idx_mesajlar_gonderen ON mesajlar(gonderen_id);
CREATE INDEX idx_mesajlar_tarih ON mesajlar(gonderildi DESC);

-- Sohbetler
CREATE INDEX idx_sohbet_katilimcilar_kullanici ON sohbet_katilimcilar(kullanici_id);
CREATE INDEX idx_sohbet_katilimcilar_sohbet ON sohbet_katilimcilar(sohbet_id);

-- Hayvanlar
CREATE INDEX idx_hayvanlar_tur ON hayvanlar(tur_id);
CREATE INDEX idx_hayvanlar_durum ON hayvanlar(durum);

-- Fotograflar
CREATE INDEX idx_fotograflar_yukleyen ON fotograflar(yukleyen_kullanici_id);

-- ============================================================================
-- TRIGGER FONKSIYONLARI VE TRIGGERLAR
-- ============================================================================

-- 1. Güncelleme Tarihi Otomatik Güncelleme
CREATE OR REPLACE FUNCTION guncelleme_tarihi_trigger()
RETURNS TRIGGER AS $$
BEGIN
    NEW.guncelleme_tarihi = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_kullanicilar_guncelleme
    BEFORE UPDATE ON kullanicilar
    FOR EACH ROW
    EXECUTE FUNCTION guncelleme_tarihi_trigger();

CREATE TRIGGER trg_ihbarlar_guncelleme
    BEFORE UPDATE ON ihbarlar
    FOR EACH ROW
    EXECUTE FUNCTION guncelleme_tarihi_trigger();

CREATE TRIGGER trg_hayvanlar_guncelleme
    BEFORE UPDATE ON hayvanlar
    FOR EACH ROW
    EXECUTE FUNCTION guncelleme_tarihi_trigger();

-- 2. İhbar Oluşturulduğunda Kullanıcı İstatistiklerini Güncelle
CREATE OR REPLACE FUNCTION ihbar_istatistik_guncelle()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE kullanicilar 
        SET toplam_ihbar_sayisi = toplam_ihbar_sayisi + 1,
            puan = puan + 10
        WHERE kullanici_id = NEW.kullanici_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_ihbar_olustur_istatistik
    AFTER INSERT ON ihbarlar
    FOR EACH ROW
    EXECUTE FUNCTION ihbar_istatistik_guncelle();

-- 3. Yorum Yapıldığında Sayaçları Güncelle
CREATE OR REPLACE FUNCTION yorum_sayac_guncelle()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        -- İhbar yorum sayısını artır
        UPDATE ihbarlar 
        SET yorum_sayisi = yorum_sayisi + 1
        WHERE ihbar_id = NEW.ihbar_id;
        
        -- Kullanıcı istatistik
        UPDATE kullanicilar 
        SET toplam_yorum_sayisi = toplam_yorum_sayisi + 1,
            puan = puan + 2
        WHERE kullanici_id = NEW.kullanici_id;
        
    ELSIF TG_OP = 'DELETE' THEN
        -- İhbar yorum sayısını azalt
        UPDATE ihbarlar 
        SET yorum_sayisi = yorum_sayisi - 1
        WHERE ihbar_id = OLD.ihbar_id;
        
        -- Kullanıcı istatistik
        UPDATE kullanicilar 
        SET toplam_yorum_sayisi = GREATEST(0, toplam_yorum_sayisi - 1)
        WHERE kullanici_id = OLD.kullanici_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_yorum_sayac
    AFTER INSERT OR DELETE ON yorumlar
    FOR EACH ROW
    EXECUTE FUNCTION yorum_sayac_guncelle();

-- 4. Beğeni İşlemlerinde Sayaçları Güncelle
CREATE OR REPLACE FUNCTION begeni_sayac_guncelle()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        -- İhbar beğenisi
        IF NEW.hedef_turu = 'ihbar' THEN
            UPDATE ihbarlar SET begeni_sayisi = begeni_sayisi + 1 WHERE ihbar_id = NEW.ihbar_id;
        -- Yorum beğenisi
        ELSIF NEW.hedef_turu = 'yorum' THEN
            UPDATE yorumlar SET begeni_sayisi = begeni_sayisi + 1 WHERE yorum_id = NEW.yorum_id;
        -- Hayvan beğenisi
        ELSIF NEW.hedef_turu = 'hayvan' THEN
            UPDATE hayvanlar SET begeni_sayisi = begeni_sayisi + 1 WHERE hayvan_id = NEW.hayvan_id;
        END IF;
        
    ELSIF TG_OP = 'DELETE' THEN
        -- Beğeni geri çekildi
        IF OLD.hedef_turu = 'ihbar' THEN
            UPDATE ihbarlar SET begeni_sayisi = GREATEST(0, begeni_sayisi - 1) WHERE ihbar_id = OLD.ihbar_id;
        ELSIF OLD.hedef_turu = 'yorum' THEN
            UPDATE yorumlar SET begeni_sayisi = GREATEST(0, begeni_sayisi - 1) WHERE yorum_id = OLD.yorum_id;
        ELSIF OLD.hedef_turu = 'hayvan' THEN
            UPDATE hayvanlar SET begeni_sayisi = GREATEST(0, begeni_sayisi - 1) WHERE hayvan_id = OLD.hayvan_id;
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_begeni_sayac
    AFTER INSERT OR DELETE ON kullanici_begeniler
    FOR EACH ROW
    EXECUTE FUNCTION begeni_sayac_guncelle();

-- 5. Mesaj Gönderildiğinde Sohbet Güncelle
CREATE OR REPLACE FUNCTION mesaj_sohbet_guncelle()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        -- Sohbet son mesaj bilgisini güncelle
        UPDATE sohbetler 
        SET son_mesaj = SUBSTRING(NEW.mesaj_metni, 1, 100),
            son_mesaj_tarihi = NEW.gonderildi,
            son_mesaj_gonderen_id = NEW.gonderen_id
        WHERE sohbet_id = NEW.sohbet_id;
        
        -- Diğer katılımcıların okunmamış mesaj sayısını artır
        UPDATE sohbet_katilimcilar
        SET okunmamis_mesaj_sayisi = okunmamis_mesaj_sayisi + 1
        WHERE sohbet_id = NEW.sohbet_id 
        AND kullanici_id != NEW.gonderen_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_mesaj_sohbet
    AFTER INSERT ON mesajlar
    FOR EACH ROW
    EXECUTE FUNCTION mesaj_sohbet_guncelle();

-- ============================================================================
-- VIEW'LER (Sık Kullanılan Sorgular İçin)
-- ============================================================================

-- 1. Feed/Timeline View (Tüm bilgilerle)
CREATE OR REPLACE VIEW v_feed_detayli AS
SELECT 
    i.ihbar_id,
    i.baslik,
    i.aciklama,
    i.kategori,
    i.ihbar_turu,
    i.hayvan_turu,
    i.durum,
    i.enlem,
    i.boylam,
    i.adres,
    i.begeni_sayisi,
    i.yorum_sayisi,
    i.paylasim_sayisi,
    i.goruntuleme_sayisi,
    i.olusturulma_tarihi,
    
    -- Kullanıcı bilgisi
    k.kullanici_id,
    k.kullanici_adi,
    k.tam_isim,
    k.avatar_url,
    k.rutbe,
    
    -- Fotoğraf bilgisi
    f.foto_id,
    f.dosya_yolu as fotograf_url,
    
    -- Hayvan bilgisi (varsa)
    h.hayvan_id,
    h.isim as hayvan_isim,
    h.tahmini_yas as hayvan_yas
FROM ihbarlar i
LEFT JOIN kullanicilar k ON i.kullanici_id = k.kullanici_id
LEFT JOIN fotograflar f ON i.foto_id = f.foto_id
LEFT JOIN hayvanlar h ON i.hayvan_id = h.hayvan_id
WHERE i.silindi = FALSE
ORDER BY i.olusturulma_tarihi DESC;

-- 2. Kullanıcı Profil Özeti
CREATE OR REPLACE VIEW v_kullanici_profil AS
SELECT 
    k.kullanici_id,
    k.kullanici_adi,
    k.tam_isim,
    k.eposta,
    k.avatar_url,
    k.bio,
    k.konum,
    k.puan,
    k.rutbe,
    k.rozet_sayisi,
    k.toplam_ihbar_sayisi,
    k.toplam_besleme_sayisi,
    k.toplam_yorum_sayisi,
    k.olusturulma_tarihi,
    
    -- Liderlik sırası
    RANK() OVER (ORDER BY k.puan DESC) as liderlik_sirasi,
    
    -- Son aktivite
    (SELECT MAX(olusturulma_tarihi) FROM ihbarlar WHERE kullanici_id = k.kullanici_id) as son_ihbar_tarihi
FROM kullanicilar k
WHERE k.aktif = TRUE;

-- 3. Liderlik Tablosu (Top 100)
CREATE OR REPLACE VIEW v_liderlik_tablosu AS
SELECT 
    k.kullanici_id,
    k.kullanici_adi,
    k.tam_isim,
    k.avatar_url,
    k.puan,
    k.rutbe,
    k.rozet_sayisi,
    k.toplam_ihbar_sayisi,
    k.toplam_besleme_sayisi,
    RANK() OVER (ORDER BY k.puan DESC) as sira
FROM kullanicilar k
WHERE k.aktif = TRUE
ORDER BY k.puan DESC
LIMIT 100;

-- ============================================================================
-- SEED DATA (Başlangıç Verileri)
-- ============================================================================

-- Hayvan Türleri
INSERT INTO hayvan_turleri (tur_adi, tur_kodu, aciklama) VALUES
('Sokak Kedisi', 'STREET_CAT', 'Sokakta yaşayan kediler'),
('Sokak Köpeği', 'STREET_DOG', 'Sokakta yaşayan köpekler'),
('Kuş', 'BIRD', 'Yaralı veya sahipsiz kuşlar'),
('Diğer', 'OTHER', 'Diğer hayvan türleri')
ON CONFLICT (tur_kodu) DO NOTHING;

-- Bölgeler (İstanbul örneği)
INSERT INTO bolgeler (bolge_adi, sehir, ilce, merkez_enlem, merkez_boylam) VALUES
('Kadıköy Merkez', 'Istanbul', 'Kadıköy', 40.9829, 29.0337),
('Beşiktaş Merkez', 'Istanbul', 'Beşiktaş', 41.0422, 29.0097),
('Üsküdar Merkez', 'Istanbul', 'Üsküdar', 41.0221, 29.0148),
('Şişli Merkez', 'Istanbul', 'Şişli', 41.0602, 28.9887),
('Bakırköy Merkez', 'Istanbul', 'Bakırköy', 40.9833, 28.8722)
ON CONFLICT DO NOTHING;

-- Rozetler
INSERT INTO rozetler (rozet_adi, rozet_kodu, aciklama, gerekli_puan, rozet_turu, renk) VALUES
('İlk Adım', 'FIRST_STEP', 'İlk ihbarını oluştur', 10, 'ihbar', 'bronze'),
('Hayvan Dostu', 'ANIMAL_FRIEND', '10 ihbar oluştur', 100, 'ihbar', 'silver'),
('Sokak Kahramanı', 'STREET_HERO', '50 ihbar oluştur', 500, 'ihbar', 'gold'),
('Besleyici', 'FEEDER', '20 besleme kaydı oluştur', 200, 'besleme', 'bronze'),
('Konuşkan', 'CHATTY', '100 yorum yap', 200, 'yorum', 'silver'),
('Efsane', 'LEGEND', '1000 puana ulaş', 1000, 'rutbe', 'diamond')
ON CONFLICT (rozet_kodu) DO NOTHING;

-- Test Admin Kullanıcısı (Şifre: Admin123!)
INSERT INTO kullanicilar (kullanici_adi, eposta, sifre_hash, rol, tam_isim, puan, rutbe) VALUES
('admin', 'admin@patitakip.com', '$2b$10$Qxw4.vQM9K5jR7Z8xY6J4.rH8mP3nF2kL6wS9tX1vB4cD5eA7gH8i', 'yonetici', 'Sistem Yöneticisi', 1000, 5)
ON CONFLICT (eposta) DO NOTHING;

-- Test Gönüllü Kullanıcısı (Şifre: Test123!)
INSERT INTO kullanicilar (kullanici_adi, eposta, sifre_hash, rol, tam_isim) VALUES
('test_kullanici', 'test@patitakip.com', '$2b$10$Qxw4.vQM9K5jR7Z8xY6J4.rH8mP3nF2kL6wS9tX1vB4cD5eA7gH8i', 'gonullu', 'Test Kullanıcı')
ON CONFLICT (eposta) DO NOTHING;

-- ============================================================================
-- YARDIMCI FONKSİYONLAR
-- ============================================================================

-- 1. Konum bazlı yakındaki ihbarları bul (Harita için)
CREATE OR REPLACE FUNCTION yakindaki_ihbarlar(
    p_enlem DECIMAL(9,6),
    p_boylam DECIMAL(9,6),
    p_yaricap_km DECIMAL DEFAULT 5.0
)
RETURNS TABLE (
    ihbar_id INT,
    mesafe_km DECIMAL
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        i.ihbar_id,
        (
            6371 * acos(
                cos(radians(p_enlem)) * 
                cos(radians(i.enlem)) * 
                cos(radians(i.boylam) - radians(p_boylam)) + 
                sin(radians(p_enlem)) * 
                sin(radians(i.enlem))
            )
        )::DECIMAL(10,2) as mesafe_km
    FROM ihbarlar i
    WHERE i.silindi = FALSE
    HAVING (
        6371 * acos(
            cos(radians(p_enlem)) * 
            cos(radians(i.enlem)) * 
            cos(radians(i.boylam) - radians(p_boylam)) + 
            sin(radians(p_enlem)) * 
            sin(radians(i.enlem))
        )
    ) <= p_yaricap_km
    ORDER BY mesafe_km ASC;
END;
$$ LANGUAGE plpgsql;

-- 2. Kullanıcı rütbe hesaplama
CREATE OR REPLACE FUNCTION kullanici_rutbe_hesapla(p_puan INT)
RETURNS INT AS $$
BEGIN
    RETURN CASE
        WHEN p_puan < 50 THEN 0
        WHEN p_puan < 100 THEN 1
        WHEN p_puan < 250 THEN 2
        WHEN p_puan < 500 THEN 3
        WHEN p_puan < 1000 THEN 4
        ELSE 5
    END;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- VERİTABANI HAKLARI (Opsiyonel - Production için)
-- ============================================================================

-- Eğer özel kullanıcı kullanıyorsanız:
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO patitakip_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO patitakip_user;
-- GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO patitakip_user;

-- ============================================================================
-- TAMAMLANDI!
-- ============================================================================
-- 🎉 PatiTakip veritabanı başarıyla oluşturuldu!
-- 
-- Kullanım:
-- 1. Bu dosyayı çalıştırın: psql -U postgres -d patitakip -f database_full_schema.sql
-- 2. Test kullanıcılarıyla giriş yapabilirsiniz:
--    - Admin: admin@patitakip.com / Admin123!
--    - Test: test@patitakip.com / Test123!
-- 
-- Toplam Tablo Sayısı: 17
-- Toplam Index Sayısı: 25+
-- Toplam Trigger Sayısı: 5
-- Toplam View Sayısı: 3
-- ============================================================================

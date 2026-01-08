-- 10_update_views_soft_delete.sql
-- Amaç: Tüm View'larda soft delete filtrelemesi eklemek

-- 1. View: Aktif İhbarlar (Silinmemiş)
CREATE OR REPLACE VIEW vi_aktif_ihbarlar AS
SELECT 
    i.ihbar_id,
    i.ihbar_turu,
    i.aciklama,
    i.enlem,
    i.boylam,
    b.bolge_adi,
    i.olusturulma_tarihi
FROM ihbarlar i
LEFT JOIN bolgeler b ON i.bolge_id = b.bolge_id AND b.deleted_at IS NULL
WHERE i.durum = 'Acik' AND i.deleted_at IS NULL;

-- 2. View: Bölge Açlık Endeksi
CREATE OR REPLACE VIEW vi_bolge_aclik_durumu AS
SELECT 
    b.bolge_adi,
    COUNT(DISTINCT i.ihbar_id) FILTER (WHERE i.ihbar_turu = 'Aclik' AND i.deleted_at IS NULL) as aclik_ihbar_sayisi,
    COUNT(DISTINCT bes.besleme_id) FILTER (WHERE bes.deleted_at IS NULL) as toplam_besleme
FROM bolgeler b
LEFT JOIN ihbarlar i ON b.bolge_id = i.bolge_id
LEFT JOIN beslemeler bes ON b.bolge_id = bes.bolge_id
WHERE b.deleted_at IS NULL
GROUP BY b.bolge_id, b.bolge_adi;

-- 3. View: Gönüllü Liderlik Tablosu
CREATE OR REPLACE VIEW vi_gonullu_siralamasi AS
SELECT 
    k.kullanici_adi,
    COUNT(DISTINCT i.ihbar_id) FILTER (WHERE i.deleted_at IS NULL) as ihbar_sayisi,
    COUNT(DISTINCT b.besleme_id) FILTER (WHERE b.deleted_at IS NULL) as besleme_sayisi,
    (COUNT(DISTINCT i.ihbar_id) FILTER (WHERE i.deleted_at IS NULL) + 
     COUNT(DISTINCT b.besleme_id) FILTER (WHERE b.deleted_at IS NULL)) as toplam_katki
FROM kullanicilar k
LEFT JOIN ihbarlar i ON k.kullanici_id = i.kullanici_id
LEFT JOIN beslemeler b ON k.kullanici_id = b.kullanici_id
WHERE k.deleted_at IS NULL
GROUP BY k.kullanici_id, k.kullanici_adi
ORDER BY toplam_katki DESC;

-- 4. View: Hayvan Detay Kartı
CREATE OR REPLACE VIEW vi_hayvan_detay AS
SELECT 
    h.hayvan_id,
    h.isim,
    t.tur_adi,
    h.tahmini_yas,
    COUNT(sk.kayit_id) FILTER (WHERE sk.deleted_at IS NULL) as saglik_mudahale_sayisi
FROM hayvanlar h
JOIN hayvan_turleri t ON h.tur_id = t.tur_id AND t.deleted_at IS NULL
LEFT JOIN saglik_kayitlari sk ON h.hayvan_id = sk.hayvan_id
WHERE h.deleted_at IS NULL
GROUP BY h.hayvan_id, h.isim, t.tur_adi, h.tahmini_yas;

-- 5. View: Günlük Aktivite
CREATE OR REPLACE VIEW vi_gunluk_aktivite AS
SELECT 
    CURRENT_DATE as tarih,
    (SELECT COUNT(*) FROM ihbarlar WHERE olusturulma_tarihi::DATE = CURRENT_DATE AND deleted_at IS NULL) as yeni_ihbarlar,
    (SELECT COUNT(*) FROM beslemeler WHERE besleme_zamani::DATE = CURRENT_DATE AND deleted_at IS NULL) as bugunku_beslemeler;

-- 6. View: Maskelenmiş Kullanıcılar
CREATE OR REPLACE VIEW vi_kullanicilar_public AS
SELECT 
    kullanici_id,
    kullanici_adi,
    fn_eposta_maskele(eposta) as maskelenmis_eposta,
    rol,
    olusturulma_tarihi
FROM kullanicilar
WHERE deleted_at IS NULL;

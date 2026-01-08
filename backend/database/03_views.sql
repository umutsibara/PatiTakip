-- 03_gorunumler.sql (Views)
-- Amaç: Raporlama ve Analiz (Güncel)

-- 1. View: Aktif İhbarlar (Harita İçin Koordinatlı)
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
LEFT JOIN bolgeler b ON i.bolge_id = b.bolge_id
WHERE i.durum = 'Acik';

-- 2. View: Bölge Açlık Endeksi
CREATE OR REPLACE VIEW vi_bolge_aclik_durumu AS
SELECT 
    b.bolge_adi,
    COUNT(DISTINCT i.ihbar_id) FILTER (WHERE i.ihbar_turu = 'Aclik') as aclik_ihbar_sayisi,
    COUNT(DISTINCT bes.besleme_id) as toplam_besleme
FROM bolgeler b
LEFT JOIN ihbarlar i ON b.bolge_id = i.bolge_id
LEFT JOIN beslemeler bes ON b.bolge_id = bes.bolge_id
GROUP BY b.bolge_id, b.bolge_adi;

-- 3. View: Gönüllü Liderlik Tablosu
CREATE OR REPLACE VIEW vi_gonullu_siralamasi AS
SELECT 
    k.kullanici_adi,
    COUNT(DISTINCT i.ihbar_id) as ihbar_sayisi,
    COUNT(DISTINCT b.besleme_id) as besleme_sayisi,
    (COUNT(DISTINCT i.ihbar_id) + COUNT(DISTINCT b.besleme_id)) as toplam_katki
FROM kullanicilar k
LEFT JOIN ihbarlar i ON k.kullanici_id = i.kullanici_id
LEFT JOIN beslemeler b ON k.kullanici_id = b.kullanici_id
GROUP BY k.kullanici_id, k.kullanici_adi
ORDER BY toplam_katki DESC;

-- 4. View: Hayvan Detay Kartı
CREATE OR REPLACE VIEW vi_hayvan_detay AS
SELECT 
    h.hayvan_id,
    h.isim,
    t.tur_adi,
    h.tahmini_yas,
    COUNT(sk.kayit_id) as saglik_mudahale_sayisi
FROM hayvanlar h
JOIN hayvan_turleri t ON h.tur_id = t.tur_id
LEFT JOIN saglik_kayitlari sk ON h.hayvan_id = sk.hayvan_id
GROUP BY h.hayvan_id, h.isim, t.tur_adi, h.tahmini_yas;

-- 5. View: Günlük Aktivite
CREATE OR REPLACE VIEW vi_gunluk_aktivite AS
SELECT 
    CURRENT_DATE as tarih,
    (SELECT COUNT(*) FROM ihbarlar WHERE olusturulma_tarihi::DATE = CURRENT_DATE) as yeni_ihbarlar,
    (SELECT COUNT(*) FROM beslemeler WHERE besleme_zamani::DATE = CURRENT_DATE) as bugunku_beslemeler;

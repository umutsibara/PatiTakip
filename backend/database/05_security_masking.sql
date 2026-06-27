-- 05_guvenlik_maskeleme.sql
-- Amaç: Maskeleme (10 Puan)

-- 1. View: Maskelenmiş Kullanıcılar (vi_kullanicilar_public)
CREATE OR REPLACE VIEW vi_kullanicilar_public AS
SELECT 
    kullanici_id,
    kullanici_adi,
    fn_eposta_maskele(eposta) as maskelenmis_eposta,
    rol,
    olusturulma_tarihi
FROM kullanicilar;

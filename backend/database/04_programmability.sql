-- 04_programlanabilirlik.sql
-- Amaç: Stored Procedure ve Fonksiyonlar (Türkçe)

-- --- Stored Procedures ---

-- 1. SP: İhbar Çözümleme (sp_ihbar_cozumle)
CREATE OR REPLACE PROCEDURE sp_ihbar_cozumle(
    p_ihbar_id INT,
    p_notlar TEXT
)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE ihbarlar 
    SET durum = 'Cozuldu', 
        aciklama = aciklama || ' [ÇÖZÜM: ' || p_notlar || ']'
    WHERE ihbar_id = p_ihbar_id;
    
    IF NOT FOUND THEN
        RAISE EXCEPTION 'İhbar ID % bulunamadı', p_ihbar_id;
    END IF;
END;
$$;

-- 2. SP: Hızlı Besleme Ekle (sp_hizli_besleme_ekle)
CREATE OR REPLACE PROCEDURE sp_hizli_besleme_ekle(
    p_kullanici_id INT,
    p_bolge_id INT,
    p_miktar DECIMAL
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO beslemeler (kullanici_id, bolge_id, mama_miktari_kg, besleme_zamani)
    VALUES (p_kullanici_id, p_bolge_id, p_miktar, NOW());
    
    RAISE NOTICE 'Kullanıcı % Bölge % için % kg besleme yaptı', p_kullanici_id, p_bolge_id, p_miktar;
END;
$$;

-- --- User Defined Functions ---

-- 1. Function: Bölge Risk Seviyesi (fn_bolge_risk_seviyesi_getir)
CREATE OR REPLACE FUNCTION fn_bolge_risk_seviyesi_getir(p_bolge_id INT)
RETURNS VARCHAR
LANGUAGE plpgsql
AS $$
DECLARE
    saglik_ihbar_sayisi INT;
    risk_seviyesi VARCHAR(10);
BEGIN
    SELECT COUNT(*) INTO saglik_ihbar_sayisi
    FROM ihbarlar
    WHERE bolge_id = p_bolge_id AND ihbar_turu = 'Saglik' AND durum = 'Acik';
    
    IF saglik_ihbar_sayisi > 5 THEN
        risk_seviyesi := 'YUKSEK';
    ELSIF saglik_ihbar_sayisi >= 2 THEN
        risk_seviyesi := 'ORTA';
    ELSE
        risk_seviyesi := 'DUSUK';
    END IF;
    
    RETURN risk_seviyesi;
END;
$$;

-- 2. Function: E-posta Maskeleme (fn_eposta_maskele)
CREATE OR REPLACE FUNCTION fn_eposta_maskele(p_eposta VARCHAR)
RETURNS VARCHAR
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN SUBSTRING(p_eposta FROM 1 FOR 1) || '***' || SUBSTRING(p_eposta FROM POSITION('@' IN p_eposta));
END;
$$;

-- 02_indeksler.sql
-- Amaç: Sorgu Performansı

-- 1. İhbarları "Bölge" ve "Durum"a göre filtreleme
CREATE INDEX idx_ihbarlar_bolge_durum ON ihbarlar(bolge_id, durum);

-- 2. Hayvanları türüne göre filtreleme
CREATE INDEX idx_hayvanlar_tur ON hayvanlar(tur_id);

-- 3. Kullanıcının ihbarları (Profil sorguları için)
CREATE INDEX idx_ihbarlar_kullanici ON ihbarlar(kullanici_id);

-- 4. Beslemelerin zaman sıralaması
CREATE INDEX idx_beslemeler_zaman ON beslemeler(besleme_zamani DESC);

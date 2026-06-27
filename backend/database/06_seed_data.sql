-- 06_test_verisi.sql (Seed Data)
-- Güncelleme: Koordinat verileri eklendi.

-- Hayvan Türleri
INSERT INTO hayvan_turleri (tur_adi) VALUES ('Kedi'), ('Köpek'), ('Kuş');

-- Bölgeler (Merkez koordinatlarıyla)
INSERT INTO bolgeler (bolge_adi, ilce, merkez_enlem, merkez_boylam) VALUES 
('Cumhuriyet Mah', 'Şişli', 41.0520, 28.9850),
('Moda', 'Kadıköy', 40.9870, 29.0250),
('Bebek', 'Beşiktaş', 41.0760, 29.0430);

-- Kullanıcılar
INSERT INTO kullanicilar (kullanici_adi, eposta, sifre_hash, rol) VALUES 
('admin_umut', 'umut@patitakip.com', 'hashed_pass_123', 'yonetici'),
('gonullu_ali', 'ali@gmail.com', 'hashed_pass_456', 'gonullu'),
('gonullu_ayse', 'ayse@hotmail.com', 'hashed_pass_789', 'gonullu');

-- Hayvanlar
INSERT INTO hayvanlar (tur_id, isim, tahmini_yas) VALUES 
(1, 'Tekir', 2),
(2, 'Karabaş', 4);

-- İhbarlar (Enlem/Boylam zorunlu)
INSERT INTO ihbarlar (kullanici_id, bolge_id, hayvan_id, ihbar_turu, aciklama, durum, enlem, boylam) VALUES 
(2, 1, 1, 'Aclik', 'Kedi çok zayıf görünüyor', 'Acik', 41.0522, 28.9855),
(3, 2, NULL, 'Saglik', 'Yaralı bir kuş var', 'Acik', 40.9875, 29.0252);

-- Beslemeler
INSERT INTO beslemeler (kullanici_id, bolge_id, mama_miktari_kg) VALUES 
(2, 1, 1.5),
(3, 2, 0.5);

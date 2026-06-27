-- 08_seed_zones.sql
-- Amaç: İzmir ve Manisa illerinin tüm ilçelerini bölge olarak eklemek

-- İZMİR İLÇELERİ
INSERT INTO bolgeler (bolge_adi, ilce, merkez_enlem, merkez_boylam) VALUES 
-- İzmir Merkez İlçeler
('Alsancak', 'Konak', 38.4370, 27.1461),
('Konak Merkez', 'Konak', 38.4189, 27.1287),
('Basmane', 'Konak', 38.4250, 27.1420),

('Karşıyaka Merkez', 'Karşıyaka', 38.4602, 27.1107),
('Bostanlı', 'Karşıyaka', 38.4650, 27.0950),

('Bornova Merkez', 'Bornova', 38.4697, 27.2167),
('Erzene', 'Bornova', 38.4850, 27.2350),

('Buca Merkez', 'Buca', 38.3833, 27.1833),
('Kuruçeşme', 'Buca', 38.3750, 27.1950),

('Çiğli Merkez', 'Çiğli', 38.4972, 27.0500),
('Sasalı', 'Çiğli', 38.5100, 27.0350),

('Gaziemir Merkez', 'Gaziemir', 38.3250, 27.1333),

('Balçova Merkez', 'Balçova', 38.3928, 27.0306),
('Teleferik', 'Balçova', 38.3850, 27.0250),

('Güzelbahçe Merkez', 'Güzelbahçe', 38.3667, 26.8833),

('Narlıdere Merkez', 'Narlıdere', 38.3972, 27.0228),

-- İzmir Diğer İlçeler
('Aliağa Merkez', 'Aliağa', 38.8000, 26.9667),

('Bayındır Merkez', 'Bayındır', 38.2167, 27.6500),

('Bayraklı Merkez', 'Bayraklı', 38.4636, 27.1583),

('Bergama Merkez', 'Bergama', 39.1211, 27.1808),

('Beydağ Merkez', 'Beydağ', 38.0833, 28.2167),

('Çeşme Merkez', 'Çeşme', 38.3250, 26.3056),
('Alaçatı', 'Çeşme', 38.3000, 26.3667),
('Ilıca', 'Çeşme', 38.3333, 26.3500),

('Dikili Merkez', 'Dikili', 39.0714, 26.8889),

('Foça Merkez', 'Foça', 38.6667, 26.7500),

('Karabağlar Merkez', 'Karabağlar', 38.3750, 27.1333),

('Karaburun Merkez', 'Karaburun', 38.6333, 26.5167),

('Karşıyaka', 'Karşıyaka', 38.4602, 27.1107),

('Kemalpaşa Merkez', 'Kemalpaşa', 38.4333, 27.4167),

('Kınık Merkez', 'Kınık', 39.0833, 27.3833),

('Kiraz Merkez', 'Kiraz', 38.2333, 28.2000),

('Menderes Merkez', 'Menderes', 38.2500, 27.1333),

('Menemen Merkez', 'Menemen', 38.6083, 27.0708),

('Ödemiş Merkez', 'Ödemiş', 38.2250, 27.9667),

('Seferihisar Merkez', 'Seferihisar', 38.1958, 26.8392),
('Sığacık', 'Seferihisar', 38.1833, 26.7833),

('Selçuk Merkez', 'Selçuk', 37.9500, 27.3667),

('Tire Merkez', 'Tire', 38.0833, 27.7333),

('Torbalı Merkez', 'Torbalı', 38.1583, 27.3583),

('Urla Merkez', 'Urla', 38.3239, 26.7664),

-- MANİSA İLÇELERİ
('Şehzadeler Merkez', 'Şehzadeler', 38.6139, 27.4292),

('Yunusemre Merkez', 'Yunusemre', 38.6292, 27.4419),

('Akhisar Merkez', 'Akhisar', 38.9167, 27.8333),

('Alaşehir Merkez', 'Alaşehir', 38.3500, 28.5167),

('Demirci Merkez', 'Demirci', 39.0500, 28.6667),

('Gördes Merkez', 'Gördes', 38.9333, 28.2833),

('Kırkağaç Merkez', 'Kırkağaç', 39.1083, 27.6667),

('Kula Merkez', 'Kula', 38.5500, 28.6500),

('Salihli Merkez', 'Salihli', 38.4833, 28.1333),

('Sarıgöl Merkez', 'Sarıgöl', 38.2333, 28.7000),

('Saruhanlı Merkez', 'Saruhanlı', 38.7333, 27.9167),

('Selendi Merkez', 'Selendi', 38.7500, 28.8667),

('Soma Merkez', 'Soma', 39.1833, 27.6167),

('Turgutlu Merkez', 'Turgutlu', 38.5000, 27.7000),

('Ahmetli Merkez', 'Ahmetli', 38.5167, 27.9500),

('Gölmarmara Merkez', 'Gölmarmara', 38.7167, 27.9000),

('Köprübaşı Merkez', 'Köprübaşı', 39.1500, 28.4333);

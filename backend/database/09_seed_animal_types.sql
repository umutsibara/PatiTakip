-- 09_seed_animal_types.sql
-- Amaç: Hayvan türlerini detaylı açıklamalarıyla eklemek

INSERT INTO hayvan_turleri (tur_adi, aciklama) VALUES 
('Kedi', 'Evcil veya sokak kedileri. Genellikle küçük ve çevik yapılıdır. Mama ve su ihtiyacı vardır.'),
('Köpek', 'Evcil veya sokak köpekleri. Orta-büyük boy olabilir. Düzenli beslenme ve su gerektirir.'),
('Kuş', 'Şehir kuşları (güvercin, serçe, vb.). Yaralıysa veteriner müdahalesi gerekebilir.'),
('Tavşan', 'Nadir görülen sokak tavşanları. Otçul beslenirler.'),
('Kaplumbağa', 'Kara veya su kaplumbağaları. Özel bakım gerektirir.'),
('Hamster', 'Küçük kemirgen evcil hayvanlar.'),
('Papağan', 'Evcil veya egzotik kuş türleri.'),
('Balık', 'Evcil akvaryum balıkları.'),
('Kirpi', 'Genellikle gececil sokak hayvanları.'),
('Diğer', 'Listede bulunmayan diğer hayvan türleri için.');

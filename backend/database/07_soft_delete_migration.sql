-- 07_soft_delete_migration.sql
-- Amaç: Tüm tablolara soft delete desteği eklemek
-- Kullanım: Veriler artık silinmeyecek, sadece deleted_at ile işaretlenecek

-- Kullanıcılar
ALTER TABLE kullanicilar ADD COLUMN deleted_at TIMESTAMP NULL;

-- Hayvan Türleri
ALTER TABLE hayvan_turleri ADD COLUMN deleted_at TIMESTAMP NULL;

-- Bölgeler
ALTER TABLE bolgeler ADD COLUMN deleted_at TIMESTAMP NULL;

-- Hayvanlar
ALTER TABLE hayvanlar ADD COLUMN deleted_at TIMESTAMP NULL;

-- İhbarlar
ALTER TABLE ihbarlar ADD COLUMN deleted_at TIMESTAMP NULL;

-- Sağlık Kayıtları
ALTER TABLE saglik_kayitlari ADD COLUMN deleted_at TIMESTAMP NULL;

-- Beslemeler
ALTER TABLE beslemeler ADD COLUMN deleted_at TIMESTAMP NULL;

-- Fotoğraflar
ALTER TABLE fotograflar ADD COLUMN deleted_at TIMESTAMP NULL;

-- NOT: deleted_at NULL ise kayıt aktif, değer varsa silinmiş demektir.

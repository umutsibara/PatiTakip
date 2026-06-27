# Veritabanı Kurulum Talimatları (Türkçe - Konum Destekli)

Veritabanı şeması **Google Maps benzeri harita kullanımı** için güncellenmiştir.
- `ihbarlar` tablosu artık `enlem` ve `boylam` parametrelerini zorunlu tutar.
- `bolgeler` (Mahalleler) referans amaçlıdır ve ihbarlar için opsiyoneldir.

## Kurulum
Lütfen dosyaları sırasıyla çalıştırınız:

1.  **`01_tables.sql`**: (Konum sütunları dahil)
2.  **`02_indexes.sql`**
3.  **`03_views.sql`**: (Harita görünümleri dahil)
4.  **`04_programmability.sql`**
5.  **`05_security_masking.sql`**
6.  **`06_test_verisi.sql`**: (Koordinatlı örnek veriler)

Artık backend tarafında kullanıcıdan gelen koordinatları doğrudan veritabanına kaydedebilirsiniz.

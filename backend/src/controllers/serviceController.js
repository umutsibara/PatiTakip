const db = require('../config/db');

// Tüm Hizmetleri Getir
exports.getAllServices = async (req, res) => {
    try {
        const query = `
            SELECT h.*, k.kullanici_adi, k.avatar_url 
            FROM hizmetler h
            JOIN kullanicilar k ON h.saglayici_id = k.kullanici_id
            ORDER BY h.olusturulma_tarihi DESC
        `;
        const result = await db.query(query);
        
        // Frontend mapping
        const services = result.rows.map(row => ({
            id: row.hizmet_id,
            provider: {
                id: row.saglayici_id,
                name: row.kullanici_adi,
                avatarUrl: row.avatar_url
            },
            title: row.baslik,
            description: row.aciklama,
            pricePerDay: row.fiyat_gunluk,
            category: row.kategori,
            rating: row.ort_puan
        }));

        res.status(200).json({
            success: true,
            data: services
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Yeni Hizmet Oluştur
exports.createService = async (req, res) => {
    const { saglayici_id, baslik, aciklama, fiyat_gunluk, kategori } = req.body;

    if (!saglayici_id || !baslik || !fiyat_gunluk) {
        return res.status(400).json({ success: false, error: 'Sağlayıcı, Başlık ve Fiyat zorunludur.' });
    }

    try {
        const query = `
            INSERT INTO hizmetler (saglayici_id, baslik, aciklama, fiyat_gunluk, kategori)
            VALUES ($1, $2, $3, $4, $5)
            RETURNING *;
        `;
        const result = await db.query(query, [saglayici_id, baslik, aciklama, fiyat_gunluk, kategori || 'PET_SITTING']);

        res.status(201).json({
            success: true,
            message: 'Hizmet ilanı oluşturuldu.',
            data: result.rows[0]
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

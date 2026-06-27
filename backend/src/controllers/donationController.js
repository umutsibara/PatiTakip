const db = require('../config/db');

// Tüm Bağışları Getir
exports.getAllDonations = async (req, res) => {
    try {
        const result = await db.query('SELECT * FROM bagislar WHERE aktif = true ORDER BY olusturulma_tarihi DESC');
        
        // Frontend mapping
        const donations = result.rows.map(row => ({
            id: row.bagis_id,
            title: row.baslik,
            description: row.aciklama,
            requiredPoints: row.gerekli_puan,
            category: row.kategori,
            icon: row.ikon || (row.kategori === 'FOOD' ? '🍗' : row.kategori === 'MEDICAL' ? '💉' : '🏠'), // Default icons
            active: row.aktif
        }));

        res.status(200).json({
            success: true,
            data: donations
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Yeni Bağış Kampanyası Oluştur
exports.createDonation = async (req, res) => {
    const { baslik, aciklama, gerekli_puan, kategori } = req.body;

    if (!baslik || !gerekli_puan) {
        return res.status(400).json({ success: false, error: 'Başlık ve Gerekli Puan zorunludur.' });
    }

    try {
        const query = `
            INSERT INTO bagislar (baslik, aciklama, gerekli_puan, kategori)
            VALUES ($1, $2, $3, $4)
            RETURNING *;
        `;
        const result = await db.query(query, [baslik, aciklama, gerekli_puan, kategori || 'FOOD']);

        res.status(201).json({
            success: true,
            message: 'Bağış kampanyası oluşturuldu.',
            data: result.rows[0]
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

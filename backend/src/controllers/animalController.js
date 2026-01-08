const db = require('../config/db');

// Tüm Hayvanları Getir
exports.getAllAnimals = async (req, res) => {
    try {
        // View (vi_hayvan_detay) yerine doğrudan sorgu
        const query = `
            SELECT 
                h.hayvan_id,
                h.isim,
                t.tur_adi,
                h.tahmini_yas,
                COUNT(sk.kayit_id)::int as saglik_mudahale_sayisi
            FROM hayvanlar h
            LEFT JOIN hayvan_turleri t ON h.tur_id = t.tur_id
            LEFT JOIN saglik_kayitlari sk ON h.hayvan_id = sk.hayvan_id
            WHERE h.deleted_at IS NULL
            GROUP BY h.hayvan_id, h.isim, t.tur_adi, h.tahmini_yas
            ORDER BY h.hayvan_id DESC
        `;
        const result = await db.query(query);
        res.status(200).json({ success: true, data: result.rows });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Yeni Hayvan Ekle
exports.createAnimal = async (req, res) => {
    const { tur_id, isim, yas } = req.body;

    try {
        const result = await db.query(
            'INSERT INTO hayvanlar (tur_id, isim, tahmini_yas) VALUES ($1, $2, $3) RETURNING *',
            [tur_id, isim, yas]
        );
        res.status(201).json({ success: true, data: result.rows[0] });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

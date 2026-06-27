const db = require('../config/db');

// Tüm Türleri Getir
exports.getAllTypes = async (req, res) => {
    try {
        const result = await db.query('SELECT * FROM hayvan_turleri ORDER BY tur_adi ASC');
        res.status(200).json({ success: true, count: result.rows.length, data: result.rows });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Tür Ekle
exports.createType = async (req, res) => {
    const { tur_adi, aciklama } = req.body;

    if (!tur_adi) {
        return res.status(400).json({ success: false, error: 'Tür adı zorunludur.' });
    }

    try {
        const result = await db.query(
            'INSERT INTO hayvan_turleri (tur_adi, aciklama) VALUES ($1, $2) RETURNING *',
            [tur_adi, aciklama]
        );
        res.status(201).json({ success: true, data: result.rows[0] });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Tür Güncelle
exports.updateType = async (req, res) => {
    const { id } = req.params;
    const { tur_adi, aciklama } = req.body;

    try {
        const result = await db.query(
            `UPDATE hayvan_turleri 
             SET tur_adi = COALESCE($1, tur_adi), 
                 aciklama = COALESCE($2, aciklama)
             WHERE tur_id = $3 
             RETURNING *`,
            [tur_adi, aciklama, id]
        );

        if (result.rows.length === 0) {
            return res.status(404).json({ success: false, error: 'Tür bulunamadı.' });
        }

        res.status(200).json({ success: true, data: result.rows[0] });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Tür Sil (Soft Delete)
exports.deleteType = async (req, res) => {
    const { id } = req.params;

    try {
        const result = await db.query(
            'DELETE FROM hayvan_turleri WHERE tur_id = $1 RETURNING tur_id',
            [id]
        );

        if (result.rows.length === 0) {
            return res.status(404).json({ success: false, error: 'Tür bulunamadı veya zaten silinmiş.' });
        }

        res.status(200).json({ success: true, message: 'Tür başarıyla silindi.' });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

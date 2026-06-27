const db = require('../config/db');

// Tüm Bölgeleri Getir (Aktif olanlar)
exports.getAllZones = async (req, res) => {
    try {
        const result = await db.query('SELECT * FROM bolgeler WHERE deleted_at IS NULL ORDER BY bolge_adi ASC');
        res.status(200).json({ success: true, count: result.rows.length, data: result.rows });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Bölge Ekle
exports.createZone = async (req, res) => {
    const { bolge_adi, ilce, merkez_enlem, merkez_boylam } = req.body;

    if (!bolge_adi || !ilce) {
        return res.status(400).json({ success: false, error: 'Bölge adı ve ilçe zorunludur.' });
    }

    try {
        const result = await db.query(
            'INSERT INTO bolgeler (bolge_adi, ilce, merkez_enlem, merkez_boylam) VALUES ($1, $2, $3, $4) RETURNING *',
            [bolge_adi, ilce, merkez_enlem, merkez_boylam]
        );
        res.status(201).json({ success: true, data: result.rows[0] });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Bölge Güncelle
exports.updateZone = async (req, res) => {
    const { id } = req.params;
    const { bolge_adi, ilce, merkez_enlem, merkez_boylam } = req.body;

    try {
        const result = await db.query(
            `UPDATE bolgeler 
             SET bolge_adi = COALESCE($1, bolge_adi), 
                 ilce = COALESCE($2, ilce), 
                 merkez_enlem = COALESCE($3, merkez_enlem), 
                 merkez_boylam = COALESCE($4, merkez_boylam)
             WHERE bolge_id = $5 AND deleted_at IS NULL 
             RETURNING *`,
            [bolge_adi, ilce, merkez_enlem, merkez_boylam, id]
        );

        if (result.rows.length === 0) {
            return res.status(404).json({ success: false, error: 'Bölge bulunamadı.' });
        }

        res.status(200).json({ success: true, data: result.rows[0] });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Bölge Sil (Soft Delete)
exports.deleteZone = async (req, res) => {
    const { id } = req.params;

    try {
        const result = await db.query(
            'UPDATE bolgeler SET deleted_at = CURRENT_TIMESTAMP WHERE bolge_id = $1 AND deleted_at IS NULL RETURNING bolge_id',
            [id]
        );

        if (result.rows.length === 0) {
            return res.status(404).json({ success: false, error: 'Bölge bulunamadı veya zaten silinmiş.' });
        }

        res.status(200).json({ success: true, message: 'Bölge başarıyla silindi.' });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

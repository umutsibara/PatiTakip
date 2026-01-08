const db = require('../config/db');

// Bir hayvana ait sağlık kayıtlarını getir
exports.getHealthRecordsByAnimal = async (req, res) => {
    const { animalId } = req.params;

    try {
        const result = await db.query(
            'SELECT * FROM saglik_kayitlari WHERE hayvan_id = $1 AND deleted_at IS NULL ORDER BY tarih DESC',
            [animalId]
        );
        res.status(200).json({ success: true, count: result.rows.length, data: result.rows });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Yeni Sağlık Kaydı Ekle
exports.createHealthRecord = async (req, res) => {
    const { hayvan_id, mudahale_turu, veteriner_notu } = req.body;

    if (!hayvan_id || !mudahale_turu) {
        return res.status(400).json({ success: false, error: 'Hayvan ID ve müdahale türü zorunludur.' });
    }

    try {
        const result = await db.query(
            'INSERT INTO saglik_kayitlari (hayvan_id, mudahale_turu, veteriner_notu) VALUES ($1, $2, $3) RETURNING *',
            [hayvan_id, mudahale_turu, veteriner_notu]
        );
        res.status(201).json({ success: true, data: result.rows[0] });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Sağlık Kaydı Güncelle
exports.updateHealthRecord = async (req, res) => {
    const { id } = req.params;
    const { mudahale_turu, veteriner_notu } = req.body;

    try {
        const result = await db.query(
            `UPDATE saglik_kayitlari 
             SET mudahale_turu = COALESCE($1, mudahale_turu), 
                 veteriner_notu = COALESCE($2, veteriner_notu)
             WHERE kayit_id = $3 AND deleted_at IS NULL 
             RETURNING *`,
            [mudahale_turu, veteriner_notu, id]
        );

        if (result.rows.length === 0) {
            return res.status(404).json({ success: false, error: 'Kayıt bulunamadı.' });
        }

        res.status(200).json({ success: true, data: result.rows[0] });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Sağlık Kaydı Sil (Soft Delete)
exports.deleteHealthRecord = async (req, res) => {
    const { id } = req.params;

    try {
        const result = await db.query(
            'UPDATE saglik_kayitlari SET deleted_at = CURRENT_TIMESTAMP WHERE kayit_id = $1 AND deleted_at IS NULL RETURNING kayit_id',
            [id]
        );

        if (result.rows.length === 0) {
            return res.status(404).json({ success: false, error: 'Kayıt bulunamadı veya zaten silinmiş.' });
        }

        res.status(200).json({ success: true, message: 'Sağlık kaydı silindi.' });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

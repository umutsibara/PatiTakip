const db = require('../config/db');

// Yeni Besleme Ekle (SP Kullanır)
exports.createFeeding = async (req, res) => {
    const { kullanici_id, bolge_id, miktar } = req.body;

    if (!kullanici_id || !bolge_id || !miktar) {
        return res.status(400).json({ success: false, error: 'Eksik veri.' });
    }

    try {
        // SP Çağrısı YERİNE Doğrudan Insert
        await db.query(
            'INSERT INTO beslemeler (kullanici_id, bolge_id, mama_miktari_kg) VALUES ($1, $2, $3)',
            [kullanici_id, bolge_id, miktar]
        );
        
        res.status(201).json({
            success: true,
            message: 'Besleme kaydı başarıyla eklendi.'
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Beslemeleri Listele (CRUD - Read)
exports.getAllFeedings = async (req, res) => {
    try {
        const result = await db.query('SELECT * FROM beslemeler ORDER BY besleme_zamani DESC LIMIT 50');
        res.status(200).json({ success: true, count: result.rows.length, data: result.rows });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

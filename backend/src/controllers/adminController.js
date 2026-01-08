const db = require('../config/db');

// Logları Listele (Filtreleme destekli)
exports.getSystemLogs = async (req, res) => {
    // Sadece Yöneticiler görebilir (Route'da kontrol edilecek)
    try {
        const { limit } = req.query;
        const limitVal = limit ? parseInt(limit) : 100;

        const query = `
            SELECT 
                l.*,
                k.kullanici_adi,
                k.rol
            FROM kullanici_loglari l
            LEFT JOIN kullanicilar k ON l.kullanici_id = k.kullanici_id
            ORDER BY l.created_at DESC
            LIMIT $1
        `;

        const result = await db.query(query, [limitVal]);

        res.status(200).json({
            success: true,
            count: result.rows.length,
            data: result.rows
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Log İstatistikleri (Dashboard için)
exports.getLogStats = async (req, res) => {
    try {
        const query = `
            SELECT islem_turu, COUNT(*) as sayi 
            FROM kullanici_loglari 
            GROUP BY islem_turu
        `;
        const result = await db.query(query);

        res.status(200).json({
            success: true,
            data: result.rows
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

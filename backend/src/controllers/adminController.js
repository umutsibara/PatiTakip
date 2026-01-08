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

// --- RAPOR YÖNETİMİ ---

// Tüm İhbarları Getir (Admin için - Durum farketmeksizin veya filtrelenebilir)
exports.getAllReportsAdmin = async (req, res) => {
    try {
        const { status } = req.query;
        
        let queryText = `
            SELECT i.*, k.kullanici_adi, k.avatar_url 
            FROM ihbarlar i
            JOIN kullanicilar k ON i.kullanici_id = k.kullanici_id
        `;
        
        const params = [];
        if (status) {
            queryText += ` WHERE i.durum = $1`;
            params.push(status);
        }
        
        queryText += ` ORDER BY i.olusturulma_tarihi DESC`;
        
        const result = await db.query(queryText, params);
        
        res.status(200).json({
            success: true,
            count: result.rows.length,
            data: result.rows
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// İhbarı Düzenle / Sil (Soft Delete)
exports.deleteReportAdmin = async (req, res) => {
    const { id } = req.params;
    
    try {
        // Soft delete: Durumu 'Kapali' yapıyoruz
        const query = `
            UPDATE ihbarlar 
            SET durum = 'Kapali', 
                aciklama = aciklama || ' [ADMIN TARAFINDAN KALDIRILDI]'
            WHERE ihbar_id = $1
            RETURNING *
        `;
        
        const result = await db.query(query, [id]);
        
        if (result.rowCount === 0) {
            return res.status(404).json({ success: false, error: 'İhbar bulunamadı.' });
        }
        
        res.status(200).json({
            success: true,
            message: 'İhbar başarıyla kaldırıldı (Soft Delete).',
            data: result.rows[0]
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// --- BESLEME YÖNETİMİ ---

// Tüm Beslemeleri Getir (Admin için)
exports.getAllFeedingsAdmin = async (req, res) => {
    try {
        const query = `
            SELECT b.*, k.kullanici_adi, k.avatar_url 
            FROM beslemeler b
            JOIN kullanicilar k ON b.kullanici_id = k.kullanici_id
            WHERE b.deleted_at IS NULL
            ORDER BY b.besleme_zamani DESC
        `;
        
        const result = await db.query(query);
        
        res.status(200).json({
            success: true,
            count: result.rows.length,
            data: result.rows
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Beslemeyi Sil (Soft Delete)
exports.deleteFeedingAdmin = async (req, res) => {
    const { id } = req.params;
    
    try {
        // Soft delete: deleted_at alanını set ediyoruz
        const query = `
            UPDATE beslemeler 
            SET deleted_at = NOW()
            WHERE besleme_id = $1
            RETURNING *
        `;
        
        const result = await db.query(query, [id]);
        
        if (result.rowCount === 0) {
            return res.status(404).json({ success: false, error: 'Besleme bulunamadı.' });
        }
        
        res.status(200).json({
            success: true,
            message: 'Besleme başarıyla silindi (Soft Delete).',
            data: result.rows[0]
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// ============================================================================
// ❤️ Beğeni Sistemi Controller
// ============================================================================
// Yazar: Muhammed Umut Sibara
// Tarih: 2026-01-08
// Açıklama: İhbar, yorum ve hayvan beğenme işlemleri
// ============================================================================

const db = require('../config/db');

// ============================================================================
// 1. Beğeni Ekle/Kaldır (Toggle)
// ============================================================================
const toggleLike = async (req, res) => {
    try {
        const { kullanici_id, hedef_turu, ihbar_id, yorum_id, hayvan_id } = req.body;

        // Validasyon
        if (!hedef_turu || !['ihbar', 'yorum', 'hayvan'].includes(hedef_turu)) {
            return res.status(400).json({
                success: false,
                message: 'Geçersiz beğeni türü. ihbar, yorum veya hayvan olmalı'
            });
        }

        // Hedef ID kontrolü
        if (hedef_turu === 'ihbar' && !ihbar_id) {
            return res.status(400).json({ success: false, message: 'ihbar_id gerekli' });
        }
        if (hedef_turu === 'yorum' && !yorum_id) {
            return res.status(400).json({ success: false, message: 'yorum_id gerekli' });
        }
        if (hedef_turu === 'hayvan' && !hayvan_id) {
            return res.status(400).json({ success: false, message: 'hayvan_id gerekli' });
        }

        // Mevcut beğeniyi kontrol et
        const existingLike = await db.query(
            `SELECT begeni_id FROM kullanici_begeniler 
             WHERE kullanici_id = $1 AND hedef_turu = $2 
             AND (
                 (hedef_turu = 'ihbar' AND ihbar_id = $3) OR
                 (hedef_turu = 'yorum' AND yorum_id = $4) OR
                 (hedef_turu = 'hayvan' AND hayvan_id = $5)
             )`,
            [kullanici_id, hedef_turu, ihbar_id || null, yorum_id || null, hayvan_id || null]
        );

        let action = '';
        let begeni_sayisi = 0;

        if (existingLike.rows.length > 0) {
            // Beğeni varsa kaldır (UNLIKE)
            await db.query(
                'DELETE FROM kullanici_begeniler WHERE begeni_id = $1',
                [existingLike.rows[0].begeni_id]
            );
            action = 'unliked';

            // Güncel sayıyı al
            if (hedef_turu === 'ihbar') {
                const result = await db.query('SELECT begeni_sayisi FROM ihbarlar WHERE ihbar_id = $1', [ihbar_id]);
                begeni_sayisi = result.rows[0].begeni_sayisi;
            } else if (hedef_turu === 'yorum') {
                const result = await db.query('SELECT begeni_sayisi FROM yorumlar WHERE yorum_id = $1', [yorum_id]);
                begeni_sayisi = result.rows[0].begeni_sayisi;
            } else {
                const result = await db.query('SELECT begeni_sayisi FROM hayvanlar WHERE hayvan_id = $1', [hayvan_id]);
                begeni_sayisi = result.rows[0].begeni_sayisi;
            }

        } else {
            // Beğeni yoksa ekle (LIKE)
            await db.query(
                `INSERT INTO kullanici_begeniler (kullanici_id, hedef_turu, ihbar_id, yorum_id, hayvan_id)
                 VALUES ($1, $2, $3, $4, $5)`,
                [kullanici_id, hedef_turu, ihbar_id || null, yorum_id || null, hayvan_id || null]
            );
            action = 'liked';

            // Güncel sayıyı al
            if (hedef_turu === 'ihbar') {
                const result = await db.query('SELECT begeni_sayisi FROM ihbarlar WHERE ihbar_id = $1', [ihbar_id]);
                begeni_sayisi = result.rows[0].begeni_sayisi;
            } else if (hedef_turu === 'yorum') {
                const result = await db.query('SELECT begeni_sayisi FROM yorumlar WHERE yorum_id = $1', [yorum_id]);
                begeni_sayisi = result.rows[0].begeni_sayisi;
            } else {
                const result = await db.query('SELECT begeni_sayisi FROM hayvanlar WHERE hayvan_id = $1', [hayvan_id]);
                begeni_sayisi = result.rows[0].begeni_sayisi;
            }
        }

        res.status(200).json({
            success: true,
            action: action,
            message: action === 'liked' ? 'Beğenildi' : 'Beğeni kaldırıldı',
            begeni_sayisi: begeni_sayisi,
            data: {
                hedef_turu,
                begeni_sayisi
            }
        });

    } catch (error) {
        console.error('Beğeni toggle hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Beğeni işlemi sırasında bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// 2. Kullanıcının Beğendiği İçerikleri Getir
// ============================================================================
const getUserLikes = async (req, res) => {
    try {
        const { kullanici_id } = req.params;
        const { hedef_turu, limit = 50, offset = 0 } = req.query;

        let query = `
            SELECT 
                kb.begeni_id,
                kb.hedef_turu,
                kb.olusturulma_tarihi,
                kb.ihbar_id,
                kb.yorum_id,
                kb.hayvan_id
        `;

        // Hedef türüne göre JOIN ekle
        if (hedef_turu === 'ihbar' || !hedef_turu) {
            query += `,
                i.baslik as ihbar_baslik,
                i.aciklama as ihbar_aciklama,
                i.kategori,
                f.dosya_yolu as ihbar_fotograf
            `;
        }

        query += `
            FROM kullanici_begeniler kb
        `;

        if (hedef_turu === 'ihbar' || !hedef_turu) {
            query += `
                LEFT JOIN ihbarlar i ON kb.ihbar_id = i.ihbar_id
                LEFT JOIN fotograflar f ON i.foto_id = f.foto_id
            `;
        }

        query += `
            WHERE kb.kullanici_id = $1
        `;

        if (hedef_turu) {
            query += ` AND kb.hedef_turu = $2`;
        }

        query += `
            ORDER BY kb.olusturulma_tarihi DESC
            LIMIT $${hedef_turu ? '3' : '2'} OFFSET $${hedef_turu ? '4' : '3'}
        `;

        const params = hedef_turu 
            ? [kullanici_id, hedef_turu, limit, offset]
            : [kullanici_id, limit, offset];

        const result = await db.query(query, params);

        res.status(200).json({
            success: true,
            data: result.rows,
            count: result.rows.length
        });

    } catch (error) {
        console.error('Kullanıcı beğenileri listeleme hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Beğeniler listelenirken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// 3. Belirli Bir İçeriği Beğenen Kullanıcıları Getir
// ============================================================================
const getLikesByContent = async (req, res) => {
    try {
        const { hedef_turu, hedef_id } = req.params;
        const { limit = 50, offset = 0 } = req.query;

        if (!hedef_turu || !['ihbar', 'yorum', 'hayvan'].includes(hedef_turu)) {
            return res.status(400).json({
                success: false,
                message: 'Geçersiz hedef türü'
            });
        }

        let whereClause = '';
        if (hedef_turu === 'ihbar') whereClause = 'kb.ihbar_id = $1';
        if (hedef_turu === 'yorum') whereClause = 'kb.yorum_id = $1';
        if (hedef_turu === 'hayvan') whereClause = 'kb.hayvan_id = $1';

        const result = await db.query(
            `SELECT 
                k.kullanici_id,
                k.kullanici_adi,
                k.avatar_url,
                k.rutbe,
                kb.olusturulma_tarihi as begeni_tarihi
             FROM kullanici_begeniler kb
             JOIN kullanicilar k ON kb.kullanici_id = k.kullanici_id
             WHERE kb.hedef_turu = $1 AND ${whereClause}
             ORDER BY kb.olusturulma_tarihi DESC
             LIMIT $2 OFFSET $3`,
            [hedef_turu, hedef_id, limit, offset]
        );

        res.status(200).json({
            success: true,
            data: result.rows,
            count: result.rows.length
        });

    } catch (error) {
        console.error('İçerik beğenileri listeleme hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Beğeniler listelenirken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// 4. Kullanıcının Belirli İçeriği Beğenip Beğenmediğini Kontrol Et
// ============================================================================
const checkUserLike = async (req, res) => {
    try {
        const { kullanici_id, hedef_turu, hedef_id } = req.params;

        let whereClause = '';
        if (hedef_turu === 'ihbar') whereClause = 'ihbar_id = $3';
        if (hedef_turu === 'yorum') whereClause = 'yorum_id = $3';
        if (hedef_turu === 'hayvan') whereClause = 'hayvan_id = $3';

        const result = await db.query(
            `SELECT begeni_id FROM kullanici_begeniler 
             WHERE kullanici_id = $1 AND hedef_turu = $2 AND ${whereClause}`,
            [kullanici_id, hedef_turu, hedef_id]
        );

        res.status(200).json({
            success: true,
            liked: result.rows.length > 0,
            data: {
                begenildi: result.rows.length > 0
            }
        });

    } catch (error) {
        console.error('Beğeni kontrol hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Beğeni kontrolü sırasında bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// 5. Toplu Beğeni Durumu Kontrolü (Feed için optimize edilmiş)
// ============================================================================
const checkMultipleLikes = async (req, res) => {
    try {
        const { kullanici_id, hedef_turu, hedef_ids } = req.body;

        if (!Array.isArray(hedef_ids) || hedef_ids.length === 0) {
            return res.status(400).json({
                success: false,
                message: 'hedef_ids array gerekli'
            });
        }

        let columnName = '';
        if (hedef_turu === 'ihbar') columnName = 'ihbar_id';
        if (hedef_turu === 'yorum') columnName = 'yorum_id';
        if (hedef_turu === 'hayvan') columnName = 'hayvan_id';

        const result = await db.query(
            `SELECT ${columnName} as hedef_id
             FROM kullanici_begeniler
             WHERE kullanici_id = $1 AND hedef_turu = $2 AND ${columnName} = ANY($3)`,
            [kullanici_id, hedef_turu, hedef_ids]
        );

        // ID'leri set'e çevir
        const likedIds = new Set(result.rows.map(row => row.hedef_id));

        // Her ID için beğeni durumunu döndür
        const likeStatus = hedef_ids.reduce((acc, id) => {
            acc[id] = likedIds.has(id);
            return acc;
        }, {});

        res.status(200).json({
            success: true,
            data: likeStatus
        });

    } catch (error) {
        console.error('Toplu beğeni kontrol hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Beğeni kontrolü sırasında bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// EXPORT
// ============================================================================
module.exports = {
    toggleLike,
    getUserLikes,
    getLikesByContent,
    checkUserLike,
    checkMultipleLikes
};

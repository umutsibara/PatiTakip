// ============================================================================
// 💬 Yorum Sistemi Controller
// ============================================================================
// Yazar: Muhammed Umut Sibara
// Tarih: 2026-01-08
// Açıklama: İhbarlar için yorum ekleme, listeleme, silme işlemleri
// ============================================================================

const db = require('../config/db');

// ============================================================================
// 1. İhbara Yorum Ekle
// ============================================================================
const addComment = async (req, res) => {
    try {
        const { ihbar_id } = req.params;
        const { kullanici_id, yorum_metni, ust_yorum_id } = req.body;

        // Validasyon
        if (!yorum_metni || yorum_metni.trim().length === 0) {
            return res.status(400).json({
                success: false,
                message: 'Yorum metni boş olamaz'
            });
        }

        if (yorum_metni.length > 1000) {
            return res.status(400).json({
                success: false,
                message: 'Yorum maksimum 1000 karakter olabilir'
            });
        }

        // İhbarın var olup olmadığını kontrol et
        const ihbarCheck = await db.query(
            'SELECT ihbar_id FROM ihbarlar WHERE ihbar_id = $1 AND silindi = FALSE',
            [ihbar_id]
        );

        if (ihbarCheck.rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'İhbar bulunamadı'
            });
        }

        // Yorum ekle
        const result = await db.query(
            `INSERT INTO yorumlar (ihbar_id, kullanici_id, yorum_metni, ust_yorum_id)
             VALUES ($1, $2, $3, $4)
             RETURNING yorum_id, yorum_metni, olusturulma_tarihi`,
            [ihbar_id, kullanici_id, yorum_metni.trim(), ust_yorum_id || null]
        );

        // Kullanıcı bilgisini de döndür
        const commentWithUser = await db.query(
            `SELECT y.*, k.kullanici_adi, k.avatar_url, k.rutbe
             FROM yorumlar y
             JOIN kullanicilar k ON y.kullanici_id = k.kullanici_id
             WHERE y.yorum_id = $1`,
            [result.rows[0].yorum_id]
        );

        res.status(201).json({
            success: true,
            message: 'Yorum başarıyla eklendi',
            data: commentWithUser.rows[0]
        });

    } catch (error) {
        console.error('Yorum ekleme hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Yorum eklenirken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// 2. İhbarın Yorumlarını Listele
// ============================================================================
const getCommentsByReport = async (req, res) => {
    try {
        const { ihbar_id } = req.params;
        const { limit = 50, offset = 0 } = req.query;

        // Ana yorumları getir (ust_yorum_id NULL olanlar)
        const comments = await db.query(
            `SELECT 
                y.yorum_id,
                y.ihbar_id,
                y.yorum_metni,
                y.begeni_sayisi,
                y.olusturulma_tarihi,
                y.guncelleme_tarihi,
                y.ust_yorum_id,
                k.kullanici_id,
                k.kullanici_adi,
                k.avatar_url,
                k.rutbe,
                -- Alt yorumların sayısını hesapla
                (SELECT COUNT(*) FROM yorumlar WHERE ust_yorum_id = y.yorum_id AND silindi = FALSE) as cevap_sayisi
             FROM yorumlar y
             JOIN kullanicilar k ON y.kullanici_id = k.kullanici_id
             WHERE y.ihbar_id = $1 AND y.silindi = FALSE AND y.ust_yorum_id IS NULL
             ORDER BY y.olusturulma_tarihi DESC
             LIMIT $2 OFFSET $3`,
            [ihbar_id, limit, offset]
        );

        // Toplam yorum sayısı
        const totalCount = await db.query(
            'SELECT COUNT(*) FROM yorumlar WHERE ihbar_id = $1 AND silindi = FALSE AND ust_yorum_id IS NULL',
            [ihbar_id]
        );

        res.status(200).json({
            success: true,
            data: comments.rows,
            pagination: {
                total: parseInt(totalCount.rows[0].count),
                limit: parseInt(limit),
                offset: parseInt(offset),
                hasMore: parseInt(offset) + comments.rows.length < parseInt(totalCount.rows[0].count)
            }
        });

    } catch (error) {
        console.error('Yorumları listeleme hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Yorumlar listelenirken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// 3. Yoruma Cevap Getir (Alt Yorumlar)
// ============================================================================
const getRepliesByComment = async (req, res) => {
    try {
        const { yorum_id } = req.params;
        const { limit = 20, offset = 0 } = req.query;

        const replies = await db.query(
            `SELECT 
                y.yorum_id,
                y.ihbar_id,
                y.yorum_metni,
                y.begeni_sayisi,
                y.olusturulma_tarihi,
                y.ust_yorum_id,
                k.kullanici_id,
                k.kullanici_adi,
                k.avatar_url,
                k.rutbe
             FROM yorumlar y
             JOIN kullanicilar k ON y.kullanici_id = k.kullanici_id
             WHERE y.ust_yorum_id = $1 AND y.silindi = FALSE
             ORDER BY y.olusturulma_tarihi ASC
             LIMIT $2 OFFSET $3`,
            [yorum_id, limit, offset]
        );

        res.status(200).json({
            success: true,
            data: replies.rows,
            count: replies.rows.length
        });

    } catch (error) {
        console.error('Cevapları listeleme hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Cevaplar listelenirken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// 4. Yorum Sil (Soft Delete)
// ============================================================================
const deleteComment = async (req, res) => {
    try {
        const { yorum_id } = req.params;
        const { kullanici_id } = req.body; // JWT middleware'den gelebilir

        // Yorumun sahibini kontrol et
        const commentCheck = await db.query(
            'SELECT kullanici_id FROM yorumlar WHERE yorum_id = $1 AND silindi = FALSE',
            [yorum_id]
        );

        if (commentCheck.rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Yorum bulunamadı'
            });
        }

        // Yorum sahibi mi kontrol et (veya admin)
        const comment = commentCheck.rows[0];
        if (comment.kullanici_id !== kullanici_id) {
            // Admin kontrolü (opsiyonel)
            const userCheck = await db.query(
                'SELECT rol FROM kullanicilar WHERE kullanici_id = $1',
                [kullanici_id]
            );
            
            if (userCheck.rows.length === 0 || userCheck.rows[0].rol !== 'yonetici') {
                return res.status(403).json({
                    success: false,
                    message: 'Bu yorumu silme yetkiniz yok'
                });
            }
        }

        // Soft delete
        await db.query(
            'UPDATE yorumlar SET silindi = TRUE WHERE yorum_id = $1',
            [yorum_id]
        );

        res.status(200).json({
            success: true,
            message: 'Yorum başarıyla silindi'
        });

    } catch (error) {
        console.error('Yorum silme hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Yorum silinirken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// 5. Yorum Güncelle
// ============================================================================
const updateComment = async (req, res) => {
    try {
        const { yorum_id } = req.params;
        const { kullanici_id, yorum_metni } = req.body;

        // Validasyon
        if (!yorum_metni || yorum_metni.trim().length === 0) {
            return res.status(400).json({
                success: false,
                message: 'Yorum metni boş olamaz'
            });
        }

        // Yorum sahibi kontrolü
        const commentCheck = await db.query(
            'SELECT kullanici_id FROM yorumlar WHERE yorum_id = $1 AND silindi = FALSE',
            [yorum_id]
        );

        if (commentCheck.rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Yorum bulunamadı'
            });
        }

        if (commentCheck.rows[0].kullanici_id !== kullanici_id) {
            return res.status(403).json({
                success: false,
                message: 'Bu yorumu güncelleme yetkiniz yok'
            });
        }

        // Güncelle
        const result = await db.query(
            `UPDATE yorumlar 
             SET yorum_metni = $1, guncelleme_tarihi = CURRENT_TIMESTAMP
             WHERE yorum_id = $2
             RETURNING *`,
            [yorum_metni.trim(), yorum_id]
        );

        res.status(200).json({
            success: true,
            message: 'Yorum başarıyla güncellendi',
            data: result.rows[0]
        });

    } catch (error) {
        console.error('Yorum güncelleme hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Yorum güncellenirken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// 6. Kullanıcının Tüm Yorumlarını Getir
// ============================================================================
const getCommentsByUser = async (req, res) => {
    try {
        const { kullanici_id } = req.params;
        const { limit = 20, offset = 0 } = req.query;

        const comments = await db.query(
            `SELECT 
                y.yorum_id,
                y.ihbar_id,
                y.yorum_metni,
                y.begeni_sayisi,
                y.olusturulma_tarihi,
                i.baslik as ihbar_baslik,
                i.kategori
             FROM yorumlar y
             JOIN ihbarlar i ON y.ihbar_id = i.ihbar_id
             WHERE y.kullanici_id = $1 AND y.silindi = FALSE
             ORDER BY y.olusturulma_tarihi DESC
             LIMIT $2 OFFSET $3`,
            [kullanici_id, limit, offset]
        );

        res.status(200).json({
            success: true,
            data: comments.rows,
            count: comments.rows.length
        });

    } catch (error) {
        console.error('Kullanıcı yorumları listeleme hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Yorumlar listelenirken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// EXPORT
// ============================================================================
module.exports = {
    addComment,
    getCommentsByReport,
    getRepliesByComment,
    deleteComment,
    updateComment,
    getCommentsByUser
};

// ============================================================================
// 💬 Chat/Mesajlaşma Sistemi Controller
// ============================================================================
// Yazar: Muhammed Umut Sibara
// Tarih: 2026-01-08
// Açıklama: Kullanıcılar arası mesajlaşma sistemi
// ============================================================================

const db = require('../config/db');

// ============================================================================
// 1. Yeni Sohbet Oluştur veya Mevcut Sohbeti Getir
// ============================================================================
const createOrGetChat = async (req, res) => {
    try {
        const { kullanici_id_1, kullanici_id_2 } = req.body;

        if (!kullanici_id_1 || !kullanici_id_2) {
            return res.status(400).json({
                success: false,
                message: 'İki kullanıcı ID gerekli'
            });
        }

        if (kullanici_id_1 === kullanici_id_2) {
            return res.status(400).json({
                success: false,
                message: 'Kendinizle sohbet oluşturamazsınız'
            });
        }

        // Mevcut bireysel sohbet var mı kontrol et
        const existingChat = await db.query(
            `SELECT DISTINCT s.sohbet_id
             FROM sohbetler s
             JOIN sohbet_katilimcilar sk1 ON s.sohbet_id = sk1.sohbet_id
             JOIN sohbet_katilimcilar sk2 ON s.sohbet_id = sk2.sohbet_id
             WHERE s.sohbet_turu = 'bireysel'
             AND sk1.kullanici_id = $1 
             AND sk2.kullanici_id = $2
             AND sk1.ayrıldi = FALSE
             AND sk2.ayrıldi = FALSE`,
            [kullanici_id_1, kullanici_id_2]
        );

        if (existingChat.rows.length > 0) {
            // Mevcut sohbet varsa detaylarını döndür
            const chatDetails = await getChatDetails(existingChat.rows[0].sohbet_id, kullanici_id_1);
            return res.status(200).json({
                success: true,
                message: 'Mevcut sohbet bulundu',
                data: chatDetails
            });
        }

        // Yeni sohbet oluştur
        const newChat = await db.query(
            `INSERT INTO sohbetler (sohbet_turu)
             VALUES ('bireysel')
             RETURNING sohbet_id, olusturulma_tarihi`,
            []
        );

        const sohbet_id = newChat.rows[0].sohbet_id;

        // İki kullanıcıyı da katılımcı olarak ekle
        await db.query(
            `INSERT INTO sohbet_katilimcilar (sohbet_id, kullanici_id)
             VALUES ($1, $2), ($1, $3)`,
            [sohbet_id, kullanici_id_1, kullanici_id_2]
        );

        // Sohbet detaylarını getir
        const chatDetails = await getChatDetails(sohbet_id, kullanici_id_1);

        res.status(201).json({
            success: true,
            message: 'Yeni sohbet oluşturuldu',
            data: chatDetails
        });

    } catch (error) {
        console.error('Sohbet oluşturma hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Sohbet oluşturulurken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// Yardımcı Fonksiyon: Sohbet Detayları
// ============================================================================
async function getChatDetails(sohbet_id, current_user_id) {
    const chatInfo = await db.query(
        `SELECT 
            s.sohbet_id,
            s.sohbet_turu,
            s.sohbet_adi,
            s.sohbet_resmi,
            s.son_mesaj,
            s.son_mesaj_tarihi,
            s.olusturulma_tarihi,
            sk.okunmamis_mesaj_sayisi,
            -- Diğer kullanıcı bilgisi (bireysel sohbet için)
            k.kullanici_id as diger_kullanici_id,
            k.kullanici_adi as diger_kullanici_adi,
            k.avatar_url as diger_kullanici_avatar
         FROM sohbetler s
         JOIN sohbet_katilimcilar sk ON s.sohbet_id = sk.sohbet_id
         LEFT JOIN sohbet_katilimcilar sk2 ON s.sohbet_id = sk2.sohbet_id AND sk2.kullanici_id != $2
         LEFT JOIN kullanicilar k ON sk2.kullanici_id = k.kullanici_id
         WHERE s.sohbet_id = $1 AND sk.kullanici_id = $2`,
        [sohbet_id, current_user_id]
    );

    return chatInfo.rows[0] || null;
}

// ============================================================================
// 2. Kullanıcının Tüm Sohbetlerini Listele
// ============================================================================
const getUserChats = async (req, res) => {
    try {
        const { kullanici_id } = req.params;
        const { limit = 50, offset = 0 } = req.query;

        const chats = await db.query(
            `SELECT 
                s.sohbet_id,
                s.sohbet_turu,
                s.sohbet_adi,
                s.sohbet_resmi,
                s.son_mesaj,
                s.son_mesaj_tarihi,
                s.olusturulma_tarihi,
                sk.okunmamis_mesaj_sayisi,
                sk.son_goruntuleme_tarihi,
                -- Diğer kullanıcı bilgisi (bireysel sohbet için)
                CASE 
                    WHEN s.sohbet_turu = 'bireysel' THEN (
                        SELECT k.kullanici_adi 
                        FROM sohbet_katilimcilar sk2 
                        JOIN kullanicilar k ON sk2.kullanici_id = k.kullanici_id
                        WHERE sk2.sohbet_id = s.sohbet_id AND sk2.kullanici_id != $1
                        LIMIT 1
                    )
                    ELSE s.sohbet_adi
                END as goruntuleme_adi,
                CASE 
                    WHEN s.sohbet_turu = 'bireysel' THEN (
                        SELECT k.avatar_url 
                        FROM sohbet_katilimcilar sk2 
                        JOIN kullanicilar k ON sk2.kullanici_id = k.kullanici_id
                        WHERE sk2.sohbet_id = s.sohbet_id AND sk2.kullanici_id != $1
                        LIMIT 1
                    )
                    ELSE s.sohbet_resmi
                END as goruntuleme_resmi,
                -- Diğer kullanıcı ID (bireysel için)
                CASE 
                    WHEN s.sohbet_turu = 'bireysel' THEN (
                        SELECT sk2.kullanici_id
                        FROM sohbet_katilimcilar sk2 
                        WHERE sk2.sohbet_id = s.sohbet_id AND sk2.kullanici_id != $1
                        LIMIT 1
                    )
                    ELSE NULL
                END as diger_kullanici_id
             FROM sohbetler s
             JOIN sohbet_katilimcilar sk ON s.sohbet_id = sk.sohbet_id
             WHERE sk.kullanici_id = $1 AND sk.ayrıldi = FALSE AND s.aktif = TRUE
             ORDER BY COALESCE(s.son_mesaj_tarihi, s.olusturulma_tarihi) DESC
             LIMIT $2 OFFSET $3`,
            [kullanici_id, limit, offset]
        );

        res.status(200).json({
            success: true,
            data: chats.rows,
            count: chats.rows.length
        });

    } catch (error) {
        console.error('Sohbetleri listeleme hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Sohbetler listelenirken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// 3. Mesaj Gönder
// ============================================================================
const sendMessage = async (req, res) => {
    try {
        const { sohbet_id } = req.params;
        const { 
            gonderen_id, 
            mesaj_metni, 
            mesaj_turu = 'metin',
            dosya_url,
            konum_enlem,
            konum_boylam,
            cevaplanan_mesaj_id 
        } = req.body;

        // Validasyon
        if (!mesaj_metni && !dosya_url && (!konum_enlem || !konum_boylam)) {
            return res.status(400).json({
                success: false,
                message: 'Mesaj içeriği boş olamaz'
            });
        }

        // Kullanıcının sohbete katılımcı olduğunu kontrol et
        const participantCheck = await db.query(
            'SELECT katilimci_id FROM sohbet_katilimcilar WHERE sohbet_id = $1 AND kullanici_id = $2 AND ayrıldi = FALSE',
            [sohbet_id, gonderen_id]
        );

        if (participantCheck.rows.length === 0) {
            return res.status(403).json({
                success: false,
                message: 'Bu sohbete mesaj gönderme yetkiniz yok'
            });
        }

        // Mesajı ekle
        const result = await db.query(
            `INSERT INTO mesajlar (
                sohbet_id, gonderen_id, mesaj_metni, mesaj_turu,
                dosya_url, konum_enlem, konum_boylam, cevaplanan_mesaj_id
             )
             VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
             RETURNING *`,
            [
                sohbet_id, gonderen_id, mesaj_metni, mesaj_turu,
                dosya_url, konum_enlem, konum_boylam, cevaplanan_mesaj_id
            ]
        );

        // Gönderen bilgisini ekle
        const messageWithSender = await db.query(
            `SELECT m.*, k.kullanici_adi, k.avatar_url
             FROM mesajlar m
             JOIN kullanicilar k ON m.gonderen_id = k.kullanici_id
             WHERE m.mesaj_id = $1`,
            [result.rows[0].mesaj_id]
        );

        res.status(201).json({
            success: true,
            message: 'Mesaj gönderildi',
            data: messageWithSender.rows[0]
        });

    } catch (error) {
        console.error('Mesaj gönderme hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Mesaj gönderilirken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// 4. Sohbetin Mesajlarını Getir
// ============================================================================
const getChatMessages = async (req, res) => {
    try {
        const { sohbet_id } = req.params;
        const { kullanici_id } = req.query;
        const { limit = 50, offset = 0, before_mesaj_id } = req.query;

        // Kullanıcının sohbete erişim yetkisi var mı kontrol et
        const accessCheck = await db.query(
            'SELECT katilimci_id FROM sohbet_katilimcilar WHERE sohbet_id = $1 AND kullanici_id = $2',
            [sohbet_id, kullanici_id]
        );

        if (accessCheck.rows.length === 0) {
            return res.status(403).json({
                success: false,
                message: 'Bu sohbete erişim yetkiniz yok'
            });
        }

        let query = `
            SELECT 
                m.mesaj_id,
                m.sohbet_id,
                m.mesaj_metni,
                m.mesaj_turu,
                m.dosya_url,
                m.konum_enlem,
                m.konum_boylam,
                m.gonderildi,
                m.iletildi,
                m.okundu,
                m.cevaplanan_mesaj_id,
                k.kullanici_id as gonderen_id,
                k.kullanici_adi as gonderen_adi,
                k.avatar_url as gonderen_avatar,
                -- Cevaplanan mesaj bilgisi
                cm.mesaj_metni as cevaplanan_mesaj_metni,
                ck.kullanici_adi as cevaplanan_mesaj_gonderen
            FROM mesajlar m
            JOIN kullanicilar k ON m.gonderen_id = k.kullanici_id
            LEFT JOIN mesajlar cm ON m.cevaplanan_mesaj_id = cm.mesaj_id
            LEFT JOIN kullanicilar ck ON cm.gonderen_id = ck.kullanici_id
            WHERE m.sohbet_id = $1 AND m.silindi = FALSE
        `;

        const params = [sohbet_id];
        let paramIndex = 2;

        // Pagination için before_mesaj_id kullanımı (daha eski mesajları getir)
        if (before_mesaj_id) {
            query += ` AND m.mesaj_id < $${paramIndex}`;
            params.push(before_mesaj_id);
            paramIndex++;
        }

        query += ` ORDER BY m.gonderildi DESC LIMIT $${paramIndex} OFFSET $${paramIndex + 1}`;
        params.push(limit, offset);

        const messages = await db.query(query, params);

        // Mesajları ters çevir (en eski üstte)
        const reversedMessages = messages.rows.reverse();

        res.status(200).json({
            success: true,
            data: reversedMessages,
            count: reversedMessages.length,
            hasMore: reversedMessages.length === parseInt(limit)
        });

    } catch (error) {
        console.error('Mesajları getirme hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Mesajlar getirilirken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// 5. Mesajları Okundu Olarak İşaretle
// ============================================================================
const markMessagesAsRead = async (req, res) => {
    try {
        const { sohbet_id } = req.params;
        const { kullanici_id } = req.body;

        // Kullanıcının tüm okunmamış mesajlarını okundu olarak işaretle
        await db.query(
            `UPDATE mesajlar
             SET okundu = CURRENT_TIMESTAMP
             WHERE sohbet_id = $1 
             AND gonderen_id != $2 
             AND okundu IS NULL`,
            [sohbet_id, kullanici_id]
        );

        // Okunmamış mesaj sayacını sıfırla
        await db.query(
            `UPDATE sohbet_katilimcilar
             SET okunmamis_mesaj_sayisi = 0,
                 son_goruntuleme_tarihi = CURRENT_TIMESTAMP
             WHERE sohbet_id = $1 AND kullanici_id = $2`,
            [sohbet_id, kullanici_id]
        );

        res.status(200).json({
            success: true,
            message: 'Mesajlar okundu olarak işaretlendi'
        });

    } catch (error) {
        console.error('Mesajları okundu işaretleme hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Mesajlar güncellenirken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// 6. Mesaj Sil
// ============================================================================
const deleteMessage = async (req, res) => {
    try {
        const { mesaj_id } = req.params;
        const { kullanici_id } = req.body;

        // Mesajın sahibini kontrol et
        const messageCheck = await db.query(
            'SELECT gonderen_id FROM mesajlar WHERE mesaj_id = $1 AND silindi = FALSE',
            [mesaj_id]
        );

        if (messageCheck.rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'Mesaj bulunamadı'
            });
        }

        if (messageCheck.rows[0].gonderen_id !== kullanici_id) {
            return res.status(403).json({
                success: false,
                message: 'Bu mesajı silme yetkiniz yok'
            });
        }

        // Soft delete
        await db.query(
            'UPDATE mesajlar SET silindi = TRUE WHERE mesaj_id = $1',
            [mesaj_id]
        );

        res.status(200).json({
            success: true,
            message: 'Mesaj silindi'
        });

    } catch (error) {
        console.error('Mesaj silme hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Mesaj silinirken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// 7. Sohbet Arama (Kullanıcı ara)
// ============================================================================
const searchUsers = async (req, res) => {
    try {
        const { arama_metni } = req.query;
        const { current_user_id } = req.query;

        if (!arama_metni || arama_metni.trim().length < 2) {
            return res.status(400).json({
                success: false,
                message: 'En az 2 karakter girin'
            });
        }

        const users = await db.query(
            `SELECT 
                kullanici_id,
                kullanici_adi,
                tam_isim,
                avatar_url,
                rutbe,
                puan
             FROM kullanicilar
             WHERE aktif = TRUE 
             AND kullanici_id != $1
             AND (
                 kullanici_adi ILIKE $2 
                 OR tam_isim ILIKE $2
             )
             ORDER BY puan DESC
             LIMIT 20`,
            [current_user_id, `%${arama_metni}%`]
        );

        res.status(200).json({
            success: true,
            data: users.rows,
            count: users.rows.length
        });

    } catch (error) {
        console.error('Kullanıcı arama hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Kullanıcılar aranırken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// 8. Okunmamış Mesaj Sayısı (Tüm Sohbetler)
// ============================================================================
const getUnreadCount = async (req, res) => {
    try {
        const { kullanici_id } = req.params;

        const result = await db.query(
            `SELECT COALESCE(SUM(okunmamis_mesaj_sayisi), 0) as toplam_okunmamis
             FROM sohbet_katilimcilar
             WHERE kullanici_id = $1 AND ayrıldi = FALSE`,
            [kullanici_id]
        );

        res.status(200).json({
            success: true,
            data: {
                toplam_okunmamis: parseInt(result.rows[0].toplam_okunmamis)
            }
        });

    } catch (error) {
        console.error('Okunmamış sayısı getirme hatası:', error);
        res.status(500).json({
            success: false,
            message: 'Okunmamış mesaj sayısı alınırken bir hata oluştu',
            error: error.message
        });
    }
};

// ============================================================================
// EXPORT
// ============================================================================
module.exports = {
    createOrGetChat,
    getUserChats,
    sendMessage,
    getChatMessages,
    markMessagesAsRead,
    deleteMessage,
    searchUsers,
    getUnreadCount
};

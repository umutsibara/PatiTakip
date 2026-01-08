const db = require('../config/db');

// Kullanıcı Kaydı
exports.register = async (req, res) => {
    const { kullanici_adi, eposta, sifre } = req.body;

    if (!kullanici_adi || !eposta || !sifre) {
        return res.status(400).json({ success: false, error: 'Tüm alanlar zorunludur.' });
    }

    try {
        const bcrypt = require('bcrypt');
        
        // Şifreyi hashle (10 salt rounds - production için ideal)
        const sifre_hash = await bcrypt.hash(sifre, 10);
        
        const query = `
            INSERT INTO kullanicilar (kullanici_adi, eposta, sifre_hash, puan, rutbe, konum, avatar_url)
            VALUES ($1, $2, $3, 0, 0, '', '')
            RETURNING kullanici_id, kullanici_adi, rol, puan, rutbe, konum, avatar_url;
        `;
        
        const result = await db.query(query, [kullanici_adi, eposta, sifre_hash]);
        
        res.status(201).json({
            success: true,
            data: result.rows[0],
            message: 'Kullanıcı başarıyla oluşturuldu.'
        });
    } catch (error) {
        // Unique constraint hatası (Aynı email veya kullanıcı adı)
        if (error.code === '23505') {
            return res.status(409).json({ success: false, error: 'Bu kullanıcı adı veya e-posta zaten kayıtlı.' });
        }
        res.status(500).json({ success: false, error: error.message });
    }
};

// Basit Giriş (Login)
exports.login = async (req, res) => {
    const { eposta, sifre } = req.body;

    try {
        const query = 'SELECT * FROM kullanicilar WHERE eposta = $1';
        const result = await db.query(query, [eposta]);

        if (result.rows.length === 0) {
            return res.status(404).json({ success: false, error: 'Kullanıcı bulunamadı.' });
        }

        const user = result.rows[0];
        const bcrypt = require('bcrypt');
        const jwt = require('jsonwebtoken');
        const { JWT_SECRET } = require('../middleware/authMiddleware');

        // Şifreyi hashlenmiş versiyonla karşılaştır
        const sifreGecerli = await bcrypt.compare(sifre, user.sifre_hash);
        
        if (!sifreGecerli) {
            return res.status(401).json({ success: false, error: 'Hatalı şifre.' });
        }

        // Token oluştur (24 saat geçerli)
        const token = jwt.sign(
            { id: user.kullanici_id, eposta: user.eposta, rol: user.rol },
            JWT_SECRET,
            { expiresIn: '24h' }
        );

        res.status(200).json({
            success: true,
            message: 'Giriş başarılı.',
            data: {
                id: user.kullanici_id,
                isim: user.kullanici_adi,
                rol: user.rol,
                eposta: user.eposta,
                avatar_url: user.avatar_url,
                puan: user.puan,
                rutbe: user.rutbe,
                konum: user.konum,
                token: token
            }
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Kullanıcı Profili ve İstatistikleri (View Kullanımı)
exports.getProfile = async (req, res) => {
    const { id } = req.params;

    try {
        // 1. Kullanıcının Temel Bilgileri
        // View yerine doğrudan tablo sorguluyoruz, yeni alanlar için.
        const userQuery = `
            SELECT kullanici_id, kullanici_adi, eposta, puan, rutbe, rol, konum, avatar_url, olusturulma_tarihi 
            FROM kullanicilar 
            WHERE kullanici_id = $1
        `;
        const userResult = await db.query(userQuery, [id]);

        if (userResult.rows.length === 0) {
            return res.status(404).json({ success: false, error: 'Kullanıcı bulunamadı.' });
        }

        const user = userResult.rows[0];
        
        // E-posta maskeleme (Güvenlik)
        // Eğer kendi profili ise tam göster, değilse maskele? Şimdilik basitçe maskeleyelim veya client'ın kararına bırakalım.
        // Backend gereksinimlerinde eposta istenmiş.
        
        // 2. Rozetler
        const badgesQuery = `
            SELECT r.*, kr.kazanilma_tarihi 
            FROM rozetler r 
            JOIN kullanici_rozetleri kr ON r.rozet_id = kr.rozet_id 
            WHERE kr.kullanici_id = $1
        `;
        const badgesResult = await db.query(badgesQuery, [id]);

        // 3. İstatistikler (View yerine doğrudan sorgu)
        const statsQuery = `
            SELECT 
                (SELECT COUNT(*) FROM ihbarlar WHERE kullanici_id = $1) as ihbar_sayisi,
                (SELECT COUNT(*) FROM beslemeler WHERE kullanici_id = $1) as besleme_sayisi
        `;
        const statsResult = await db.query(statsQuery, [id]); // ID kullanıyoruz artık, isim değil
        
        const rawStats = statsResult.rows[0];
        const totalContribution = parseInt(rawStats.ihbar_sayisi) + parseInt(rawStats.besleme_sayisi);

        const finalStats = {
            ihbar_sayisi: parseInt(rawStats.ihbar_sayisi),
            besleme_sayisi: parseInt(rawStats.besleme_sayisi),
            toplam_katki: totalContribution
        };

        res.status(200).json({
            success: true,
            user: {
                ...user,
                badges: badgesResult.rows
            },
            stats: finalStats
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Liderlik Tablosu
exports.getLeaderboard = async (req, res) => {
    try {
        const query = `
            SELECT kullanici_id, kullanici_adi, puan, rutbe, avatar_url, konum
            FROM kullanicilar
            ORDER BY puan DESC
            LIMIT 20
        `;
        const result = await db.query(query);

        res.status(200).json(result.rows);
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

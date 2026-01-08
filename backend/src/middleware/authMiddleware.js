const jwt = require('jsonwebtoken');

// Sabit bir secret key (Normalde .env'de olmalı)
const JWT_SECRET = process.env.JWT_SECRET || 'gizli_anahtar_pati_takip_2025';

// 1. Token Doğrulama Middleware
const authenticateToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    // Bearer <TOKEN> formatında gelir
    const token = authHeader && authHeader.split(' ')[1];

    if (!token) {
        return res.status(401).json({ success: false, error: 'Erişim reddedildi. Token bulunamadı.' });
    }

    jwt.verify(token, JWT_SECRET, (err, user) => {
        if (err) {
            return res.status(403).json({ success: false, error: 'Geçersiz veya süresi dolmuş token.' });
        }
        // Token geçerliyse user bilgisini request'e ekle
        req.user = user;
        next();
    });
};

// 2. Yönetici Kontrolü Middleware
const isAdmin = (req, res, next) => {
    // authenticateToken'dan sonra çalışır, req.user doludur.
    if (req.user && req.user.rol === 'yonetici') {
        next();
    } else {
        res.status(403).json({ success: false, error: 'Bu işlem için yetkiniz yok (Yönetici yetkisi gerekir).' });
    }
};

module.exports = {
    authenticateToken,
    isAdmin,
    JWT_SECRET
};

const db = require('../config/db');

const loggerMiddleware = async (req, res, next) => {
    // Statik dosya isteklerini loglama
    if (req.url.startsWith('/uploads')) {
        return next();
    }

    // Response bitince logla (finish event)
    res.on('finish', async () => {
        // Sadece başarılı veya istemci hatası olan istekleri loglayalım (500'ler error handler'da)
        // Veya her şeyi loglayabiliriz. Profesyonel olan her şeyi loglamaktır.
        
        const kullanici_id = req.user ? req.user.id : null;
        const ip_adresi = req.headers['x-forwarded-for'] || req.socket.remoteAddress;
        const method = req.method;
        const url = req.originalUrl;
        
        let islem_turu = 'DIGER';
        if (method === 'GET') islem_turu = 'GORUNTULEME';
        if (method === 'POST') islem_turu = 'EKLEME';
        if (method === 'PUT') islem_turu = 'GUNCELLEME';
        if (method === 'DELETE') islem_turu = 'SILME';

        // Login özel durumu
        if (url.includes('/login')) islem_turu = 'GIRIS';
        if (url.includes('/register')) islem_turu = 'KAYIT';

        const aciklama = `${method} ${url} - Status: ${res.statusCode}`;

        // Veritabanına Asenkron Kayıt (Await etmiyoruz, client beklemesin)
        try {
            const query = `
                INSERT INTO kullanici_loglari (kullanici_id, islem_turu, aciklama, ip_adresi)
                VALUES ($1, $2, $3, $4)
            `;
            // Log hatası akışı bozmasın diye catch bloğu içinde
            db.query(query, [kullanici_id, islem_turu, aciklama, ip_adresi]).catch(err => {
                console.error('Loglama Hatası:', err.message);
            });
        } catch (error) {
            console.error('Loglama Middleware Hatası:', error);
        }
    });

    next();
};

module.exports = loggerMiddleware;

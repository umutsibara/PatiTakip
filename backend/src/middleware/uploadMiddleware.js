const multer = require('multer');
const sharp = require('sharp');
const path = require('path');
const fs = require('fs');
const db = require('../config/db');

// Depolama ayarları (Geçici olarak memory'de tutuyoruz, işleyip diske yazacağız)
const storage = multer.memoryStorage();

// Dosya filtresi (Sadece resim)
const fileFilter = (req, file, cb) => {
    if (file.mimetype.startsWith('image/')) {
        cb(null, true);
    } else {
        cb(new Error('Sadece resim dosyaları yüklenebilir!'), false);
    }
};

const upload = multer({
    storage: storage,
    limits: {
        fileSize: 5 * 1024 * 1024 // 5MB limit
    },
    fileFilter: fileFilter
});

// Resim işleme ve kaydetme fonksiyonu
const processAndSaveImage = async (req, res, next) => {
    if (!req.file) return next();

    // ID yoksa veya User yoksa hata ver (Auth middleware'den gelmeli)
    const kullanici_id = req.user ? req.user.id : req.body.kullanici_id;

    if (!kullanici_id) {
         return res.status(401).json({ success: false, error: 'Oturum açmanız gerekiyor.' });
    }

    const filename = `foto-${Date.now()}-${Math.round(Math.random() * 1E9)}.webp`;
    const outputPath = path.join('uploads', 'photos', filename);
    const fullPath = path.join(__dirname, '../../', outputPath);

    try {
        // Sharp ile işle (Resize 1024px genişlik, WebP format, %80 kalite)
        const metadata = await sharp(req.file.buffer)
            .resize(1024, null, { withoutEnlargement: true }) // Sadece genişlik sınırı, oran koru
            .toFormat('webp')
            .webp({ quality: 80 })
            .toFile(fullPath);

        // Veritabanına kaydet
        const query = `
            INSERT INTO fotograflar (kullanici_id, dosya_adi, dosya_yolu, dosya_boyutu, genislik, yukseklik)
            VALUES ($1, $2, $3, $4, $5, $6)
            RETURNING foto_id;
        `;
        
        const result = await db.query(query, [
            kullanici_id, 
            filename, 
            outputPath.replace(/\\\\/g, '/'), // Windows path düzeltme
            metadata.size, 
            metadata.width, 
            metadata.height
        ]);

        // Request nesnesine veri ekle
        req.savedPhotoId = result.rows[0].foto_id;
        req.savedPhotoName = filename;
        next();

    } catch (error) {
        console.error('Fotoğraf işleme hatası:', error);
        return res.status(500).json({ success: false, error: 'Fotoğraf işlenirken hata oluştu.' });
    }
};

module.exports = {
    upload,
    processAndSaveImage
};

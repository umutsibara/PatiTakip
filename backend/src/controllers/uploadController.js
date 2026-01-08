exports.uploadPhoto = (req, res) => {
    // Middleware'den gelen verileri kontrol et
    if (!req.savedPhotoId || !req.savedPhotoName) {
        return res.status(500).json({ success: false, error: 'Dosya yükleme başarısız.' });
    }

    // Tam URL oluştur (Sunucu adresini client bilmeli, biz path dönüyoruz)
    const fileUrl = `/uploads/photos/${req.savedPhotoName}`;

    res.status(201).json({
        success: true,
        message: 'Fotoğraf başarıyla yüklendi.',
        data: {
            foto_id: req.savedPhotoId,
            url: fileUrl
        }
    });
};

// Merkezi Hata Yönetimi
const errorHandler = (err, req, res, next) => {
    console.error(err.stack); // Sunucu konsoluna bas (Loglama için)

    // PostgreSQL Hata Kodları Kontrolü
    let statusCode = 500;
    let message = 'Sunucu tarafında bir hata oluştu.';

    // 23505: Unique Constraint (Çakışma)
    if (err.code === '23505') {
        statusCode = 409;
        message = 'Bu kayıt zaten mevcut (Duplicate Entry).';
    }
    // 23503: Foreign Key Constraint (Geçersiz ID)
    if (err.code === '23503') {
        statusCode = 400;
        message = 'Geçersiz ilişki. Referans verilen kayıt (ID) bulunamadı.';
    }
    // 22P02: Invalid Text Representation (Örn: ID yerine yazı göndermek)
    if (err.code === '22P02') {
        statusCode = 400;
        message = 'Geçersiz veri formatı.';
    }

    res.status(statusCode).json({
        success: false,
        error: message,
        // Prodüksiyon ortamında stack trace gönderilmemeli!
        // details: process.env.NODE_ENV === 'production' ? null : err.message
    });
};

module.exports = errorHandler;

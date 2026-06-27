// Basit Input Validasyonu (Giriş Doğrulama)
// SQL Injection'a karşı ilk savunma hattı input'un tip kontrolüdür.

const validateId = (req, res, next) => {
    const { id } = req.params;
    
    // ID bir sayı mı?
    if (id && isNaN(parseInt(id))) {
        return res.status(400).json({ success: false, error: 'Geçersiz ID formatı. Sayı olmalıdır.' });
    }
    next();
};

const validateReportInput = (req, res, next) => {
    const { ihbar_turu, enlem, boylam } = req.body;

    // Koordinat Sınır Kontrolü
    if (enlem < -90 || enlem > 90 || boylam < -180 || boylam > 180) {
        return res.status(400).json({ success: false, error: 'Geçersiz GPS koordinatları.' });
    }

    const gecerliTurler = ['Aclik', 'Saglik', 'Kayip', 'Genel'];
    if (ihbar_turu && !gecerliTurler.includes(ihbar_turu)) {
        return res.status(400).json({ success: false, error: 'Geçersiz ihbar türü.' });
    }

    next();
};

module.exports = {
    validateId,
    validateReportInput
};

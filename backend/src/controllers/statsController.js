const db = require('../config/db');

// Genel İstatistikler
exports.getGeneralStats = async (req, res) => {
    try {
        // View (vi_gunluk_aktivite) yerine doğrudan sorgu
        const dailyQuery = `
            SELECT 
                CURRENT_DATE as tarih,
                (SELECT COUNT(*) FROM ihbarlar WHERE olusturulma_tarihi::DATE = CURRENT_DATE AND deleted_at IS NULL) as yeni_ihbarlar,
                (SELECT COUNT(*) FROM beslemeler WHERE besleme_zamani::DATE = CURRENT_DATE AND deleted_at IS NULL) as bugunku_beslemeler
        `;
        const daily = await db.query(dailyQuery);
        
        // View (vi_bolge_aclik_durumu) yerine doğrudan sorgu
        const hungerQuery = `
            SELECT 
                b.bolge_adi,
                COUNT(DISTINCT i.ihbar_id) FILTER (WHERE i.ihbar_turu = 'Aclik' AND i.deleted_at IS NULL) as aclik_ihbar_sayisi,
                COUNT(DISTINCT bes.besleme_id) FILTER (WHERE bes.deleted_at IS NULL) as toplam_besleme
            FROM bolgeler b
            LEFT JOIN ihbarlar i ON b.bolge_id = i.bolge_id
            LEFT JOIN beslemeler bes ON b.bolge_id = bes.bolge_id
            WHERE b.deleted_at IS NULL
            GROUP BY b.bolge_id, b.bolge_adi
        `;
        const hunger = await db.query(hungerQuery);

        res.status(200).json({
            success: true,
            bugun: daily.rows[0],
            aclik_durumu: hunger.rows
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Bölge Risk Analizi (Fonksiyon Kullanımı)
exports.getZoneRisk = async (req, res) => {
    const { id } = req.params; // bolge_id

    try {
        // Function Çağrısı: SELECT fn_bolge_risk_seviyesi_getir(id)
        // Function (fn_bolge_risk_seviyesi_getir) yerine doğrudan sorgu ve JS mantığı
        // Risk Seviyesi: Açık durumdaki Sağlık ihbarları sayısı > 5 ise YUKSEK, > 2 ise ORTA, diğer DUSUK
        const result = await db.query(
            `SELECT COUNT(*) as sayi FROM ihbarlar WHERE bolge_id = $1 AND ihbar_turu = 'Saglik' AND durum = 'Acik' AND deleted_at IS NULL`,
            [id]
        );
        
        const count = parseInt(result.rows[0].sayi);
        let riskLevel = 'DUSUK';
        if (count > 5) riskLevel = 'YUKSEK';
        else if (count >= 2) riskLevel = 'ORTA';
        
        res.status(200).json({
            success: true,
            bolge_id: id,
            risk_seviyesi: riskLevel
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

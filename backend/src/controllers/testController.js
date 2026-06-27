const db = require('../config/db');

exports.getTestStats = async (req, res) => {
    try {
        // 1. Önce veritabanındaki mevcut tabloları görelim
        const tableList = await db.query(`
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'public'
            ORDER BY table_name;
        `);

        // 2. Hedef View'ı çekmeye çalışalım
        let viewResult = null;
        let errorDetails = null;
        
        try {
            const result = await db.query('SELECT * FROM vi_aktif_ihbarlar LIMIT 5');
            viewResult = result.rows;
        } catch (err) {
            errorDetails = err.message; // "relation does not exist" hatasını yakala
        }

        res.status(200).json({
            database_name: process.env.DB_NAME, // Hangi DB'ye bağlıyız?
            tables_in_database: tableList.rows.map(t => t.table_name), // İçinde hangi tablolar var?
            view_query_result: viewResult || "Sorgu Başarısız",
            error: errorDetails
        });

    } catch (error) {
        console.error('Genel Hata:', error);
        res.status(500).json({ success: false, error: error.message });
    }
};

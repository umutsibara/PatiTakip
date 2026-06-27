const express = require('express');
const router = express.Router();
const db = require('../config/db');

// Test endpoint to verify database writes are persisting
router.post('/', async (req, res) => {
    const client = await db.getClient();
    
    try {
        await client.query('BEGIN');
        
        // Insert a test report
        const testData = {
            kullanici_id: 1,
            baslik: 'Test Gönderi - ' + new Date().toISOString(),
            aciklama: 'Bu bir test gönderidir. Veritabanı transaction testi.',
            ihbar_turu: 'Genel',
            kategori: 'REPORT',
            hayvan_turu: 'Kedi',
            enlem: 41.0082,
            boylam: 28.9784,
            adres: 'Test Adresi'
        };
        
        const insertQuery = `
            INSERT INTO ihbarlar (kullanici_id, baslik, aciklama, ihbar_turu, kategori, hayvan_turu, enlem, boylam, adres)
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
            RETURNING ihbar_id, baslik, olusturulma_tarihi;
        `;
        
        const insertResult = await client.query(insertQuery, [
            testData.kullanici_id,
            testData.baslik,
            testData.aciklama,
            testData.ihbar_turu,
            testData.kategori,
            testData.hayvan_turu,
            testData.enlem,
            testData.boylam,
            testData.adres
        ]);
        
        await client.query('COMMIT');
        
        // Verify the insert by counting records
        const countResult = await db.query('SELECT COUNT(*) FROM ihbarlar');
        
        res.json({
            success: true,
            message: 'Test kaydı başarıyla eklendi ve commit edildi',
            inserted: insertResult.rows[0],
            totalRecords: parseInt(countResult.rows[0].count)
        });
    } catch (error) {
        await client.query('ROLLBACK');
        res.status(500).json({
            success: false,
            error: error.message
        });
    } finally {
        client.release();
    }
});

// Get total record count
router.get('/count', async (req, res) => {
    try {
        const result = await db.query('SELECT COUNT(*) FROM ihbarlar');
        res.json({
            success: true,
            totalRecords: parseInt(result.rows[0].count)
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            error: error.message
        });
    }
});

module.exports = router;

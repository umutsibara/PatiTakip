const db = require('../config/db');

// Tüm Aktif İhbarları Getir (View Kullanır)
// Helper to map DB row to Frontend JSON
const mapPostToJSON = (row) => ({
    id: row.ihbar_id,
    title: row.baslik || 'Başlıksız',
    description: row.aciklama,
    category: row.kategori,
    type: row.ihbar_turu,
    animalType: row.hayvan_turu,
    location: {
        latitude: row.enlem,
        longitude: row.boylam,
        address: row.adres
    },
    user: {
        id: row.kullanici_id,
        userName: row.kullanici_adi,
        avatarUrl: row.avatar_url,
        rank: row.rutbe,
        points: row.puan
    },
    stats: {
        likes: row.begeni_sayisi,
        comments: row.yorum_sayisi,
        shares: row.paylasim_sayisi
    },
    imageUrls: row.foto_dosya_adi ? [`/uploads/photos/${row.foto_dosya_adi}`] : [],
    createdAt: row.olusturulma_tarihi
});

// Tüm Aktif İhbarları/Gönderileri Getir (Filtreleme ile)
exports.getAllReports = async (req, res) => {
    try {
        const { category, animal_type, lat, lng, radius } = req.query;
        
        let queryText = `
            SELECT i.*, k.kullanici_adi, k.avatar_url, k.rutbe, k.puan, f.dosya_adi as foto_dosya_adi
            FROM ihbarlar i
            JOIN kullanicilar k ON i.kullanici_id = k.kullanici_id
            LEFT JOIN fotograflar f ON i.foto_id = f.foto_id
            WHERE i.durum = 'Acik'
        `;
        const params = [];
        let paramIndex = 1;

        if (category) {
            queryText += ` AND i.kategori = $${paramIndex}`;
            params.push(category);
            paramIndex++;
        }

        if (animal_type) {
            queryText += ` AND i.hayvan_turu = $${paramIndex}`;
            params.push(animal_type);
            paramIndex++;
        }
        
        if (lat && lng) {
            const diff = 0.1; 
             queryText += ` AND i.enlem BETWEEN $${paramIndex} AND $${paramIndex+1} AND i.boylam BETWEEN $${paramIndex+2} AND $${paramIndex+3}`;
             params.push(parseFloat(lat) - diff, parseFloat(lat) + diff, parseFloat(lng) - diff, parseFloat(lng) + diff);
             paramIndex += 4;
        }

        queryText += ' ORDER BY i.olusturulma_tarihi DESC';

        const result = await db.query(queryText, params);
        
        res.status(200).json({
            success: true,
            count: result.rows.length,
            data: result.rows.map(mapPostToJSON)
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Yeni İhbar Oluştur
// Yeni İhbar/Gönderi Oluştur (Transaction ile)
exports.createReport = async (req, res) => {
    const { kullanici_id, baslik, aciklama, ihbar_turu, kategori, hayvan_turu, enlem, boylam, bolge_id, hayvan_id, foto_id, adres } = req.body;

    if (!kullanici_id || !aciklama || !enlem || !boylam) {
        return res.status(400).json({ success: false, error: 'Lütfen zorunlu alanları doldurun.' });
    }

    // Get a client from the pool for transaction
    const client = await db.getClient();
    
    try {
        // BEGIN TRANSACTION
        await client.query('BEGIN');
        
        // İhbar türü mapping (Eski sistem uyumluluğu için)
        // Eğer kategori gelmezse, ihbar_turu'nu kategori olarak kullan veya REPORT yap
        const finalKategori = kategori || 'REPORT';
        const finalIhbarTuru = ihbar_turu || 'Genel'; // Default

        const query = `
            INSERT INTO ihbarlar (kullanici_id, baslik, aciklama, ihbar_turu, kategori, hayvan_turu, enlem, boylam, bolge_id, hayvan_id, foto_id, adres)
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12)
            RETURNING *;
        `;
        
        const values = [
            kullanici_id, 
            baslik || 'Başlıksız Gönderi', 
            aciklama, 
            finalIhbarTuru, 
            finalKategori, 
            hayvan_turu || 'Kedi', // Default olarak artık 'STREET' değil, yaygın bir tür (örn. Kedi) veya boş bırakılabilir.
            enlem, 
            boylam, 
            bolge_id || null, 
            hayvan_id || null, 
            foto_id || null, 
            adres || ''
        ];
        
        const result = await client.query(query, values);

        // Kullanıcıya puan ekle (Gamification) - Aynı transaction içinde
        await client.query('UPDATE kullanicilar SET puan = puan + 10 WHERE kullanici_id = $1', [kullanici_id]);

        // COMMIT TRANSACTION - Tüm değişiklikleri kalıcı yap
        await client.query('COMMIT');
        
        console.log(`✓ Gönderi başarıyla oluşturuldu: ID ${result.rows[0].ihbar_id}`);

        res.status(201).json({
            success: true,
            data: result.rows[0],
            message: 'Gönderi başarıyla oluşturuldu.'
        });
    } catch (error) {
        // ROLLBACK on error
        await client.query('ROLLBACK');
        console.error('✗ Gönderi oluşturma hatası:', error.message);
        res.status(500).json({ success: false, error: error.message });
    } finally {
        // Release client back to pool
        client.release();
    }
};

// İhbarı Çözüldü Olarak İşaretle (Stored Procedure Kullanır)
exports.resolveReport = async (req, res) => {
    const { id } = req.params;
    const { notlar } = req.body;

    try {
        // SP Çağrısı YERİNE Doğrudan SQL Update
        const query = `
            UPDATE ihbarlar 
            SET durum = 'Cozuldu', 
                aciklama = aciklama || ' [ÇÖZÜM: ' || $2 || ']'
            WHERE ihbar_id = $1
            RETURNING *;
        `;
        const result = await db.query(query, [id, notlar || 'Kullanıcı tarafından çözüldü.']);

        if (result.rowCount === 0) {
            return res.status(404).json({ success: false, error: 'İhbar bulunamadı.' });
        }
        
        res.status(200).json({
            success: true,
            message: `İhbar ID ${id} başarıyla çözüldü.`
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};

// Tekil Gönderi Getir (ID ile)
exports.getReportById = async (req, res) => {
    const { id } = req.params;
    try {
        const query = `
            SELECT i.*, k.kullanici_adi, k.avatar_url, k.rutbe, k.puan, f.dosya_adi as foto_dosya_adi
            FROM ihbarlar i
            JOIN kullanicilar k ON i.kullanici_id = k.kullanici_id
            LEFT JOIN fotograflar f ON i.foto_id = f.foto_id
            WHERE i.ihbar_id = $1
        `;
        const result = await db.query(query, [id]);

        if (result.rows.length === 0) {
            return res.status(404).json({ success: false, error: 'Gönderi bulunamadı.' });
        }

        res.status(200).json({
            success: true,
            data: mapPostToJSON(result.rows[0])
        });
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
};


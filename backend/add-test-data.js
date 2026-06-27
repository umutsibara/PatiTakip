const db = require('./src/config/db');

async function addTestData() {
    try {
        console.log('Test verisi ekleniyor...');
        
        // Test ihbarı ekle
        const result = await db.query(`
            INSERT INTO ihbarlar (
                kullanici_id, baslik, aciklama, kategori, ihbar_turu, hayvan_turu,
                enlem, boylam, adres, durum
            ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)
            RETURNING *
        `, [
            4, // umut kullanıcısının ID'si
            'Yaralı sokak köpeği',
            'Parkta yaralı bir köpek gördüm. Acil yardım gerekiyor!',
            'REPORT',
            'Acil',
            'Köpek',
            41.0082,
            28.9784,
            'Kadıköy, İstanbul',
            'Acik'
        ]);
        
        console.log('✅ Test ihbarı eklendi:', result.rows[0]);
        
        // Birkaç tane daha ekle
        await db.query(`
            INSERT INTO ihbarlar (kullanici_id, baslik, aciklama, kategori, hayvan_turu, enlem, boylam, adres)
            VALUES 
            ($1, 'Aç kedi', 'Evimin önünde aç bir kedi var', 'FEEDING', 'Kedi', 41.0092, 28.9794, 'Beşiktaş, İstanbul'),
            ($1, 'Sahiplendirilecek köpek', 'Sevimli köpek yavrusu sahiplendirme için', 'ADOPTION', 'Köpek', 41.0102, 28.9804, 'Şişli, İstanbul')
        `, [4]);
        
        console.log('✅ Toplam 3 test ihbarı eklendi!');
        console.log('\n🎉 Artık feed\'de ihbarları görebilirsiniz!');
        
        process.exit(0);
    } catch (error) {
        console.error('❌ Hata:', error);
        process.exit(1);
    }
}

addTestData();

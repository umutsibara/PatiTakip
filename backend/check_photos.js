const db = require('./src/config/db');

async function checkPhotos() {
    try {
        const query = `
            SELECT foto_id, kullanici_id, dosya_adi, dosya_boyutu, genislik, yukseklik, yuklenme_tarihi 
            FROM fotograflar 
            ORDER BY yuklenme_tarihi DESC 
            LIMIT 5;
        `;
        
        const result = await db.query(query);
        
        console.log('\n=== Veritabanındaki Fotoğraflar ===\n');
        result.rows.forEach(photo => {
            console.log(`ID: ${photo.foto_id}`);
            console.log(`Kullanıcı ID: ${photo.kullanici_id}`);
            console.log(`Dosya Adı: ${photo.dosya_adi}`);
            console.log(`Boyut: ${(photo.dosya_boyutu / 1024).toFixed(2)} KB`);
            console.log(`Boyutlar: ${photo.genislik}x${photo.yukseklik}`);
            console.log(`Yüklenme: ${photo.yuklenme_tarihi}`);
            console.log('---');
        });
        
        console.log(`\nToplam ${result.rows.length} fotoğraf bulundu.`);
        process.exit(0);
    } catch (error) {
        console.error('Hata:', error.message);
        process.exit(1);
    }
}

checkPhotos();

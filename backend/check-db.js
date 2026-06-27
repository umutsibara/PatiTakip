const db = require('./src/config/db');

(async () => {
    try {
        const count = await db.query('SELECT COUNT(*) FROM ihbarlar');
        console.log(`Toplam: ${count.rows[0].count} kayit\n`);
        
        const recent = await db.query('SELECT ihbar_id, baslik, kategori FROM ihbarlar ORDER BY ihbar_id DESC LIMIT 8');
        console.log('Tum kayitlar:');
        recent.rows.forEach(row => console.log(`  ID ${row.ihbar_id}: ${row.baslik || 'Basliks iz'} [${row.kategori}]`));
    } catch (e) {
        console.error('Hata:', e.message);
    } finally {
        process.exit();
    }
})();

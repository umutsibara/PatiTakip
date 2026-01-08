require('dotenv').config();
const { Pool } = require('pg');

const pool = new Pool({
  user: process.env.DB_USER,
  host: process.env.DB_HOST,
  database: process.env.DB_NAME,
  password: process.env.DB_PASSWORD,
  port: process.env.DB_PORT,
});

const makeAdmin = async () => {
    const email = process.argv[2];

    if (!email) {
        console.error('Lütfen bir e-posta adresi belirtin.');
        console.log('Kullanım: node make_admin.js <eposta>');
        process.exit(1);
    }

    try {
        const query = `
            UPDATE kullanicilar 
            SET rol = 'yonetici' 
            WHERE eposta = $1 
            RETURNING kullanici_adi, eposta, rol
        `;
        
        const res = await pool.query(query, [email]);

        if (res.rowCount === 0) {
            console.error(`Hata: '${email}' e-posta adresine sahip kullanıcı bulunamadı.`);
        } else {
            console.log(`Başarılı! Kullanıcı '${res.rows[0].kullanici_adi}' (${res.rows[0].eposta}) artık bir YÖNETİCİ.`);
        }
    } catch (err) {
        console.error('Veritabanı hatası:', err);
    } finally {
        pool.end();
    }
};

makeAdmin();

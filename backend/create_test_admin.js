require('dotenv').config();
const { Pool } = require('pg');
const bcrypt = require('bcrypt');

const pool = new Pool({
  user: process.env.DB_USER,
  host: process.env.DB_HOST,
  database: process.env.DB_NAME,
  password: process.env.DB_PASSWORD,
  port: process.env.DB_PORT,
});

const createTestAdmin = async () => {
    const email = 'admin@patitakip.com';
    const password = '1234';
    const username = 'AdminUser';

    try {
        const hashedPassword = await bcrypt.hash(password, 10);

        // Check if user exists
        const checkQuery = 'SELECT * FROM kullanicilar WHERE eposta = $1';
        const checkRes = await pool.query(checkQuery, [email]);

        if (checkRes.rowCount > 0) {
            // Update existing user
            console.log('Kullanıcı zaten var. Şifre ve Rol güncelleniyor...');
            const updateQuery = `
                UPDATE kullanicilar 
                SET sifre_hash = $1, rol = 'yonetici' 
                WHERE eposta = $2
            `;
            await pool.query(updateQuery, [hashedPassword, email]);
            console.log(`Kullanıcı '${email}' güncellendi. Şifre: ${password}, Rol: yonetici`);
        } else {
            // Create new user
            console.log('Yeni Admin kullanıcısı oluşturuluyor...');
            const insertQuery = `
                INSERT INTO kullanicilar (kullanici_adi, eposta, sifre_hash, rol, puan, rutbe, konum, avatar_url)
                VALUES ($1, $2, $3, 'yonetici', 0, 0, '', '')
            `;
            await pool.query(insertQuery, [username, email, hashedPassword]);
            console.log(`Kullanıcı '${email}' oluşturuldu. Şifre: ${password}, Rol: yonetici`);
        }

    } catch (err) {
        console.error('Veritabanı hatası:', err);
    } finally {
        pool.end();
    }
};

createTestAdmin();

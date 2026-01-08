const { Pool } = require("pg");
require("dotenv").config();

const pool = new Pool({
  user: process.env.DB_USER,
  host: process.env.DB_HOST,
  database: process.env.DB_NAME,
  password: process.env.DB_PASSWORD,
  port: process.env.DB_PORT,
});

// Bağlantıyı test et
pool.connect((err, client, release) => {
  if (err) {
    return console.error("Hata: Veritabanına bağlanılamadı!", err.stack);
  }
  console.log("Başarılı: PostgreSQL veritabanına bağlanıldı.");
  release();
});

module.exports = {
  query: (text, params) => pool.query(text, params),
};

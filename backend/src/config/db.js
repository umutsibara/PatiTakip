const { Pool } = require("pg");
require("dotenv").config();

const pool = new Pool({
  user: process.env.DB_USER,
  host: process.env.DB_HOST,
  database: process.env.DB_NAME,
  password: process.env.DB_PASSWORD,
  port: process.env.DB_PORT,
  max: 20, // Maximum number of clients in the pool
  idleTimeoutMillis: 30000, // Close idle clients after 30 seconds
  connectionTimeoutMillis: 2000, // Return an error after 2 seconds if connection could not be established
});

// Bağlantıyı test et
pool.connect((err, client, release) => {
  if (err) {
    return console.error("Hata: Veritabanına bağlanılamadı!", err.stack);
  }
  console.log("Başarılı: PostgreSQL veritabanına bağlanıldı.");
  release();
});

// Transaction helper - Yeni işlem başlat
const getClient = async () => {
  const client = await pool.connect();
  return client;
};

module.exports = {
  query: (text, params) => pool.query(text, params),
  getClient, // Export client getter for transactions
  pool, // Export pool for direct access if needed
};

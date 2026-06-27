const { Pool } = require("pg");
const fs = require("fs");
const path = require("path");
require("dotenv").config({ path: path.join(__dirname, "..", ".env") });

const pool = new Pool({
  user: process.env.DB_USER,
  host: process.env.DB_HOST,
  database: process.env.DB_NAME,
  password: process.env.DB_PASSWORD,
  port: process.env.DB_PORT,
});

async function runMigration() {
  const filePath = path.join(__dirname, "..", "database", "11_gamification_and_features.sql");
  try {
    const sql = fs.readFileSync(filePath, "utf8");
    console.log("Running migration:", filePath);
    const client = await pool.connect();
    try {
      await client.query("BEGIN");
      await client.query(sql);
      await client.query("COMMIT");
      console.log("Migration completed successfully.");
    } catch (e) {
      await client.query("ROLLBACK");
      console.error("Migration failed:", e);
    } finally {
      client.release();
    }
  } catch (err) {
    console.error("Error reading file or connecting:", err);
  } finally {
    pool.end();
  }
}

runMigration();

const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
require('dotenv').config();

const app = express();

// Middleware
app.use(helmet());
app.use(cors());
app.use(express.json());

// DB Test
console.log('1. Testing DB connection...');
require('./config/db');

console.log('2. Loading basic routes...');
const userRoutes = require('./routes/userRoutes');
app.use('/api/users', userRoutes);

console.log('3. Adding root endpoint...');
app.get('/', (req, res) => {
    res.json({ message: 'PatiTakip API Çalışıyor! 🐾' });
});

// Server Başlat
const PORT = process.env.PORT || 3000;
console.log('4. Starting server...');
const server = app.listen(PORT, '0.0.0.0', () => {
    console.log(`✅ Sunucu ${PORT} portunda çalışıyor...`);
    
    const os = require('os');
    const interfaces = os.networkInterfaces();
    for (const devName in interfaces) {
        const iface = interfaces[devName];
        for (const alias of iface) {
            if (alias.family === 'IPv4' && !alias.internal) {
                console.log(`📱 Mobil Cihazdan Bağlanmak İçin: http://${alias.address}:${PORT}`);
            }
        }
    }
});

// Sunucu kapatma eventlerini dinle
server.on('close', () => {
    console.log('❌ Server closed!');
});

process.on('exit', (code) => {
    console.log(`❌ Process exiting with code: ${code}`);
});

process.on('uncaughtException', (err) => {
    console.error('❌ Uncaught Exception:', err);
});

process.on('unhandledRejection', (reason, promise) => {
    console.error('❌ Unhandled Rejection at:', promise, 'reason:', reason);
});

console.log('5. Setup complete, waiting for connections...');

module.exports = app;

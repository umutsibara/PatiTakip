const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
require('dotenv').config();

const app = express();

// .env değişikliklerinin algılanması için server restart edildi.
// Middleware
app.use(helmet()); // Güvenlik başlıkları
app.use(cors());   // CORS politikaları
app.use(express.json()); // JSON veri okuma

// Veritabanı Bağlantısını Başlat (Logları görmek için)
require('./config/db'); // DB Bağlantısı
const loggerMiddleware = require('./middleware/loggerMiddleware');
app.use(loggerMiddleware); // LOGLAMA (Her istekte çalışır)

// Rotalar
const reportRoutes = require('./routes/reportRoutes');
const userRoutes = require('./routes/userRoutes');
const extraRoutes = require('./routes/extraRoutes');
const uploadRoutes = require('./routes/uploadRoutes');
const referenceRoutes = require('./routes/referenceRoutes');
const healthRecordRoutes = require('./routes/healthRecordRoutes');
const adminRoutes = require('./routes/adminRoutes');
const donationRoutes = require('./routes/donationRoutes');
const serviceRoutes = require('./routes/serviceRoutes');
const path = require('path');

// Statik Dosyalar (Uploads klasörünü dışarı aç)
// Artık http://localhost:3000/uploads/photos/resim.webp adresinden erişilebilir
app.use('/uploads', express.static(path.join(__dirname, '../uploads')));

// API Endpoints
app.use('/api/reports', reportRoutes); // İhbarlar
app.use('/api/users', userRoutes);     // Kullanıcılar (Giriş/Profil)
app.use('/api', extraRoutes);          // Besleme, Hayvan, İstatistik
app.use('/api/upload', uploadRoutes);  // Dosya Yükleme
app.use('/api', referenceRoutes);      // Bölgeler, Türler (CRUD)
app.use('/api/health-records', healthRecordRoutes); // Sağlık Kayıtları
app.use('/api/admin', adminRoutes);     // Admin/Log İşlemleri
app.use('/api/donations', donationRoutes); // Bağışlar
app.use('/api/services', serviceRoutes);   // Hizmetler

// Global Error Handler (En altta olmalı)
const errorHandler = require('./middleware/errorHandler');
app.use(errorHandler);

app.get('/', (req, res) => {
    res.json({ message: 'PatiTakip API Çalışıyor! 🐾' });
});

// Server Başlat
const PORT = process.env.PORT || 3000;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`Sunucu ${PORT} portunda çalışıyor...`);
    
    // IP Adresini Göster (Mobil bağlantı için)
    const os = require('os');
    const interfaces = os.networkInterfaces();
    for (const devName in interfaces) {
        const iface = interfaces[devName];
        for (const alias of iface) {
            if (alias.family === 'IPv4' && !alias.internal) {
                console.log(`Mobil Cihazdan Bağlanmak İçin: http://${alias.address}:${PORT}`);
            }
        }
    }
});

module.exports = app;

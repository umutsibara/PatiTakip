const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
require('dotenv').config();

const app = express();

// Middleware
app.use(helmet());
app.use(cors());  
app.use(express.json());

// Veritabanı Bağlantısını Başlat
console.log('📦 Loading database connection...');
require('./config/db');

// Logger Middleware
console.log('📦 Loading logger middleware...');
const loggerMiddleware = require('./middleware/loggerMiddleware');
app.use(loggerMiddleware);

// Rotalar
console.log('📦 Loading routes...');
const path = require('path');
const reportRoutes = require('./routes/reportRoutes');
const userRoutes = require('./routes/userRoutes');
const extraRoutes = require('./routes/extraRoutes');
const uploadRoutes = require('./routes/uploadRoutes');
const referenceRoutes = require('./routes/referenceRoutes');
const healthRecordRoutes = require('./routes/healthRecordRoutes');
const adminRoutes = require('./routes/adminRoutes');
const donationRoutes = require('./routes/donationRoutes');
const serviceRoutes = require('./routes/serviceRoutes');
const commentRoutes = require('./routes/commentRoutes');
const likeRoutes = require('./routes/likeRoutes');
const chatRoutes = require('./routes/chatRoutes');
const testDbRoutes = require('./routes/testDbRoutes');

// Statik Dosyalar
app.use('/uploads', express.static(path.join(__dirname, '../uploads')));

// API Endpoints
console.log('🔗 Registering API endpoints...');
app.use('/api/test-db', testDbRoutes); // Test endpoint - Must be first
app.use('/api/reports', reportRoutes);
app.use('/api/users', userRoutes);
app.use('/api', extraRoutes);
app.use('/api/upload', uploadRoutes);
app.use('/api', referenceRoutes);
app.use('/api/health-records', healthRecordRoutes);
app.use('/api/admin', adminRoutes);
app.use('/api/donations', donationRoutes);
app.use('/api/services', serviceRoutes);
app.use('/api', commentRoutes);
app.use('/api', likeRoutes);
app.use('/api', chatRoutes);

// Global Error Handler
const errorHandler = require('./middleware/errorHandler');
app.use(errorHandler);

app.get('/', (req, res) => {
    res.json({ message: 'PatiTakip API Çalışıyor! 🐾' });
});

// Server Başlat
const PORT = process.env.PORT || 3000;
console.log('🚀 Starting server...');
app.listen(PORT, '0.0.0.0', () => {
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

module.exports = app;

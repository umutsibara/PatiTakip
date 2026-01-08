const express = require('express');
const router = express.Router();
const uploadController = require('../controllers/uploadController');
const { upload, processAndSaveImage } = require('../middleware/uploadMiddleware');
const { authenticateToken } = require('../middleware/authMiddleware');

// POST /api/upload/photo
// 1. authenticateToken: Token kontrolü yap
// 2. upload.single('photo'): Multer ile dosyayı al (Memory)
// 3. processAndSaveImage: Sharp ile işle ve DB'ye kaydet
// 4. uploadController.uploadPhoto: Yanıt dön
router.post('/photo', authenticateToken, upload.single('photo'), processAndSaveImage, uploadController.uploadPhoto);

module.exports = router;

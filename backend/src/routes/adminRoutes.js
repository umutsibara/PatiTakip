const express = require('express');
const router = express.Router();
const adminController = require('../controllers/adminController');
const { authenticateToken, isAdmin } = require('../middleware/authMiddleware');

// Tüm admin rotaları korumalıdır
router.use(authenticateToken, isAdmin);

// GET /api/admin/logs
router.get('/logs', adminController.getSystemLogs);

// GET /api/admin/logs/stats
router.get('/logs/stats', adminController.getLogStats);

module.exports = router;

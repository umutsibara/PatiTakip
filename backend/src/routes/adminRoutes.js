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

// --- RAPOR YÖNETİMİ ---
// GET /api/admin/reports
router.get('/reports', adminController.getAllReportsAdmin);

// DELETE /api/admin/reports/:id
router.delete('/reports/:id', adminController.deleteReportAdmin);

// --- BESLEME YÖNETİMİ ---
// GET /api/admin/feedings
router.get('/feedings', adminController.getAllFeedingsAdmin);

// DELETE /api/admin/feedings/:id
router.delete('/feedings/:id', adminController.deleteFeedingAdmin);

module.exports = router;

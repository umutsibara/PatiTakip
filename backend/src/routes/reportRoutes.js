const express = require('express');
const router = express.Router();
const reportController = require('../controllers/reportController');

// GET /api/reports -> Tüm aktif ihbarlar
router.get('/', reportController.getAllReports);

// GET /api/reports/:id -> Tekil ihbar
router.get('/:id', reportController.getReportById);

// POST /api/reports -> Yeni ihbar ekle
router.post('/', reportController.createReport);

// PUT /api/reports/:id/resolve -> İhbarı çöz (SP Tetikler)
router.put('/:id/resolve', reportController.resolveReport);

module.exports = router;

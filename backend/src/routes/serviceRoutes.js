const express = require('express');
const router = express.Router();
const serviceController = require('../controllers/serviceController');

// GET /api/services
router.get('/', serviceController.getAllServices);

// POST /api/services
router.post('/', serviceController.createService);

module.exports = router;

const express = require('express');
const router = express.Router();
const testController = require('../controllers/testController');

router.get('/db-check', testController.getTestStats);

module.exports = router;

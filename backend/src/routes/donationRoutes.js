const express = require('express');
const router = express.Router();
const donationController = require('../controllers/donationController');

// GET /api/donations
router.get('/', donationController.getAllDonations);

// POST /api/donations
router.post('/', donationController.createDonation);

module.exports = router;

const express = require('express');
const router = express.Router();

const feedingController = require('../controllers/feedingController');
const animalController = require('../controllers/animalController');
const statsController = require('../controllers/statsController');

// --- Beslemeler ---
router.post('/feedings', feedingController.createFeeding);
router.get('/feedings', feedingController.getAllFeedings);

// --- Hayvanlar ---
router.get('/animals', animalController.getAllAnimals);
router.post('/animals', animalController.createAnimal);

// --- İstatistikler ---
router.get('/stats/general', statsController.getGeneralStats);
router.get('/stats/risk/:id', statsController.getZoneRisk); // Fonksiyon kullanımı

module.exports = router;

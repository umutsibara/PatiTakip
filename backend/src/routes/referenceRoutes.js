const express = require('express');
const router = express.Router();
const zoneController = require('../controllers/zoneController');
const animalTypeController = require('../controllers/animalTypeController');
const { validateId } = require('../middleware/validator');
const { authenticateToken, isAdmin } = require('../middleware/authMiddleware');

// --- Bölgeler (Zones) ---
// Herkes görüntüleyebilir, sadece Yönetici ekleyip silebilir
router.get('/zones', zoneController.getAllZones);
router.post('/zones', authenticateToken, isAdmin, zoneController.createZone);
router.put('/zones/:id', authenticateToken, isAdmin, validateId, zoneController.updateZone);
router.delete('/zones/:id', authenticateToken, isAdmin, validateId, zoneController.deleteZone);

// --- Hayvan Türleri (Animal Types) ---
router.get('/animal-types', animalTypeController.getAllTypes);
router.post('/animal-types', authenticateToken, isAdmin, animalTypeController.createType);
router.put('/animal-types/:id', authenticateToken, isAdmin, validateId, animalTypeController.updateType);
router.delete('/animal-types/:id', authenticateToken, isAdmin, validateId, animalTypeController.deleteType);

module.exports = router;

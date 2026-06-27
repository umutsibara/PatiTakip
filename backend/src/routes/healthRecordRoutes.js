const express = require('express');
const router = express.Router();
const healthRecordController = require('../controllers/healthRecordController');
const { validateId } = require('../middleware/validator');
const { authenticateToken } = require('../middleware/authMiddleware');

// Tüm authenticated kullanıcılar sağlık kaydı ekleyebilir/görebilir
// (Advanced: Sadece veteriner rolü yapabilsin diye kısıtlanabilir ama şimdilik gonullu de yapabilsin)

router.get('/animal/:animalId', authenticateToken, validateId, healthRecordController.getHealthRecordsByAnimal);
router.post('/', authenticateToken, healthRecordController.createHealthRecord);
router.put('/:id', authenticateToken, validateId, healthRecordController.updateHealthRecord);
router.delete('/:id', authenticateToken, validateId, healthRecordController.deleteHealthRecord);

module.exports = router;

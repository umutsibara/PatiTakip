const express = require('express');
const router = express.Router();
const userController = require('../controllers/userController');

// POST /api/users/register
router.post('/register', userController.register);

// POST /api/users/login
router.post('/login', userController.login);

// GET /api/users/:id/profile
router.get('/:id/profile', userController.getProfile);

// GET /api/users/leaderboard
router.get('/leaderboard', userController.getLeaderboard);

module.exports = router;

// ============================================================================
// 💬 Yorum Sistemi Routes
// ============================================================================

const express = require('express');
const router = express.Router();
const commentController = require('../controllers/commentController');
// const authMiddleware = require('../middleware/authMiddleware'); // JWT için

// ============================================================================
// YORUM İŞLEMLERİ
// ============================================================================

// İhbara yorum ekle
router.post('/reports/:ihbar_id/comments', commentController.addComment);

// İhbarın yorumlarını listele
router.get('/reports/:ihbar_id/comments', commentController.getCommentsByReport);

// Yoruma cevapları getir (alt yorumlar)
router.get('/comments/:yorum_id/replies', commentController.getRepliesByComment);

// Yorum güncelle
router.put('/comments/:yorum_id', commentController.updateComment);

// Yorum sil
router.delete('/comments/:yorum_id', commentController.deleteComment);

// Kullanıcının tüm yorumlarını getir
router.get('/users/:kullanici_id/comments', commentController.getCommentsByUser);

module.exports = router;

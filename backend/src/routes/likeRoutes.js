// ============================================================================
// ❤️ Beğeni Sistemi Routes
// ============================================================================

const express = require('express');
const router = express.Router();
const likeController = require('../controllers/likeController');
// const authMiddleware = require('../middleware/authMiddleware'); // JWT için

// ============================================================================
// BEĞENİ İŞLEMLERİ
// ============================================================================

// Beğeni ekle/kaldır (toggle)
router.post('/likes/toggle', likeController.toggleLike);

// Kullanıcının beğendiği içerikleri getir
router.get('/users/:kullanici_id/likes', likeController.getUserLikes);

// Belirli içeriği beğenen kullanıcıları getir
router.get('/likes/:hedef_turu/:hedef_id/users', likeController.getLikesByContent);

// Kullanıcının belirli içeriği beğenip beğenmediğini kontrol et
router.get('/likes/check/:kullanici_id/:hedef_turu/:hedef_id', likeController.checkUserLike);

// Toplu beğeni durumu kontrolü (Feed için optimize edilmiş)
router.post('/likes/check-multiple', likeController.checkMultipleLikes);

module.exports = router;

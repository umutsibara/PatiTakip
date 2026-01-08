// ============================================================================
// 💬 Chat/Mesajlaşma Sistemi Routes
// ============================================================================

const express = require('express');
const router = express.Router();
const chatController = require('../controllers/chatController');
// const authMiddleware = require('../middleware/authMiddleware'); // JWT için

// ============================================================================
// SOHBET YÖNETİMİ
// ============================================================================

// Yeni sohbet oluştur veya mevcut sohbeti getir
router.post('/chats/create-or-get', chatController.createOrGetChat);

// Kullanıcının tüm sohbetlerini listele
router.get('/users/:kullanici_id/chats', chatController.getUserChats);

// Okunmamış mesaj sayısı (tüm sohbetler)
router.get('/users/:kullanici_id/unread-count', chatController.getUnreadCount);

// ============================================================================
// MESAJ İŞLEMLERİ
// ============================================================================

// Mesaj gönder
router.post('/chats/:sohbet_id/messages', chatController.sendMessage);

// Sohbetin mesajlarını getir
router.get('/chats/:sohbet_id/messages', chatController.getChatMessages);

// Mesajları okundu olarak işaretle
router.put('/chats/:sohbet_id/mark-read', chatController.markMessagesAsRead);

// Mesaj sil
router.delete('/messages/:mesaj_id', chatController.deleteMessage);

// ============================================================================
// KULLANICI ARAMA
// ============================================================================

// Sohbet için kullanıcı ara
router.get('/chats/search-users', chatController.searchUsers);

module.exports = router;

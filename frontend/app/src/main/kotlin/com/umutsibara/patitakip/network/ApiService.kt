package com.umutsibara.patitakip.network

import com.umutsibara.patitakip.network.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // ============================================================================
    // AUTH - Kullanıcı İşlemleri
    // ============================================================================
    
    @POST("users/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>
    
    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @GET("users/{id}/profile")
    suspend fun getUserProfile(@Path("id") userId: Int): Response<ApiResponse<User>>
    
    // ============================================================================
    // REPORTS - İhbar İşlemleri
    // ============================================================================
    
    @GET("reports")
    suspend fun getReports(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("category") category: String? = null,  // Fixed: was "kategori", should be "category"
        @Query("enlem") lat: Double? = null,
        @Query("boylam") lng: Double? = null,
        @Query("yaricap") radius: Double? = null
    ): Response<ApiResponse<List<Report>>>
    
    @GET("reports/{id}")
    suspend fun getReportDetail(@Path("id") reportId: Int): Response<ApiResponse<Report>>
    
    @POST("reports")
    suspend fun createReport(@Body request: CreateReportRequest): Response<ApiResponse<Report>>
    
    @PUT("reports/{id}")
    suspend fun updateReport(
        @Path("id") reportId: Int,
        @Body request: CreateReportRequest
    ): Response<ApiResponse<Report>>
    
    @DELETE("reports/{id}")
    suspend fun deleteReport(@Path("id") reportId: Int): Response<ApiResponse<Any>>
    
    // ============================================================================
    // COMMENTS - Yorum İşlemleri
    // ============================================================================
    
    @GET("reports/{ihbar_id}/comments")
    suspend fun getComments(
        @Path("ihbar_id") ihbarId: Int,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<ApiResponse<List<Comment>>>
    
    @POST("reports/{ihbar_id}/comments")
    suspend fun addComment(
        @Path("ihbar_id") ihbarId: Int,
        @Body request: CreateCommentRequest
    ): Response<ApiResponse<Comment>>
    
    @GET("comments/{yorum_id}/replies")
    suspend fun getReplies(
        @Path("yorum_id") yorumId: Int
    ): Response<ApiResponse<List<Comment>>>
    
    @DELETE("comments/{yorum_id}")
    suspend fun deleteComment(@Path("yorum_id") yorumId: Int): Response<ApiResponse<Any>>
    
    // ============================================================================
    // LIKES - Beğeni İşlemleri
    // ============================================================================
    
    @POST("likes/toggle")
    suspend fun toggleLike(@Body request: LikeToggleRequest): Response<LikeResponse>
    
    @GET("likes/check/{kullanici_id}/{hedef_turu}/{hedef_id}")
    suspend fun checkLike(
        @Path("kullanici_id") kullaniciId: Int,
        @Path("hedef_turu") hedefTuru: String,
        @Path("hedef_id") hedefId: Int
    ): Response<ApiResponse<Map<String, Boolean>>>
    
    // ============================================================================
    // CHAT - Mesajlaşma İşlemleri
    // ============================================================================
    
    @POST("chats/create-or-get")
    suspend fun createOrGetChat(@Body request: CreateChatRequest): Response<ApiResponse<Chat>>
    
    @GET("users/{kullanici_id}/chats")
    suspend fun getUserChats(
        @Path("kullanici_id") userId: Int,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<Chat>>>
    
    @GET("chats/{sohbet_id}/messages")
    suspend fun getChatMessages(
        @Path("sohbet_id") chatId: Int,
        @Query("kullanici_id") userId: Int,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<Message>>>
    
    @POST("chats/{sohbet_id}/messages")
    suspend fun sendMessage(
        @Path("sohbet_id") chatId: Int,
        @Body request: SendMessageRequest  
    ): Response<ApiResponse<Message>>
    
    @PUT("chats/{sohbet_id}/mark-read")
    suspend fun markMessagesAsRead(
        @Path("sohbet_id") chatId: Int,
        @Body body: Map<String, Int> // kullanici_id
    ): Response<ApiResponse<Any>>
    
    @GET("users/{kullanici_id}/unread-count")
    suspend fun getUnreadCount(
        @Path("kullanici_id") userId: Int
    ): Response<ApiResponse<Map<String, Int>>>
    
    @GET("chats/search-users")
    suspend fun searchUsers(
        @Query("arama_metni") query: String,
        @Query("current_user_id") currentUserId: Int
    ): Response<ApiResponse<List<User>>>
    
    // ============================================================================
    // FEEDINGS - Besleme İşlemleri
    // ============================================================================
    
    @GET("feedings")
    suspend fun getFeedings(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<ApiResponse<List<Feeding>>>
    
    @POST("feedings")
    suspend fun createFeeding(@Body request: CreateFeedingRequest): Response<ApiResponse<Feeding>>
}

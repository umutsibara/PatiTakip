package com.umutsibara.patitakip.data.repository

import com.umutsibara.patitakip.data.api.ApiService
import com.umutsibara.patitakip.data.model.AuthResponse
import com.umutsibara.patitakip.data.model.LoginRequest
import com.umutsibara.patitakip.data.model.RegisterRequest
import com.umutsibara.patitakip.data.model.User
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): Response<AuthResponse> {
        return apiService.login(LoginRequest(email, password))
    }

    suspend fun register(
            username: String,
            email: String,
            password: String
    ): Response<AuthResponse> {
        return apiService.register(RegisterRequest(username, email, password))
    }

    suspend fun getProfile(id: Int): Response<User> {
        return apiService.getProfile(id)
    }
}

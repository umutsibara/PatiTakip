package com.umutsibara.patitakip.data.api

import com.umutsibara.patitakip.data.model.AnimalTypesResponse
import com.umutsibara.patitakip.data.model.AuthResponse
import com.umutsibara.patitakip.data.model.LoginRequest
import com.umutsibara.patitakip.data.model.PhotoUploadResponse
import com.umutsibara.patitakip.data.model.RegisterRequest
import com.umutsibara.patitakip.data.model.Report
import com.umutsibara.patitakip.data.model.ReportResponse
import com.umutsibara.patitakip.data.model.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("users/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("users/login") 
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("reports") 
    suspend fun getReports(): Response<ReportResponse>

    @POST("reports") 
    suspend fun createReport(@Body request: com.umutsibara.patitakip.data.model.CreateReportRequest): Response<Report>

    @GET("users/{id}/profile")
    suspend fun getProfile(@retrofit2.http.Path("id") id: Int): Response<User>

    @Multipart
    @POST("upload/photo")
    suspend fun uploadPhoto(@Part photo: MultipartBody.Part): Response<PhotoUploadResponse>

    @GET("animal-types")
    suspend fun getAnimalTypes(): Response<AnimalTypesResponse>
}

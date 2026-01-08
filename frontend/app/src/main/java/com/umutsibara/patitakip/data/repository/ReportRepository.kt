package com.umutsibara.patitakip.data.repository

import com.umutsibara.patitakip.data.api.ApiService
import com.umutsibara.patitakip.data.model.AnimalTypesResponse
import com.umutsibara.patitakip.data.model.PhotoUploadResponse
import com.umutsibara.patitakip.data.model.Report
import okhttp3.MultipartBody
import retrofit2.Response

class ReportRepository(private val apiService: ApiService) {

    suspend fun getReports(): Response<com.umutsibara.patitakip.data.model.ReportResponse> {
        return apiService.getReports()
    }

    suspend fun createReport(request: com.umutsibara.patitakip.data.model.CreateReportRequest): Response<Report> {
        return apiService.createReport(request)
    }

    suspend fun uploadPhoto(photo: MultipartBody.Part): Response<PhotoUploadResponse> {
        return apiService.uploadPhoto(photo)
    }

    suspend fun getAnimalTypes(): Response<AnimalTypesResponse> {
        return apiService.getAnimalTypes()
    }
}

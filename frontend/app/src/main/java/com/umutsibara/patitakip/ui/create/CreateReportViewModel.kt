package com.umutsibara.patitakip.ui.create

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.umutsibara.patitakip.data.model.AnimalType
import com.umutsibara.patitakip.data.model.ErrorResponse
import com.umutsibara.patitakip.data.model.Report
import com.umutsibara.patitakip.data.repository.ReportRepository
import com.umutsibara.patitakip.ui.base.BaseViewModel
import com.umutsibara.patitakip.util.SessionManager
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class CreateReportViewModel(
    private val repository: ReportRepository,
    private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _createSuccess = MutableLiveData<Boolean>()
    val createSuccess: LiveData<Boolean> get() = _createSuccess

    private val _animalTypes = MutableLiveData<List<AnimalType>>()
    val animalTypes: LiveData<List<AnimalType>> get() = _animalTypes

    private val _uploadedPhotoId = MutableLiveData<Int?>()
    val uploadedPhotoId: LiveData<Int?> get() = _uploadedPhotoId


    private val _photoPreviewUrl = MutableLiveData<String?>()
    val photoPreviewUrl: LiveData<String?> get() = _photoPreviewUrl


    fun fetchAnimalTypes() {
        viewModelScope.launch {
            try {
                val response = repository.getAnimalTypes()
                if (response.isSuccessful && response.body()?.success == true) {
                    _animalTypes.value = response.body()?.data ?: emptyList()
                } else {
                    onError("Hayvan türleri yüklenemedi")
                }
            } catch (e: Exception) {
                onError("Hayvan türleri yüklenirken hata: ${e.localizedMessage}")
            }
        }
    }

    fun uploadPhoto(context: Context, uri: Uri) {
        setLoading(true)
        viewModelScope.launch {
            try {
                // Convert URI to File
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                // Create multipart request
                val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestBody)

                val response = repository.uploadPhoto(photoPart)
                if (response.isSuccessful && response.body()?.success == true) {
                    val photoData = response.body()?.data
                    _uploadedPhotoId.value = photoData?.photoId
                    _photoPreviewUrl.value = uri.toString() // Use local URI for preview
                    onError("Fotoğraf başarıyla yüklendi") // Success message
                } else {
                    val errorMsg = parseErrorResponse(response.errorBody()?.string())
                    onError(errorMsg ?: "Fotoğraf yüklenemedi")
                }

                // Clean up temp file
                file.delete()
            } catch (e: Exception) {
                onError("Fotoğraf yüklenirken hata: ${e.localizedMessage}")
            } finally {
                setLoading(false)
            }
        }
    }

    fun createReport(
        title: String,
        description: String,
        category: String,
        reportType: String?,
        animalType: String,
        latitude: Double,
        longitude: Double
    ) {
        // Validation
        if (title.isBlank() || description.isBlank()) {
            onError("Lütfen başlık ve açıklama giriniz.")
            return
        }

        if (animalType.isBlank()) {
            onError("Lütfen hayvan türü seçiniz.")
            return
        }

        if (latitude == 0.0 || longitude == 0.0) {
            onError("Lütfen geçerli bir konum giriniz.")
            return
        }

        val userId = sessionManager.getUserId()
        if (userId == -1) {
            onError("Kullanıcı oturumu bulunamadı. Lütfen tekrar giriş yapın.")
            return
        }

        setLoading(true)
        val request = com.umutsibara.patitakip.data.model.CreateReportRequest(
            userId = userId,
            title = title.trim(),
            description = description.trim(),
            category = category,
            reportType = reportType?.takeIf { category == "REPORT" },
            animalType = animalType,
            latitude = latitude,
            longitude = longitude,
            photoId = _uploadedPhotoId.value
        )

        viewModelScope.launch {
            try {
                val response = repository.createReport(request)
                if (response.isSuccessful) {
                    _createSuccess.value = true
                } else {
                    val errorMsg = parseErrorResponse(response.errorBody()?.string())
                    onError(errorMsg ?: "İhbar oluşturulamadı: ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Hata: ${e.localizedMessage}")
            } finally {
                setLoading(false)
            }
        }
    }

    private fun parseErrorResponse(errorBody: String?): String? {
        return try {
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            errorResponse.error ?: errorResponse.message
        } catch (e: Exception) {
            null
        }
    }

    fun clearPhotoUpload() {
        _uploadedPhotoId.value = null
        _photoPreviewUrl.value = null
    }
}

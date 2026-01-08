package com.umutsibara.patitakip.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.umutsibara.patitakip.data.model.Report
import com.umutsibara.patitakip.data.repository.ReportRepository
import com.umutsibara.patitakip.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ReportRepository) : BaseViewModel() {

    private val _reports = MutableLiveData<List<Report>>()
    val reports: LiveData<List<Report>>
        get() = _reports

    fun fetchReports() {
        Log.d("HomeViewModel", "=== fetchReports started ===")
        setLoading(true)
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Making API call to /reports...")
                val response = repository.getReports()
                
                Log.d("HomeViewModel", "Response received:")
                Log.d("HomeViewModel", "  - Code: ${response.code()}")
                Log.d("HomeViewModel", "  - Success: ${response.isSuccessful}")
                Log.d("HomeViewModel", "  - Message: ${response.message()}")
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Log.d("HomeViewModel", "  - Body not null")
                        Log.d("HomeViewModel", "  - success field: ${body.success}")
                        Log.d("HomeViewModel", "  - message field: ${body.message}")
                        Log.d("HomeViewModel", "  - data size: ${body.data.size}")
                        
                        // Log first few reports for debugging
                        body.data.take(3).forEachIndexed { index, report ->
                            Log.d("HomeViewModel", "  Report[$index]:")
                            Log.d("HomeViewModel", "    - id: ${report.id}")
                            Log.d("HomeViewModel", "    - title: ${report.title}")
                            Log.d("HomeViewModel", "    - description: ${report.description}")
                            Log.d("HomeViewModel", "    - category: ${report.category}")
                            Log.d("HomeViewModel", "    - photoUrl: ${report.photoUrl}")
                            Log.d("HomeViewModel", "    - creatorName: ${report.creatorName}")
                        }
                        
                        // Set data regardless of success field
                        _reports.value = body.data
                        
                        if (!body.success) {
                            Log.w("HomeViewModel", "Success=false but data exists: ${body.message}")
                        }
                    } else {
                        Log.e("HomeViewModel", "Response body is null!")
                        onError("Backend'den veri gelmedi")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                   Log.e("HomeViewModel", "Error response: $errorBody")
                    onError("Hata ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception occurred:", e)
                e.printStackTrace()
                onError("Bağlantı hatası: ${e.message}")
            } finally {
                setLoading(false)
                Log.d("HomeViewModel", "=== fetchReports finished ===")
            }
        }
    }

    fun deleteReport(reportId: Int?) {
        if (reportId == null) return
        setLoading(true)
        viewModelScope.launch {
            try {
                val response = repository.deleteReport(reportId)
                if (response.isSuccessful) {
                    // Refresh list after successful delete
                    fetchReports()
                } else {
                    onError("Silme başarısız: ${response.code()}")
                }
            } catch (e: Exception) {
                onError("Silme hatası: ${e.localizedMessage}")
            } finally {
                setLoading(false)
            }
        }
    }
}


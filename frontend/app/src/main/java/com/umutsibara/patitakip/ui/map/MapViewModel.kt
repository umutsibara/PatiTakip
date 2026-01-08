package com.umutsibara.patitakip.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umutsibara.patitakip.data.model.Report
import com.umutsibara.patitakip.data.repository.ReportRepository
import kotlinx.coroutines.launch

class MapViewModel(private val repository: ReportRepository) : ViewModel() {

    private val _reports = MutableLiveData<List<Report>>()
    val reports: LiveData<List<Report>> = _reports

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchReports() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getReports()
                if (response.isSuccessful && response.body() != null) {
                    _reports.value = response.body()!!.data
                } else {
                    _errorMessage.value = "İhbarlar yüklenemedi"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Bağlantı hatası: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

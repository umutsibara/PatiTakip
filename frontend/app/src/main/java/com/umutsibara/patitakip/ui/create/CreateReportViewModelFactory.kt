package com.umutsibara.patitakip.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.umutsibara.patitakip.data.repository.ReportRepository
import com.umutsibara.patitakip.util.SessionManager

class CreateReportViewModelFactory(
    private val repository: ReportRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateReportViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.umutsibara.patitakip.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umutsibara.patitakip.data.model.AuthData
import com.umutsibara.patitakip.data.repository.AuthRepository
import com.umutsibara.patitakip.util.SessionManager
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _registerSuccess = MutableLiveData<AuthData?>()
    val registerSuccess: LiveData<AuthData?> = _registerSuccess

    fun register(username: String, email: String, password: String) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "Tüm alanları doldurun"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.register(username, email, password)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    
                    if (authResponse.success && authResponse.data != null) {
                        val authData = authResponse.data!!
                        
                        // Save session
                        authData.token?.let { sessionManager.saveAuthToken(it) }
                        sessionManager.saveUser(authData.id, authData.username, authData.role ?: "gonullu")
                        
                        _registerSuccess.value = authData
                    } else {
                        _errorMessage.value = "Kayıt başarısız: ${authResponse.message ?: "Bilinmeyen hata"}"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Kayıt başarısız: ${errorBody ?: "Bilinmeyen hata"}"
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


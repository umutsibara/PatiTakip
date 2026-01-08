package com.umutsibara.patitakip.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.umutsibara.patitakip.data.model.User
import com.umutsibara.patitakip.data.repository.AuthRepository
import com.umutsibara.patitakip.ui.base.BaseViewModel
import com.umutsibara.patitakip.util.SessionManager
import kotlinx.coroutines.launch

class ProfileViewModel(
        private val repository: AuthRepository,
        private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun fetchProfile() {
        val userId = sessionManager.getUserId() // need to implement access to userId
        if (userId == -1) {
            onError("Kullanıcı ID bulunamadı.")
            return
        }

        setLoading(true)
        viewModelScope.launch {
            try {
                val response = repository.getProfile(userId)
                if (response.isSuccessful && response.body() != null) {
                    _user.value = response.body()
                } else {
                    onError("Profil yüklenemedi: ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Hata: ${e.localizedMessage}")
            } finally {
                setLoading(false)
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }
}

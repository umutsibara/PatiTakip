package com.umutsibara.patitakip.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.umutsibara.patitakip.data.model.User
import com.umutsibara.patitakip.data.repository.AuthRepository
import com.umutsibara.patitakip.ui.base.BaseViewModel
import com.umutsibara.patitakip.util.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel(
        private val authRepository: AuthRepository,
        private val sessionManager: SessionManager
) : BaseViewModel() {

    private val _loginSuccess = MutableLiveData<User?>()
    val loginSuccess: LiveData<User?>
        get() = _loginSuccess

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            onError("Lütfen tüm alanları doldurun.")
            return
        }

        setLoading(true)
        viewModelScope.launch {
            try {
                val response = authRepository.login(email, password)
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    if (authResponse.success && authResponse.data != null) {
                        val authData = authResponse.data
                        if (authData.token != null) {
                            sessionManager.saveAuthToken(authData.token)
                            sessionManager.saveUser(authData.id, authData.username, authData.role ?: "gonullu")
                            
                            val user = User(
                                id = authData.id,
                                username = authData.username,
                                email = authData.email,
                                role = authData.role ?: "gonullu",
                                score = authData.score,
                                badges = null // AuthData doesn't have badges for now
                            )
                            _loginSuccess.value = user
                        } else {
                            onError("Token alınamadı.")
                        }
                    } else {
                        onError(authResponse.message ?: "Giriş başarısız.")
                    }
                } else {
                    onError("Giriş başarısız: ${response.message()}")
                }
            } catch (e: java.net.SocketTimeoutException) {
                onError("Sunucuya ulaşılamadı. Lütfen internet bağlantınızı kontrol edin.")
            } catch (e: java.net.ConnectException) {
                onError("Sunucuya bağlanılamadı. IP adresini kontrol edin.")
            } catch (e: Exception) {
                onError("Hata: ${e.localizedMessage}")
            } finally {
                setLoading(false)
            }
        }
    }
}

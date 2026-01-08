package com.umutsibara.patitakip.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    protected fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    protected fun onError(message: String) {
        _errorMessage.value = message
        setLoading(false) // Ensure loading is hidden on error
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

package com.umutsibara.patitakip.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.umutsibara.patitakip.MainActivity
import com.umutsibara.patitakip.data.api.RetrofitClient
import com.umutsibara.patitakip.data.repository.AuthRepository
import com.umutsibara.patitakip.databinding.ActivityRegisterBinding
import com.umutsibara.patitakip.ui.login.LoginActivity
import com.umutsibara.patitakip.util.SessionManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupObservers()
        setupListeners()
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getApiService(this)
        val repository = AuthRepository(apiService)
        val sessionManager = SessionManager(this)
        val factory = RegisterViewModelFactory(repository, sessionManager)
        viewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !isLoading
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.registerSuccess.observe(this) { authData ->
            if (authData != null) {
                Toast.makeText(this, "Hoşgeldin ${authData.username}! Kayıt başarılı.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            if (validateForm()) {
                val username = binding.etUsername.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                viewModel.register(username, email, password)
            }
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateForm(): Boolean {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Username validation
        if (username.isEmpty()) {
            binding.tilUsername.error = "Kullanıcı adı gerekli"
            return false
        }
        if (username.length < 3) {
            binding.tilUsername.error = "Kullanıcı adı en az 3 karakter olmalı"
            return false
        }
        binding.tilUsername.error = null

        // Email validation
        if (email.isEmpty()) {
            binding.tilEmail.error = "E-posta gerekli"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Geçerli bir e-posta adresi girin"
            return false
        }
        binding.tilEmail.error = null

        // Password validation
        if (password.isEmpty()) {
            binding.tilPassword.error = "Şifre gerekli"
            return false
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Şifre en az 6 karakter olmalı"
            return false
        }
        binding.tilPassword.error = null

        // Confirm password validation
        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = "Şifre tekrarı gerekli"
            return false
        }
        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Şifreler eşleşmiyor"
            return false
        }
        binding.tilConfirmPassword.error = null

        return true
    }
}

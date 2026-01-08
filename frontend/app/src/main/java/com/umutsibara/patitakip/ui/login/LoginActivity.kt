package com.umutsibara.patitakip.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.umutsibara.patitakip.MainActivity
import com.umutsibara.patitakip.data.api.RetrofitClient
import com.umutsibara.patitakip.data.repository.AuthRepository
import com.umutsibara.patitakip.databinding.ActivityLoginBinding
import com.umutsibara.patitakip.util.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupObservers()
        setupListeners()
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getApiService(this)
        val repository = AuthRepository(apiService)
        val sessionManager = SessionManager(this)
        val factory = LoginViewModelFactory(repository, sessionManager)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
        }

        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        viewModel.loginSuccess.observe(this) { user ->
            if (user != null) {
                Toast.makeText(this, "Hoşgeldin ${user.username}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, com.umutsibara.patitakip.ui.register.RegisterActivity::class.java))
        }
    }
}

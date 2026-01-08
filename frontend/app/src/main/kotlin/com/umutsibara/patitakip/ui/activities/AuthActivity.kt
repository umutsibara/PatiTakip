package com.umutsibara.patitakip.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.umutsibara.patitakip.MainActivity
import com.umutsibara.patitakip.R
import com.umutsibara.patitakip.databinding.ActivityAuthBinding
import com.umutsibara.patitakip.network.ApiClient
import com.umutsibara.patitakip.network.models.LoginRequest
import com.umutsibara.patitakip.network.models.RegisterRequest
import com.umutsibara.patitakip.utils.PreferencesManager
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAuthBinding
    private lateinit var prefsManager: PreferencesManager
    private var isLoginMode = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        prefsManager = PreferencesManager(this)
        
        setupUI()
        setupListeners()
    }
    
    private fun setupUI() {
        updateUIForMode()
    }
    
    private fun setupListeners() {
        binding.btnSubmit.setOnClickListener {
            if (isLoginMode) {
                performLogin()
            } else {
                performRegister()
            }
        }
        
        binding.tvToggleMode.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUIForMode()
        }
    }
    
    private fun updateUIForMode() {
        if (isLoginMode) {
            // Login modu
            binding.tvTitle.text = "Giriş Yap"
            binding.tilUsername.visibility = View.GONE
            binding.tilFullName.visibility = View.GONE
            binding.btnSubmit.text = "Giriş Yap"
            binding.tvToggleMode.text = "Hesabınız yok mu? Kayıt olun"
        } else {
            // Register modu
            binding.tvTitle.text = "Kayıt Ol"
            binding.tilUsername.visibility = View.VISIBLE
            binding.tilFullName.visibility = View.VISIBLE
            binding.btnSubmit.text = "Kayıt Ol"
            binding.tvToggleMode.text = "Zaten hesabınız var mı? Giriş yapın"
        }
        
        // Input'ları temizle
        binding.etUsername.text?.clear()
        binding.etFullName.text?.clear()
        binding.etEmail.text?.clear()
        binding.etPassword.text?.clear()
    }
    
    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.text = "Giriş yapılıyor..."
        
        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService().login(LoginRequest(email, password))
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()!!
                    val userData = body.data
                    
                    // Token ve kullanıcı bilgilerini kaydet
                    userData?.token?.let { prefsManager.saveToken(it) }
                    userData?.let {
                        prefsManager.saveUserId(it.getUserId())
                        prefsManager.saveUserName(it.getUsername())
                    }
                    prefsManager.setLoggedIn(true)
                    
                    Toast.makeText(this@AuthActivity, "Giriş başarılı!", Toast.LENGTH_SHORT).show()
                    
                    // Ana ekrana git - Activity stack'i temizle
                    val intent = Intent(this@AuthActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@AuthActivity,
                        response.body()?.message ?: "Giriş başarısız",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.btnSubmit.isEnabled = true
                    binding.btnSubmit.text = "Giriş Yap"
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@AuthActivity,
                    "Hata: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                binding.btnSubmit.isEnabled = true
                binding.btnSubmit.text = "Giriş Yap"
            }
        }
    }
    
    private fun performRegister() {
        val username = binding.etUsername.text.toString().trim()
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        
        // Validasyon
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Lütfen zorunlu alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (username.length < 3) {
            Toast.makeText(this, "Kullanıcı adı en az 3 karakter olmalı", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (password.length < 6) {
            Toast.makeText(this, "Şifre en az 6 karakter olmalı", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Geçerli bir e-posta adresi girin", Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.text = "Kayıt oluşturuluyor..."
        
        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService().register(
                    RegisterRequest(
                        kullaniciAdi = username,
                        eposta = email,
                        sifre = password,
                        tamIsim = fullName.ifEmpty { null }
                    )
                )
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()!!
                    val userData = body.data
                    
                    // Token ve kullanıcı bilgilerini kaydet
                    userData?.token?.let { prefsManager.saveToken(it) }
                    userData?.let {
                        prefsManager.saveUserId(it.getUserId())
                        prefsManager.saveUserName(it.getUsername())
                    }
                    prefsManager.setLoggedIn(true)
                    
                    Toast.makeText(this@AuthActivity, "Kayıt başarılı! Hoş geldiniz!", Toast.LENGTH_SHORT).show()
                    
                    // Ana ekrana git - Activity stack'i temizle
                    val intent = Intent(this@AuthActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@AuthActivity,
                        response.body()?.message ?: "Kayıt başarısız",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.btnSubmit.isEnabled = true
                    binding.btnSubmit.text = "Kayıt Ol"
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@AuthActivity,
                    "Hata: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                binding.btnSubmit.isEnabled = true
                binding.btnSubmit.text = "Kayıt Ol"
            }
        }
    }
}

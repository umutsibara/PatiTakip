package com.umutsibara.patitakip

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.umutsibara.patitakip.databinding.ActivityMainBinding
import com.umutsibara.patitakip.ui.activities.AuthActivity
import com.umutsibara.patitakip.ui.fragments.FeedFragment
import com.umutsibara.patitakip.ui.fragments.MapFragment
import com.umutsibara.patitakip.ui.fragments.ProfileFragment
import com.umutsibara.patitakip.utils.PreferencesManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefsManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash screen
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        prefsManager = PreferencesManager(this)
        
        // Token kontrolü
        if (!prefsManager.isLoggedIn()) {
            // Login'e yönlendir
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // İlk fragment'i göster
        if (savedInstanceState == null) {
            loadFragment(FeedFragment())
        }
        
        // BottomNavigationView setup
        setupBottomNavigation()
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_feed -> {
                    loadFragment(FeedFragment())
                    true
                }
                R.id.nav_map -> {
                    loadFragment(MapFragment())
                    true
                }
                R.id.nav_add -> {
                    loadFragment(com.umutsibara.patitakip.ui.fragments.CreateReportFragment())
                    true
                }
                R.id.nav_chat -> {
                    Toast.makeText(this, "Mesajlar yakında...", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
        
        // İlk seçimi ayarla
        binding.bottomNavigation.selectedItemId = R.id.nav_feed
    }
    
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}

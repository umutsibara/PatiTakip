package com.umutsibara.patitakip.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.umutsibara.patitakip.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.cardReports.setOnClickListener {
            // Navigate to Report Management
            startActivity(android.content.Intent(this, AdminReportListActivity::class.java))
        }

        binding.cardFeedings.setOnClickListener {
            // Navigate to Feeding Management Fragment/Activity
            startActivity(android.content.Intent(this, AdminFeedingListActivity::class.java))
        }
    }
}

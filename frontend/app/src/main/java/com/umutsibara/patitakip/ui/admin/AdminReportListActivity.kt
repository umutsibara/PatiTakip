package com.umutsibara.patitakip.ui.admin

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.umutsibara.patitakip.data.model.Report
import com.umutsibara.patitakip.databinding.ActivityAdminReportListBinding
import com.umutsibara.patitakip.util.Constants
import com.umutsibara.patitakip.util.SessionManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class AdminReportListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminReportListBinding
    private lateinit var adapter: AdminReportAdapter
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminReportListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        fetchReports()
    }

    private fun setupRecyclerView() {
        adapter = AdminReportAdapter(emptyList()) { report ->
            confirmDelete(report)
        }
        binding.rvReports.layoutManager = LinearLayoutManager(this)
        binding.rvReports.adapter = adapter
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun confirmDelete(report: Report) {
        AlertDialog.Builder(this)
            .setTitle("İhbarı Sil?")
            .setMessage("Bu ihbarı kaldırmak istediğinize emin misiniz? Bu işlem geri alınamaz (Soft Delete).")
            .setPositiveButton("Sil") { _, _ ->
                deleteReport(report.id)
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun fetchReports() {
        runOnUiThread { binding.progressBar.visibility = View.VISIBLE }

        val sessionManager = SessionManager(this)
        val token = sessionManager.getAuthToken()
        
        val url = "${Constants.BASE_URL}${Constants.ENDPOINT_ADMIN_REPORTS}"
        
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()
            
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@AdminReportListActivity, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful && body != null) {
                        try {
                            val gson = Gson()
                            val jsonObject = gson.fromJson(body, JsonObject::class.java)
                            if (jsonObject.has("data")) {
                                val reportsType = object : TypeToken<List<Report>>() {}.type
                                val reports = gson.fromJson<List<Report>>(jsonObject.get("data"), reportsType)
                                adapter.updateData(reports)
                                
                                if (reports.isEmpty()) {
                                    binding.tvEmpty.visibility = View.VISIBLE
                                } else {
                                    binding.tvEmpty.visibility = View.GONE
                                }
                            } else {
                                binding.tvEmpty.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@AdminReportListActivity, "Veri işleme hatası", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@AdminReportListActivity, "Yükleme hatası: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun deleteReport(reportId: Int?) {
        if (reportId == null) return
        
        runOnUiThread { binding.progressBar.visibility = View.VISIBLE }
        
        val sessionManager = SessionManager(this)
        val token = sessionManager.getAuthToken()
        
        val url = "${Constants.BASE_URL}${Constants.ENDPOINT_ADMIN_REPORTS}/$reportId"
        
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .delete()
            .build()
            
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@AdminReportListActivity, "Silme hatası: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        Toast.makeText(this@AdminReportListActivity, "İhbar silindi", Toast.LENGTH_SHORT).show()
                        fetchReports() // Refresh list
                    } else {
                         Toast.makeText(this@AdminReportListActivity, "Silinemedi: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}

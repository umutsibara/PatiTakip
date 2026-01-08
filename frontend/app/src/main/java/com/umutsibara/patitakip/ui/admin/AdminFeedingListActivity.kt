package com.umutsibara.patitakip.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.umutsibara.patitakip.data.model.Feeding
import com.umutsibara.patitakip.databinding.ActivityAdminFeedingListBinding
import com.umutsibara.patitakip.util.Constants
import com.umutsibara.patitakip.util.SessionManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class AdminFeedingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminFeedingListBinding
    private lateinit var adapter: AdminFeedingAdapter
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminFeedingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        fetchFeedings()
    }

    private fun setupRecyclerView() {
        adapter = AdminFeedingAdapter(emptyList()) { feeding ->
            confirmDelete(feeding)
        }
        binding.rvFeedings.layoutManager = LinearLayoutManager(this)
        binding.rvFeedings.adapter = adapter
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun confirmDelete(feeding: Feeding) {
        AlertDialog.Builder(this)
            .setTitle("Beslemeyi Sil?")
            .setMessage("Bu besleme kaydını silmek istediğinize emin misiniz? (Soft Delete)")
            .setPositiveButton("Sil") { _, _ ->
                deleteFeeding(feeding.id)
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun fetchFeedings() {
        runOnUiThread { binding.progressBar.visibility = View.VISIBLE }

        val sessionManager = SessionManager(this)
        val token = sessionManager.getAuthToken()
        
        val url = "${Constants.BASE_URL}${Constants.ENDPOINT_ADMIN_FEEDINGS}"
        
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()
            
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@AdminFeedingListActivity, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
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
                                val feedingType = object : TypeToken<List<Feeding>>() {}.type
                                val feedings = gson.fromJson<List<Feeding>>(jsonObject.get("data"), feedingType)
                                adapter.updateData(feedings)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@AdminFeedingListActivity, "Veri işleme hatası", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@AdminFeedingListActivity, "Yükleme hatası: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun deleteFeeding(feedingId: Int) {
        runOnUiThread { binding.progressBar.visibility = View.VISIBLE }
        
        val sessionManager = SessionManager(this)
        val token = sessionManager.getAuthToken()
        
        val url = "${Constants.BASE_URL}${Constants.ENDPOINT_ADMIN_FEEDINGS}/$feedingId"
        
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .delete()
            .build()
            
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@AdminFeedingListActivity, "Silme hatası: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        Toast.makeText(this@AdminFeedingListActivity, "Besleme silindi", Toast.LENGTH_SHORT).show()
                        fetchFeedings() // Refresh list
                    } else {
                         Toast.makeText(this@AdminFeedingListActivity, "Silinemedi: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}

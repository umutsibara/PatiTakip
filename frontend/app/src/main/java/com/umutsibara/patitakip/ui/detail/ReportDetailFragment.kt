package com.umutsibara.patitakip.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.umutsibara.patitakip.R
import com.umutsibara.patitakip.data.api.RetrofitClient
import com.umutsibara.patitakip.data.model.Report
import com.umutsibara.patitakip.data.repository.ReportRepository
import com.umutsibara.patitakip.databinding.FragmentReportDetailBinding
import com.umutsibara.patitakip.util.Constants
import com.umutsibara.patitakip.util.SessionManager

class ReportDetailFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentReportDetailBinding? = null
    private val binding get() = _binding!!
    private var report: Report? = null
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Retrieve report if passed via bundle
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportDetailBinding.inflate(inflater, container, false)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        report?.let { displayReportDetails(it) }
        setupListeners()
        setupAdminControls()
    }
    
    // Admin features
    private fun setupAdminControls() {
        val sessionManager = SessionManager(requireContext())
        val userRole = sessionManager.getUserRole()
        
        if (userRole == "yonetici" || userRole == "admin") {
            binding.btnDeleteDetail.visibility = View.VISIBLE
            binding.btnDeleteDetail.setOnClickListener {
                confirmDelete()
            }
        } else {
            binding.btnDeleteDetail.visibility = View.GONE
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(requireContext())
            .setTitle("İhbarı Sil?")
            .setMessage("Bu ihbarı silmek istediğinize emin misiniz? (Admin)")
            .setPositiveButton("Sil") { _, _ ->
                 deleteReport()
            }
            .setNegativeButton("İptal", null)
            .show()
    }
    
    private fun deleteReport() {
       val apiService = RetrofitClient.getApiService(requireContext())
       val repository = ReportRepository(apiService)
       report?.id?.let { id ->
           // Using coroutine scope from lifecycle
           lifecycleScope.launchWhenStarted {
               try {
                  val response = repository.deleteReport(id)
                  if (response.isSuccessful) {
                      Toast.makeText(context, "İhbar silindi", Toast.LENGTH_SHORT).show()
                      parentFragmentManager.popBackStack()
                  } else {
                      Toast.makeText(context, "Silme hatası: ${response.code()}", Toast.LENGTH_SHORT).show()
                  }
               } catch (e: Exception) {
                   Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
               }
           }
       }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.vMapClickOverlay.setOnClickListener {
             // Navigate to MapFragment focused on this location
             parentFragmentManager.beginTransaction()
                 .replace(R.id.fragment_container, com.umutsibara.patitakip.ui.map.MapFragment())
                 .addToBackStack(null)
                 .commit()
        }
    }

    private fun displayReportDetails(report: Report) {
        binding.apply {
            tvTitle.text = report.title
            tvDescription.text = report.description
            tvCategory.text = when (report.category) {
                "REPORT" -> "İhbar"
                "FEEDING" -> "Besleme"
                "ADOPTION" -> "Sahiplendirme"
                else -> "Genel"
            }
            tvUser.text = report.creatorName ?: "Anonim"
            tvAnimalType.text = report.animalType
            
            // Load photo
            val photoUrl = report.photoUrl
            if (!photoUrl.isNullOrEmpty()) {
                val baseUrl = Constants.BASE_URL.removeSuffix("api/")
                val fullPhotoUrl = baseUrl + photoUrl.removePrefix("/")
                
                Glide.with(requireContext())
                    .load(fullPhotoUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivPhoto)
                
                ivPhoto.visibility = View.VISIBLE
            } else {
                ivPhoto.visibility = View.GONE
            }

            // Update Map if ready
            updateMapLocation()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.setAllGesturesEnabled(false) // Static map
        updateMapLocation()
    }

    private fun updateMapLocation() {
        val map = googleMap ?: return
        val currentReport = report ?: return
        val lat = currentReport.latitude ?: return
        val lng = currentReport.longitude ?: return

        val position = LatLng(lat, lng)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
        map.addMarker(MarkerOptions().position(position))
    }

    // Lifecycle methods for MapView
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        _binding?.mapView?.onLowMemory()
    }

    override fun onDestroyView() {
        _binding?.mapView?.onDestroy()
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(report: Report) = ReportDetailFragment().apply {
            this.report = report
        }
    }
}

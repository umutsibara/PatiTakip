package com.umutsibara.patitakip.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.umutsibara.patitakip.R
import com.umutsibara.patitakip.data.model.Report
import com.umutsibara.patitakip.databinding.FragmentReportDetailBinding
import com.umutsibara.patitakip.util.Constants

class ReportDetailFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentReportDetailBinding? = null
    private val binding get() = _binding!!
    private var report: Report? = null
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Retrieve report from arguments if passed via Bundle
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
        
        report?.let { displayReport(it) }
        
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.vMapClickOverlay.setOnClickListener {
             // Navigate to MapFragment focused on this location
             // In a real app, you might pass arguments to MapFragment
             parentFragmentManager.beginTransaction()
                 .replace(R.id.fragment_container, com.umutsibara.patitakip.ui.map.MapFragment())
                 .addToBackStack(null)
                 .commit()
        }
    }

    private fun displayReport(report: Report) {
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
            // Removed text location setting since we use map now

            // Load photo
            val photoUrl = report.photoUrl
            if (!photoUrl.isNullOrEmpty()) {
                val baseUrl = Constants.BASE_URL.removeSuffix("api/")
                val fullPhotoUrl = baseUrl + photoUrl.removePrefix("/")
                
                Glide.with(requireContext())
                    .load(fullPhotoUrl)
                    .placeholder(R.drawable.ic_launcher_background) // Consider using a better placeholder
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

    // Forwarding Lifecycle methods for MapView
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
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(report: Report) = ReportDetailFragment().apply {
            this.report = report
        }
    }
}

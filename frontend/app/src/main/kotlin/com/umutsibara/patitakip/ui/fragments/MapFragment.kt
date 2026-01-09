package com.umutsibara.patitakip.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.umutsibara.patitakip.R
import com.umutsibara.patitakip.databinding.FragmentMapBinding
import com.umutsibara.patitakip.network.ApiClient
import com.umutsibara.patitakip.network.ApiService
import com.umutsibara.patitakip.utils.PreferencesManager

class MapFragment : Fragment(), OnMapReadyCallback {
    
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    
    private var googleMap: GoogleMap? = null
    private val markers = mutableListOf<MarkerData>()
    private var selectedFilter = FilterType.ALL
    private lateinit var prefsManager: PreferencesManager
    private val apiService: ApiService by lazy { ApiClient.getApiService() }
    
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val DEFAULT_ZOOM = 12f
        // Default location: Istanbul
        private val DEFAULT_LOCATION = LatLng(41.0082, 28.9784)
    }
    
    enum class FilterType {
        ALL, ADOPTION, FEEDING, REPORTING
    }
    
    data class MarkerData(
        val marker: Marker,
        val type: FilterType,
        val title: String,
        val description: String
    )
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize preferences
        prefsManager = PreferencesManager(requireContext())
        
        // Initialize MapView
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        
        setupClickListeners()
        setupFilterChips()
    }
    
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Configure map settings
        googleMap?.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled = true
            uiSettings.isMyLocationButtonEnabled = true
            
            // Check location permission
            if (hasLocationPermission()) {
                try {
                    isMyLocationEnabled = true
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            } else {
                requestLocationPermission()
            }
        }
        
        // Move camera to default location
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM))
        
        // Load markers from backend
        loadMarkersFromBackend()
    }
    
    private fun setupClickListeners() {
        // Back button
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        
        // FAB - Create Report
        binding.fabAddReport.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CreateReportFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    
    private fun setupFilterChips() {
        // Set initial selection
        binding.chipAll.isChecked = true
        
        // All filter
        binding.chipAll.setOnClickListener {
            updateFilterSelection(FilterType.ALL, binding.chipAll)
        }
        
        // Adoption filter
        binding.chipAdoption.setOnClickListener {
            updateFilterSelection(FilterType.ADOPTION, binding.chipAdoption)
        }
        
        // Feeding filter
        binding.chipFeeding.setOnClickListener {
            updateFilterSelection(FilterType.FEEDING, binding.chipFeeding)
        }
        
        // Reporting filter
        binding.chipReporting.setOnClickListener {
            updateFilterSelection(FilterType.REPORTING, binding.chipReporting)
        }
    }
    
    private fun updateFilterSelection(filterType: FilterType, selectedChip: Chip) {
        selectedFilter = filterType
        
        // Update chip colors
        val chips = listOf(
            binding.chipAll,
            binding.chipAdoption,
            binding.chipFeeding,
            binding.chipReporting
        )
        
        chips.forEach { chip ->
            if (chip == selectedChip) {
                chip.setChipBackgroundColorResource(R.color.paw_green)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                val grayColor = android.graphics.Color.parseColor("#F5F5F5")
                chip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(grayColor)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            }
        }
        
        // Filter markers
        applyFilter()
    }
    
    private fun applyFilter() {
        markers.forEach { markerData ->
            markerData.marker.isVisible = when (selectedFilter) {
                FilterType.ALL -> true
                else -> markerData.type == selectedFilter
            }
        }
    }
    
    private fun loadMarkersFromBackend() {
        // Using mock data for demonstration
        try {
            val mockMarkers = com.umutsibara.patitakip.utils.MockDataProvider.getMockMapMarkers()
            
            mockMarkers.forEach { markerData ->
                val filterType = when (markerData.category) {
                    "ADOPTION" -> FilterType.ADOPTION
                    "FEEDING" -> FilterType.FEEDING
                    "REPORT" -> FilterType.REPORTING
                    else -> FilterType.ALL
                }
                
                val marker = addMarkerToMap(
                    markerData.location, 
                    markerData.emoji,
                    filterType
                )
                
                // Set data for info window
                marker?.tag = markerData
            }
            

        } catch (e: Exception) {
            android.util.Log.e("MapFragment", "Error loading mock markers", e)
            Toast.makeText(
                requireContext(),
                "Hata: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            // Fallback to example markers if mock data fails
            loadExampleMarkers()
        }
        
        /* ORIGINAL API CALL - Commented out for mock data demonstration
        lifecycleScope.launch {
            try {
                // Load reports
                val reportsResponse = apiService.getReports(limit = 100)
                if (reportsResponse.isSuccessful) {
                    reportsResponse.body()?.data?.forEach { report ->
                        val emoji = getAnimalEmoji(report.hayvanTuru)
                        val filterType = if (report.kategori.contains("sahiplen", true) || report.kategori.contains("ADOPTION", true)) {
                            FilterType.ADOPTION
                        } else if (report.kategori.contains("FEEDING", true)) {
                            FilterType.FEEDING
                        } else {
                            FilterType.REPORTING
                        }
                        addMarkerToMap(
                            LatLng(report.enlem, report.boylam),
                            emoji,
                            filterType
                        )
                    }
                }
                
                // Note: Feedings don't have location data in current model
                // Skipping feedings for now - backend needs to add location fields
                
                // If no data from backend, show example markers
                if (markers.isEmpty()) {
                    loadExampleMarkers()
                }
            } catch (e: Exception) {
                android.util.Log.e("MapFragment", "Error loading markers", e)
                // Fallback to example markers on error
                loadExampleMarkers()
            }
        }
        */
    }
    
    private fun loadExampleMarkers() {
        // Example markers for demonstration
        val exampleData = listOf(
            Triple(LatLng(41.0382, 28.9844), FilterType.FEEDING, "🐱"),
            Triple(LatLng(41.0282, 28.9644), FilterType.ADOPTION, "🐰"),
            Triple(LatLng(40.9982, 28.9944), FilterType.REPORTING, "🐶"),
            Triple(LatLng(41.0182, 28.9584), FilterType.FEEDING, "🐦"),
            Triple(LatLng(41.0482, 28.9744), FilterType.ADOPTION, "🐰"),
            Triple(LatLng(40.9882, 28.9684), FilterType.FEEDING, "🐱"),
            Triple(LatLng(41.0582, 29.0044), FilterType.REPORTING, "🐶"),
            Triple(LatLng(41.0082, 29.0144), FilterType.ADOPTION, "🐶")
        )
        
        exampleData.forEach { (location, type, emoji) ->
            addMarkerToMap(location, emoji, type)
        }
    }
    
    private fun addMarkerToMap(location: LatLng, emoji: String, filterType: FilterType): Marker? {
        val markerIcon = createCustomMarker(emoji)
        
        val marker = googleMap?.addMarker(
            MarkerOptions()
                .position(location)
                .icon(markerIcon)
                .anchor(0.5f, 0.5f)
        )
        
        marker?.let {
            markers.add(MarkerData(it, filterType, emoji, ""))
        }
        
        return marker
    }
    
    private fun getAnimalEmoji(animalType: String?): String {
        return when (animalType?.lowercase()) {
            "kedi", "cat" -> "🐱"
            "köpek", "dog" -> "🐶"
            "kuş", "bird" -> "🐦"
            "tavşan", "rabbit" -> "🐰"
            "hamster" -> "🐹"
            "balık", "fish" -> "🐠"
            "kaplumbağa", "turtle" -> "🐢"
            else -> "🐾" // Default paw print for unknown animals
        }
    }
    
    private fun createCustomMarker(emoji: String): BitmapDescriptor {
        val size = 120
        val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        
        // Draw white circle background
        val circlePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            isAntiAlias = true
            style = android.graphics.Paint.Style.FILL
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 2, circlePaint)
        
        // Draw shadow/border
        val borderPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#E0E0E0")
            isAntiAlias = true
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f - 2, borderPaint)
        
        // Draw emoji text
        val textPaint = android.graphics.Paint().apply {
            textSize = 60f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
        }
        
        val textBounds = android.graphics.Rect()
        textPaint.getTextBounds(emoji, 0, emoji.length, textBounds)
        val textY = size / 2f + textBounds.height() / 2f
        
        canvas.drawText(emoji, size / 2f, textY, textPaint)
        
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    
    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    googleMap?.isMyLocationEnabled = true
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Konum izni reddedildi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    // MapView lifecycle methods
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
}

package com.umutsibara.patitakip.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.umutsibara.patitakip.R
import com.umutsibara.patitakip.data.api.RetrofitClient
import com.umutsibara.patitakip.data.model.Report
import com.umutsibara.patitakip.data.repository.ReportRepository
import com.umutsibara.patitakip.databinding.FragmentMapBinding

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MapViewModel
    private var googleMap: GoogleMap? = null
    private var selectedCategory: String = "ALL"
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private var selectedReport: Report? = null

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

        setupViewModel()
        setupMap()
        setupBottomSheet()
        setupListeners()
        setupObservers()
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getApiService(requireContext())
        val repository = ReportRepository(apiService)
        val factory = MapViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MapViewModel::class.java]

        // Fetch reports from API
        viewModel.fetchReports()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun setupListeners() {
        // Category filter chips
        binding.chipGroupCategories.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                selectedCategory = when (checkedIds[0]) {
                    R.id.chipReport -> "REPORT"
                    R.id.chipFeeding -> "FEEDING"
                    R.id.chipAdoption -> "ADOPTION"
                    else -> "ALL"
                }
                updateMapMarkers()
            }
        }

        // My location FAB
        binding.fabMyLocation.setOnClickListener {
            enableMyLocation()
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(41.0082, 28.9784),
                    12f
                )
            )
        }

        // View details button in bottom sheet
        binding.btnViewDetails.setOnClickListener {
            selectedReport?.let { report ->
                navigateToDetail(report)
            }
        }

        // Directions button
        binding.btnDirections.setOnClickListener {
            selectedReport?.let { report ->
                val lat = report.latitude
                val lng = report.longitude
                if (lat != null && lng != null) {
                    val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$lng")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(mapIntent)
                    } else {
                        // Fallback handling if no Maps app, maybe launch browser
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng"))
                        startActivity(browserIntent)
                    }
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.reports.observe(viewLifecycleOwner) { reports ->
            if (reports.isNotEmpty()) {
                updateMapMarkers()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Custom Map Style
        try {
            googleMap?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.map_style
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Move camera to Istanbul
        val istanbul = LatLng(41.0082, 28.9784)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(istanbul, 12f))

        // Enable location if permission granted
        enableMyLocation()

        // Set marker click listener
        googleMap?.setOnMarkerClickListener { marker ->
            val report = marker.tag as? Report
            report?.let {
                showReportPreview(it)
            }
            true
        }

        // Update markers with initial data
        updateMapMarkers()
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
            googleMap?.uiSettings?.isMyLocationButtonEnabled = false // Using custom FAB
        }
    }

    private fun updateMapMarkers() {
        googleMap?.clear()

        val filteredReports = if (selectedCategory == "ALL") {
            viewModel.reports.value ?: emptyList()
        } else {
            viewModel.reports.value?.filter { it.category == selectedCategory } ?: emptyList()
        }

        filteredReports.forEach { report ->
            // Skip if location data is missing
            val lat = report.latitude
            val lng = report.longitude
            if (lat == null || lng == null) {
                return@forEach
            }
            
            val position = LatLng(lat, lng)
            val markerIcon = when (report.category) {
                "REPORT" -> bitmapDescriptorFromVector(R.drawable.ic_pin_report)
                "FEEDING" -> bitmapDescriptorFromVector(R.drawable.ic_pin_feeding)
                "ADOPTION" -> bitmapDescriptorFromVector(R.drawable.ic_pin_adoption)
                else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            }

            val marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(report.title ?: "İhbar")
                    .snippet(report.description ?: "")
                    .icon(markerIcon)
            )
            marker?.tag = report
        }
    }

    private fun bitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor? {
        return try {
            val vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorResId)
            vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
            val bitmap = Bitmap.createBitmap(
                vectorDrawable.intrinsicWidth,
                vectorDrawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            vectorDrawable.draw(canvas)
            BitmapDescriptorFactory.fromBitmap(bitmap)
        } catch (e: Exception) {
            null
        }
    }

    private fun showReportPreview(report: Report) {
        selectedReport = report
        binding.tvBottomSheetTitle.text = report.title ?: "İhbar"
        binding.tvBottomSheetDescription.text = report.description ?: ""

        val categoryText = when (report.category) {
            "REPORT" -> "İhbar"
            "FEEDING" -> "Besleme"
            "ADOPTION" -> "Sahiplendirme"
            else -> report.category
        }
        binding.tvBottomSheetCategory.text = categoryText

        val categoryColor = when (report.category) {
            "REPORT" -> R.color.category_report
            "FEEDING" -> R.color.category_feeding
            "ADOPTION" -> R.color.category_adoption
            else -> R.color.text_secondary
        }
        binding.tvBottomSheetCategory.setBackgroundColor(
            ContextCompat.getColor(requireContext(), categoryColor)
        )

        binding.bottomSheet.visibility = View.VISIBLE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = 400 
    }

    private fun navigateToDetail(report: Report) {
        val detailFragment = com.umutsibara.patitakip.ui.detail.ReportDetailFragment.newInstance(report)
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

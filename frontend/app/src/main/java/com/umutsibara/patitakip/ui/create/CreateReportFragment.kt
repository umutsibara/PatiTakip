package com.umutsibara.patitakip.ui.create

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.umutsibara.patitakip.R
import com.umutsibara.patitakip.data.api.RetrofitClient
import com.umutsibara.patitakip.data.model.AnimalType
import com.umutsibara.patitakip.data.repository.ReportRepository
import com.umutsibara.patitakip.databinding.FragmentCreateReportBinding
import com.umutsibara.patitakip.util.SessionManager

class CreateReportFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentCreateReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CreateReportViewModel
    
    private var selectedAnimalType: String = ""
    private val animalTypes = mutableListOf<AnimalType>()
    
    private var googleMap: GoogleMap? = null
    private var selectedLatLng: LatLng = LatLng(41.0082, 28.9784) // Default: Istanbul
    
    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            binding.ivPhotoPreview.setImageURI(it)
            binding.ivPhotoPreview.visibility = View.VISIBLE
            viewModel.uploadPhoto(requireContext(), it)
        }
    }
    
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            enableMyLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()
        setupListeners()
        setupReportTypeSpinner()
        setupMap()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Move camera to default location
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 13f))
        
        // Enable location if permission granted
        enableMyLocation()
        
        // Update location on camera movement
        googleMap?.setOnCameraIdleListener {
            selectedLatLng = googleMap?.cameraPosition?.target ?: selectedLatLng
            updateLocationText(selectedLatLng)
        }
        
        // Initial location text
        updateLocationText(selectedLatLng)
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
            googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
    
    private fun updateLocationText(latLng: LatLng) {
        binding.tvSelectedLocation.text = "📍 ${String.format("%.4f", latLng.latitude)}, ${String.format("%.4f", latLng.longitude)}"
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getApiService(requireContext())
        val repository = ReportRepository(apiService)
        val sessionManager = SessionManager(requireContext())
        val factory = CreateReportViewModelFactory(repository, sessionManager)
        viewModel = ViewModelProvider(this, factory)[CreateReportViewModel::class.java]
        
        // Fetch animal types after ViewModel is ready
        viewModel.fetchAnimalTypes()
    }

    private fun setupReportTypeSpinner() {
        val reportTypesAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.report_types,
            android.R.layout.simple_spinner_item
        )
        reportTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerReportType.adapter = reportTypesAdapter
    }

    private fun setupListeners() {
        // Category selection - show/hide report type
        binding.rgCategory.setOnCheckedChangeListener { _, checkedId ->
            val isReport = checkedId == R.id.rbReport
            binding.tvReportTypeLabel.visibility = if (isReport) View.VISIBLE else View.GONE
            binding.spinnerReportType.visibility = if (isReport) View.VISIBLE else View.GONE
        }

        // Photo selection
        binding.btnSelectPhoto.setOnClickListener {
            photoPickerLauncher.launch("image/*")
        }

        // Submit button
        binding.btnSubmit.setOnClickListener {
            submitForm()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSubmit.isEnabled = !isLoading
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.animalTypes.observe(viewLifecycleOwner) { types ->
            types?.let {
                animalTypes.clear()
                animalTypes.addAll(it)
                
                val typeNames = it.map { type -> type.name }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    typeNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerAnimalType.adapter = adapter
                
                binding.spinnerAnimalType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedAnimalType = animalTypes[position].name
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        selectedAnimalType = ""
                    }
                }
            }
        }

        viewModel.createSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "İhbar başarıyla oluşturuldu!", Toast.LENGTH_SHORT).show()
                clearForm()
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun submitForm() {
        val title = binding.etTitle.text.toString()
        val description = binding.etDescription.text.toString()

        val selectedId = binding.rgCategory.checkedRadioButtonId
        val category = when (selectedId) {
            R.id.rbFeeding -> "FEEDING"
            R.id.rbAdoption -> "ADOPTION"
            else -> "REPORT"
        }

        val reportType = if (category == "REPORT") {
            binding.spinnerReportType.selectedItem?.toString()
        } else {
            null
        }

        viewModel.createReport(
            title = title,
            description = description,
            category = category,
            reportType = reportType,
            animalType = selectedAnimalType,
            latitude = selectedLatLng.latitude,
            longitude = selectedLatLng.longitude
        )
    }

    private fun clearForm() {
        binding.etTitle.text?.clear()
        binding.etDescription.text?.clear()
        binding.rbReport.isChecked = true
        binding.spinnerAnimalType.setSelection(0)
        binding.spinnerReportType.setSelection(0)
        binding.ivPhotoPreview.visibility = View.GONE
        binding.ivPhotoPreview.setImageURI(null)
        viewModel.clearPhotoUpload()
        selectedLatLng = LatLng(41.0082, 28.9784)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.umutsibara.patitakip.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.umutsibara.patitakip.R
import com.umutsibara.patitakip.databinding.FragmentCreateReportBinding
import com.umutsibara.patitakip.network.ApiClient
import com.umutsibara.patitakip.network.models.CreateReportRequest
import com.umutsibara.patitakip.utils.PreferencesManager
import kotlinx.coroutines.launch

class CreateReportFragment : Fragment() {
    
    private var _binding: FragmentCreateReportBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefsManager: PreferencesManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private var selectedSpecies: String = "Kedi"
    private var selectedPhotoUris: MutableList<Uri> = mutableListOf()
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    
    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            selectedPhotoUris.addAll(uris)
            updatePhotoPreview()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateReportBinding.inflate(inflater, container, false)
        prefsManager = PreferencesManager(requireContext())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackButton()
        setupSharingTypeSpinner()
        setupSpeciesSelection()
        setupLocationButton()
        setupPhotoSelection()
        setupSubmitButton()
    }
    
    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    
    private fun setupSharingTypeSpinner() {
        val sharingTypes = arrayOf("Besleme", "Gönderi", "Sahiplendirme")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            sharingTypes
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSharingType.adapter = adapter
    }
    
    private fun setupSpeciesSelection() {
        // Default selection
        binding.btnSpeciesCat.isSelected = true
        selectedSpecies = "Kedi"
        
        binding.btnSpeciesCat.setOnClickListener {
            clearSpeciesSelection()
            binding.btnSpeciesCat.isSelected = true
            selectedSpecies = "Kedi"
        }
        
        binding.btnSpeciesDog.setOnClickListener {
            clearSpeciesSelection()
            binding.btnSpeciesDog.isSelected = true
            selectedSpecies = "Köpek"
        }
        
        binding.btnSpeciesBird.setOnClickListener {
            clearSpeciesSelection()
            binding.btnSpeciesBird.isSelected = true
            selectedSpecies = "Kuş"
        }
        
        binding.btnSpeciesOther.setOnClickListener {
            clearSpeciesSelection()
            binding.btnSpeciesOther.isSelected = true
            selectedSpecies = "Diğer"
        }
    }
    
    private fun clearSpeciesSelection() {
        binding.btnSpeciesCat.isSelected = false
        binding.btnSpeciesDog.isSelected = false
        binding.btnSpeciesBird.isSelected = false
        binding.btnSpeciesOther.isSelected = false
    }
    
    private fun setupLocationButton() {
        binding.btnUseCurrentLocation.setOnClickListener {
            getCurrentLocation()
        }
    }
    
    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    binding.tvLocationStatus.text = "✅ Konum eklendi (${String.format("%.4f, %.4f", 
                        location.latitude, location.longitude)})"
                    binding.tvLocationStatus.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.success))
                    Toast.makeText(requireContext(), "Konum başarıyla eklendi!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Konum alınamadı", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Konum hatası: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun setupPhotoSelection() {
        binding.btnAddPhoto.setOnClickListener {
            photoPickerLauncher.launch("image/*")
        }
    }
    
    private fun updatePhotoPreview() {
        binding.photoPreviewLayout.removeAllViews()
        
        if (selectedPhotoUris.isEmpty()) {
            binding.photoPreviewContainer.visibility = View.GONE
            return
        }
        
        binding.photoPreviewContainer.visibility = View.VISIBLE
        
        selectedPhotoUris.forEachIndexed { index, uri ->
            val photoFrame = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.MarginLayoutParams(200, 200).apply {
                    marginEnd = 16
                }
            }
            
            val imageView = ImageView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageURI(uri)
                setBackgroundResource(R.drawable.bg_card)
            }
            
            // Remove button
            val removeButton = ImageView(requireContext()).apply {
                layoutParams = FrameLayout.LayoutParams(32, 32).apply {
                    gravity = android.view.Gravity.TOP or android.view.Gravity.END
                    topMargin = 8
                    marginEnd = 8
                }
                setImageResource(R.drawable.ic_delete)
                setBackgroundResource(android.R.drawable.screen_background_light_transparent)
                setPadding(4, 4, 4, 4)
                setOnClickListener {
                    selectedPhotoUris.removeAt(index)
                    updatePhotoPreview()
                }
            }
            
            photoFrame.addView(imageView)
            photoFrame.addView(removeButton)
            binding.photoPreviewLayout.addView(photoFrame)
        }
    }
    
    private fun setupSubmitButton() {
        binding.btnSubmit.setOnClickListener {
            submitReport()
        }
    }
    
    private fun submitReport() {
        val caption = binding.etCaption.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        
        // Validation
        if (caption.isEmpty()) {
            Toast.makeText(requireContext(), "Lütfen bir başlık girin", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Lütfen detaylı açıklama girin", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (currentLatitude == null || currentLongitude == null) {
            Toast.makeText(requireContext(), "Lütfen konum ekleyin", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Get sharing type
        val category = binding.spinnerSharingType.selectedItem.toString().uppercase()
        
        val request = CreateReportRequest(
            kullaniciId = prefsManager.getUserId(),
            baslik = caption,
            aciklama = description,
            enlem = currentLatitude ?: 41.0082,
            boylam = currentLongitude ?: 28.9784,
            kategori = category,
            hayvanTuru = selectedSpecies,
            ihbarTuru = category,
            adres = null,
            fotoId = null // TODO: Photo upload
        )
        
        createReport(request)
    }
    
    private fun createReport(request: CreateReportRequest) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSubmit.isEnabled = false
        
        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(prefsManager.getToken())
                    .createReport(request)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(
                        requireContext(),
                        "Gönderi başarıyla oluşturuldu!",
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // Navigate back
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Hata: ${response.body()?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Bağlantı hatası: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.btnSubmit.isEnabled = true
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}

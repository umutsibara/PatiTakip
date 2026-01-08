package com.umutsibara.patitakip.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.umutsibara.patitakip.R
import com.umutsibara.patitakip.databinding.FragmentFeedBinding

class FeedFragment : Fragment() {
    
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // Feed an Animal Card
        binding.cardFeedAnimal.setOnClickListener {
            // Navigate to feeding timeline
            parentFragmentManager.beginTransaction()
                .replace(com.umutsibara.patitakip.R.id.fragmentContainer, FeedingFragment())
                .addToBackStack(null)
                .commit()
        }
        
        // Report Animal Card
        binding.cardReportAnimal.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CreateReportFragment())
                .addToBackStack(null)
                .commit()
        }
        
        // Adopt Companion Card
        binding.cardAdoptCompanion.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Sahiplendirme özelliği yakında...",
                Toast.LENGTH_SHORT
            ).show()
            // TODO: Navigate to adoption feature
        }
        
        // View Full Map Button
        binding.btnViewMap.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Harita görünümüne yönlendiriliyor...",
                Toast.LENGTH_SHORT
            ).show()
            // TODO: Navigate to map fragment
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

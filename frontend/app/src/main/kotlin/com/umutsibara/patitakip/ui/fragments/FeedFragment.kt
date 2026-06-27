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
        // Feed an Animal Card - Navigate to FEEDING posts only
        binding.cardFeedAnimal.setOnClickListener {
            val fragment = ReportsFragment.newInstance("FEEDING")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        
        // Report Animal Card - Navigate to REPORT category posts
        binding.cardReportAnimal.setOnClickListener {
            val fragment = ReportsFragment.newInstance("REPORT")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        
        // Adopt Companion Card - Navigate to ADOPTION category
        binding.cardAdoptCompanion.setOnClickListener {
            val fragment = ReportsFragment.newInstance("ADOPTION")
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        
        // View Full Map Button
        binding.btnViewMap.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MapFragment())
                .addToBackStack(null)
                .commit()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

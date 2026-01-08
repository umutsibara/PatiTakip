package com.umutsibara.patitakip.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umutsibara.patitakip.databinding.FragmentProfileBinding
import com.umutsibara.patitakip.utils.PreferencesManager

class ProfileFragment : Fragment() {
    
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefsManager: PreferencesManager
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        prefsManager = PreferencesManager(requireContext())
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Kullanıcı bilgilerini göster
        binding.tvUsername.text = prefsManager.getUserName() ?: "Kullanıcı"
        binding.tvUserId.text = "ID: ${prefsManager.getUserId()}"
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

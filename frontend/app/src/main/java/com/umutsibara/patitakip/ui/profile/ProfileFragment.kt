package com.umutsibara.patitakip.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.umutsibara.patitakip.data.api.RetrofitClient
import com.umutsibara.patitakip.data.repository.AuthRepository
import com.umutsibara.patitakip.databinding.FragmentProfileBinding
import com.umutsibara.patitakip.ui.login.LoginActivity
import com.umutsibara.patitakip.util.SessionManager

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding
        get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()
        setupListeners()

        viewModel.fetchProfile()
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getApiService(requireContext())
        val repository = AuthRepository(apiService)
        val sessionManager = SessionManager(requireContext())
        val factory = ProfileViewModelFactory(repository, sessionManager)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.tvUsername.text = user.username
            binding.tvEmail.text = user.email
            binding.tvScore.text = user.score?.toString() ?: "0"

            // Admin kontrolü
            if (user.role == "yonetici" || user.role == "admin") {
                binding.btnAdminPanel.visibility = View.VISIBLE
            } else {
                binding.btnAdminPanel.visibility = View.GONE
            }
        }
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.btnAdminPanel.setOnClickListener {
            startActivity(Intent(requireContext(), com.umutsibara.patitakip.ui.admin.AdminActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

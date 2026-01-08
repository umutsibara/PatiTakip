package com.umutsibara.patitakip.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.umutsibara.patitakip.R
import com.umutsibara.patitakip.data.api.RetrofitClient
import com.umutsibara.patitakip.data.repository.ReportRepository
import com.umutsibara.patitakip.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding
        get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ReportAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupViewModel()
        setupObservers()

        // Fetch reports from API
        viewModel.fetchReports()

        binding.fabCreate.setOnClickListener {
            parentFragmentManager
                    .beginTransaction()
                    .replace(
                            R.id.fragment_container,
                            com.umutsibara.patitakip.ui.create.CreateReportFragment()
                    )
                    .addToBackStack(null)
                    .commit()
        }

        binding.fabMap.setOnClickListener {
            parentFragmentManager
                    .beginTransaction()
                    .replace(
                            R.id.fragment_container,
                            com.umutsibara.patitakip.ui.map.MapFragment()
                    )
                    .addToBackStack(null)
                    .commit()
        }

        binding.btnProfile.setOnClickListener {
            parentFragmentManager
                    .beginTransaction()
                    .replace(
                            R.id.fragment_container,
                            com.umutsibara.patitakip.ui.profile.ProfileFragment()
                    )
                    .addToBackStack(null)
                    .commit()
        }
    }

    private fun setupRecyclerView() {
        adapter = ReportAdapter(emptyList()) { report ->
            // Navigate to detail screen
            val detailFragment = com.umutsibara.patitakip.ui.detail.ReportDetailFragment.newInstance(report)
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.rvReports.layoutManager = LinearLayoutManager(context)
        binding.rvReports.adapter = adapter
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.getApiService(requireContext())
        val repository = ReportRepository(apiService)
        val factory = HomeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
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

        viewModel.reports.observe(viewLifecycleOwner) { reports -> adapter.updateData(reports) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

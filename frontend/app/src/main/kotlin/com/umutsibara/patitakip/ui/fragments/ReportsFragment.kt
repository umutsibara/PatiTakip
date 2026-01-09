package com.umutsibara.patitakip.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.umutsibara.patitakip.databinding.FragmentReportsBinding
import com.umutsibara.patitakip.network.ApiClient
import com.umutsibara.patitakip.network.models.Report
import com.umutsibara.patitakip.ui.adapters.ReportAdapter
import com.umutsibara.patitakip.utils.PreferencesManager
import kotlinx.coroutines.launch

class ReportsFragment : Fragment() {
    
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefsManager: PreferencesManager
    private lateinit var reportAdapter: ReportAdapter
    private val reports = mutableListOf<Report>()
    private var categoryFilter: String? = null
    
    companion object {
        private const val ARG_CATEGORY = "category"
        
        fun newInstance(category: String? = null): ReportsFragment {
            return ReportsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category)
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryFilter = arguments?.getString(ARG_CATEGORY)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        prefsManager = PreferencesManager(requireContext())
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        setupSwipeRefresh()
        updatePageTitle()
        loadReports()
    }
    
    private fun updatePageTitle() {
        val title = when (categoryFilter) {
            "FEEDING" -> "Besleme İhbarları"
            "ADOPTION" -> "Sahiplendirme İlanları"
            "REPORT" -> "Acil İhbarlar"
            "SERVICE" -> "Hizmet İlanları"
            "DONATION" -> "Bağış İlanları"
            else -> "Tüm İhbarlar"
        }
        binding.tvPageTitle.text = title
    }
    
    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    
    private fun setupRecyclerView() {
        reportAdapter = ReportAdapter(
            reports = reports,
            onReportClick = { report ->
                Toast.makeText(
                    requireContext(),
                    "İhbar detayı: ${report.kullaniciAdi}",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO: Navigate to detail screen
            },
            onLikeClick = { report ->
                toggleLike(report)
            },
            onCommentClick = { report ->
                Toast.makeText(
                    requireContext(),
                    "Yorumlar: ${report.yorumSayisi}",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO: Navigate to detail/comments
            }
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = reportAdapter
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadReports()
        }
    }
    
    private fun loadReports() {
        binding.swipeRefresh.isRefreshing = true
        binding.progressBar.visibility = View.VISIBLE
        
        // Using mock data for demonstration
        try {
            val mockReports = com.umutsibara.patitakip.utils.MockDataProvider.getMockReports(categoryFilter)
            reports.clear()
            reports.addAll(mockReports)
            reportAdapter.notifyDataSetChanged()
            
            // Empty state
            if (reports.isEmpty()) {
                binding.llEmpty.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.llEmpty.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
            

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Hata: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        } finally {
            binding.swipeRefresh.isRefreshing = false
            binding.progressBar.visibility = View.GONE
        }
        
        /* ORIGINAL API CALL - Commented out for mock data demonstration
        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(prefsManager.getToken())
                    .getReports(
                        limit = 50, 
                        offset = 0,
                        category = categoryFilter  // Apply category filter
                    )
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val newReports = response.body()?.data ?: emptyList()
                    reports.clear()
                    reports.addAll(newReports)
                    reportAdapter.notifyDataSetChanged()
                    
                    // Empty state
                    if (reports.isEmpty()) {
                        binding.llEmpty.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        binding.llEmpty.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "İhbarlar yüklenemedi: ${response.body()?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Hata: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.swipeRefresh.isRefreshing = false
                binding.progressBar.visibility = View.GONE
            }
        }
        */
    }
    
    private fun toggleLike(report: Report) {
        // TODO: Implement like toggle with API
        Toast.makeText(requireContext(), "Beğenme özelliği yakında...", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

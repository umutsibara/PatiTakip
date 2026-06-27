package com.umutsibara.patitakip.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.umutsibara.patitakip.databinding.FragmentFeedingBinding
import com.umutsibara.patitakip.network.ApiClient
import com.umutsibara.patitakip.network.models.Feeding
import com.umutsibara.patitakip.ui.adapters.FeedingAdapter
import com.umutsibara.patitakip.utils.PreferencesManager
import kotlinx.coroutines.launch

class FeedingFragment : Fragment() {
    
    private var _binding: FragmentFeedingBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefsManager: PreferencesManager
    private lateinit var feedingAdapter: FeedingAdapter
    private val feedings = mutableListOf<Feeding>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedingBinding.inflate(inflater, container, false)
        prefsManager = PreferencesManager(requireContext())
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        loadFeedings()
    }
    
    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    
    private fun setupRecyclerView() {
        feedingAdapter = FeedingAdapter(
            feedings = feedings,
            onLikeClick = { feeding ->
                toggleLike(feeding)
            },
            onCommentClick = { feeding ->
                Toast.makeText(
                    requireContext(),
                    "Yorumlar: ${feeding.commentCount}",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO: Navigate to detail/comments
            },
            onShareClick = { feeding ->
                Toast.makeText(
                    requireContext(),
                    "Paylaşım özelliği yakında...",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onCardClick = { feeding ->
                Toast.makeText(
                    requireContext(),
                    "Besleme detayı: ${feeding.username}",
                    Toast.LENGTH_SHORT
                ).show()
                // TODO: Navigate to detail screen
            }
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = feedingAdapter
        }
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadFeedings()
        }
    }
    
    private fun loadFeedings() {
        binding.swipeRefresh.isRefreshing = true
        binding.progressBar.visibility = View.VISIBLE
        
        // Using mock data for demonstration
        try {
            val mockFeedings = com.umutsibara.patitakip.utils.MockDataProvider.getMockFeedings()
            feedings.clear()
            feedings.addAll(mockFeedings)
            feedingAdapter.notifyDataSetChanged()
            
            // Empty state
            if (feedings.isEmpty()) {
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
                    .getFeedings(limit = 50, offset = 0)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val newFeedings = response.body()?.data ?: emptyList()
                    feedings.clear()
                    feedings.addAll(newFeedings)
                    feedingAdapter.notifyDataSetChanged()
                    
                    // Empty state
                    if (feedings.isEmpty()) {
                        binding.llEmpty.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        binding.llEmpty.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Beslemeler yüklenemedi: ${response.body()?.message}",
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
    
    private fun toggleLike(feeding: Feeding) {
        // TODO: Implement like toggle with API
        Toast.makeText(requireContext(), "Beğenme özelliği yakında...", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

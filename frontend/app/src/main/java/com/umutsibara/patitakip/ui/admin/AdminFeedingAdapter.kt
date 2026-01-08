package com.umutsibara.patitakip.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umutsibara.patitakip.data.model.Feeding
import com.umutsibara.patitakip.databinding.ItemAdminFeedingBinding

class AdminFeedingAdapter(
    private var feedings: List<Feeding>,
    private val onDeleteClick: (Feeding) -> Unit
) : RecyclerView.Adapter<AdminFeedingAdapter.FeedingViewHolder>() {

    class FeedingViewHolder(val binding: ItemAdminFeedingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedingViewHolder {
        val binding = ItemAdminFeedingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedingViewHolder, position: Int) {
        val feeding = feedings[position]
        
        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(feeding)
        }
        
        holder.binding.apply {
            tvZoneName.text = feeding.zoneName ?: "Bölge Belirsiz"
            tvTime.text = formatTime(feeding.feedingTime)
            tvAmount.text = "${feeding.amountKg} kg ${feeding.foodType ?: "Mama"}"
            tvUser.text = "by ${feeding.userName ?: "Anonim"}"
        }
    }
    
    private fun formatTime(timeString: String): String {
        // Simple formatter, can be improved
        // Assuming ISO format from backend 2024-01-01T12:00:00.000Z
        return try {
            timeString.substring(0, 10) + " " + timeString.substring(11, 16)
        } catch (e: Exception) {
            timeString
        }
    }

    override fun getItemCount() = feedings.size

    fun updateData(newFeedings: List<Feeding>) {
        feedings = newFeedings
        notifyDataSetChanged()
    }
}

package com.umutsibara.patitakip.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.umutsibara.patitakip.R
import com.umutsibara.patitakip.data.model.Report
import com.umutsibara.patitakip.databinding.ItemReportBinding
import com.umutsibara.patitakip.util.Constants

class ReportAdapter(
    private var reports: List<Report>,
    private val onReportClick: (Report) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(val binding: ItemReportBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]
        
        // Click listener for detail navigation
        holder.binding.root.setOnClickListener {
            onReportClick(report)
        }
        
        holder.binding.apply {
            tvTitle.text = report.title ?: "Başlık yok"
            tvDescription.text = report.description ?: "Açıklama yok"
            
            // Category with null safety
            tvCategory.text = when (report.category) {
                "REPORT" -> "İhbar"
                "FEEDING" -> "Besleme"
                "ADOPTION" -> "Sahiplendirme"
                null -> "Genel"
                else -> report.category
            }
            tvUser.text = report.creatorName ?: "Anonim"
            
            // Animal type with null safety
            if (!report.animalType.isNullOrEmpty()) {
                val emoji = getAnimalEmoji(report.animalType)
                cvAnimalType.text = "$emoji ${report.animalType}"
                cvAnimalType.visibility = View.VISIBLE
            } else {
                cvAnimalType.visibility = View.GONE
            }

            // Category badge colors with null safety
            val categoryColor = when (report.category) {
                "REPORT" -> "#E53935" // Red
                "FEEDING" -> "#43A047" // Green
                "ADOPTION" -> "#1E88E5" // Blue
                null -> "#9E9E9E" // Gray for null
                else -> "#9E9E9E" // Gray for unknown
            }
            cvCategory.setCardBackgroundColor(Color.parseColor(categoryColor))

            // Load photo with full URL
            val photoUrl = report.photoUrl
            if (!photoUrl.isNullOrEmpty()) {
                val baseUrl = Constants.BASE_URL.removeSuffix("api/")
                val fullPhotoUrl = baseUrl + photoUrl.removePrefix("/")
                
                android.util.Log.d("ReportAdapter", "Attempting to load image from URL: $fullPhotoUrl")
                
                Glide.with(root.context)
                    .load(fullPhotoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_add_photo)
                    .error(R.drawable.ic_add_photo)
                    .centerCrop()
                    .into(ivReportImage)
                
                ivReportImage.visibility = View.VISIBLE
            } else {
                // Show placeholder instead of hiding
                Glide.with(root.context)
                    .load(R.drawable.ic_add_photo) // Use app icon or specific placeholder
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(ivReportImage)
                    
                ivReportImage.visibility = View.VISIBLE
            }
        }
    }

    private fun getAnimalEmoji(animalType: String): String {
        return when (animalType.lowercase()) {
            "kedi" -> "🐱"
            "köpek" -> "🐶"
            "kuş" -> "🐦"
            "hamster" -> "🐹"
            "papağan" -> "🦜"
            "balık" -> "🐟"
            "kirpi" -> "🦔"
            else -> "🐾"
        }
    }

    override fun getItemCount() = reports.size

    fun updateData(newReports: List<Report>) {
        reports = newReports
        notifyDataSetChanged()
    }
}

package com.umutsibara.patitakip.ui.admin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.umutsibara.patitakip.R
import com.umutsibara.patitakip.data.model.Report
import com.umutsibara.patitakip.databinding.ItemAdminReportBinding
import com.umutsibara.patitakip.util.Constants

class AdminReportAdapter(
    private var reports: List<Report>,
    private val onDeleteClick: (Report) -> Unit
) : RecyclerView.Adapter<AdminReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(val binding: ItemAdminReportBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemAdminReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]
        
        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(report)
        }
        
        holder.binding.apply {
            tvTitle.text = report.title ?: "Başlık yok"
            tvDescription.text = report.description ?: "Açıklama yok"
            
            // Category setup (Same as ReportAdapter)
            tvCategory.text = when (report.category) {
                "REPORT" -> "İhbar"
                "FEEDING" -> "Besleme"
                "ADOPTION" -> "Sahiplendirme"
                null -> "Genel"
                else -> report.category
            }
            tvUser.text = report.creatorName ?: "Anonim"
            
            if (!report.animalType.isNullOrEmpty()) {
                val emoji = getAnimalEmoji(report.animalType)
                cvAnimalType.text = "$emoji ${report.animalType}"
                cvAnimalType.visibility = View.VISIBLE
            } else {
                cvAnimalType.visibility = View.GONE
            }

            val categoryColor = when (report.category) {
                "REPORT" -> "#E53935" 
                "FEEDING" -> "#43A047" 
                "ADOPTION" -> "#1E88E5" 
                null -> "#9E9E9E" 
                else -> "#9E9E9E" 
            }
            cvCategory.setCardBackgroundColor(Color.parseColor(categoryColor))

            // Load photo
            val photoUrl = report.photoUrl
            if (!photoUrl.isNullOrEmpty()) {
                val baseUrl = Constants.BASE_URL.removeSuffix("api/")
                val fullPhotoUrl = baseUrl + photoUrl.removePrefix("/")
                
                Glide.with(root.context)
                    .load(fullPhotoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_add_photo)
                    .error(R.drawable.ic_add_photo)
                    .centerCrop()
                    .into(ivReportImage)
                
                ivReportImage.visibility = View.VISIBLE
            } else {
                Glide.with(root.context)
                    .load(R.drawable.ic_add_photo)
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

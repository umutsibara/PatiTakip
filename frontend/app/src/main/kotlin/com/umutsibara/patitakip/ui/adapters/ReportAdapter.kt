package com.umutsibara.patitakip.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.umutsibara.patitakip.R
import com.umutsibara.patitakip.databinding.ItemReportCardBinding
import com.umutsibara.patitakip.network.models.Report
import com.umutsibara.patitakip.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class ReportAdapter(
    private val reports: List<Report>,
    private val onReportClick: (Report) -> Unit,
    private val onLikeClick: (Report) -> Unit,
    private val onCommentClick: (Report) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReportViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(reports[position])
    }
    
    override fun getItemCount() = reports.size
    
    inner class ReportViewHolder(
        private val binding: ItemReportCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(report: Report) {
            binding.apply {
                // Kullanıcı bilgileri
                tvUsername.text = report.kullaniciAdi ?: "Anonim"
                tvTimestamp.text = formatDate(report.olusturmaTarihi)
                
                // Avatar
                if (!report.avatarUrl.isNullOrEmpty()) {
                    ivAvatar.load(Constants.BASE_URL.replace("/api/", "") + report.avatarUrl) {
                        placeholder(R.drawable.ic_launcher_foreground)
                        error(R.drawable.ic_launcher_foreground)
                    }
                }
                
                // İçerik
                tvTitle.text = report.baslik ?: "Başlıksız"
                tvDescription.text = report.aciklama
                tvCategory.text = getCategoryText(report.kategori)
                tvLocation.text = report.adres ?: "Konum bilgisi yok"
                
                // Fotoğraf
                if (!report.fotografUrl.isNullOrEmpty()) {
                    ivPhoto.load(Constants.BASE_URL.replace("/api/", "") + report.fotografUrl) {
                        placeholder(R.drawable.ic_launcher_background)
                        error(R.drawable.ic_launcher_background)
                    }
                } else {
                    ivPhoto.setImageResource(R.drawable.ic_launcher_background)
                }
                
                // İstatistikler
                tvLikeCount.text = report.begeniSayisi.toString()
                tvCommentCount.text = report.yorumSayisi.toString()
                tvShareCount.text = report.paylasimSayisi.toString()
                
                // Click listeners
                root.setOnClickListener { onReportClick(report) }
                btnLike.setOnClickListener { onLikeClick(report) }
                btnComment.setOnClickListener { onCommentClick(report) }
            }
        }
        
        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("tr", "TR"))
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: dateString
            } catch (e: Exception) {
                dateString
            }
        }
        
        private fun getCategoryText(category: String): String {
            return when (category) {
                "REPORT" -> "🚨 İhbar"
                "FEEDING" -> "🍖 Besleme"
                "ADOPTION" -> "🏠 Sahiplendirme"
                "SERVICE" -> "🛠️ Hizmet"
                "DONATION" -> "❤️ Bağış"
                else -> category
            }
        }
    }
}

package com.umutsibara.patitakip.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umutsibara.patitakip.databinding.ItemFeedingCardBinding
import com.umutsibara.patitakip.network.models.Feeding

class FeedingAdapter(
    private val feedings: List<Feeding>,
    private val onLikeClick: (Feeding) -> Unit,
    private val onCommentClick: (Feeding) -> Unit,
    private val onShareClick: (Feeding) -> Unit,
    private val onCardClick: (Feeding) -> Unit
) : RecyclerView.Adapter<FeedingAdapter.FeedingViewHolder>() {

    inner class FeedingViewHolder(private val binding: ItemFeedingCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(feeding: Feeding) {
            // User info
            binding.tvUsername.text = feeding.username ?: "Kullanıcı"
            binding.tvLocation.text = feeding.regionName ?: "Konum belirtilmemiş"
            
            // Animal type
            binding.tvAnimalType.text = when (feeding.animalType) {
                "Kedi" -> "Cat"
                "Köpek" -> "Dog"
                else -> feeding.animalType ?: "Animal"
            }
            
            // Description
            binding.tvDescription.text = feeding.description ?: "Besleme aktivitesi gerçekleştirildi."
            
            // Tags - show first tag if available
            if (!feeding.tags.isNullOrEmpty()) {
                binding.tvTag1.text = feeding.tags[0]
                binding.tvTag1.visibility = View.VISIBLE
                
                if (feeding.tags.size > 1) {
                    binding.tvTag2.text = feeding.tags[1]
                    binding.tvTag2.visibility = View.VISIBLE
                }
            } else {
                binding.tvTag1.visibility = View.GONE
                binding.tvTag2.visibility = View.GONE
            }
            
            // Interaction counts
            binding.tvLikeCount.text = feeding.likeCount.toString()
            binding.tvCommentCount.text = feeding.commentCount.toString()
            binding.tvShareCount.text = "0" // Share count not in model yet
            
            // Click listeners
            binding.llLikeButton.setOnClickListener { onLikeClick(feeding) }
            binding.llCommentButton.setOnClickListener { onCommentClick(feeding) }
            binding.llShareButton.setOnClickListener { onShareClick(feeding) }
            binding.root.setOnClickListener { onCardClick(feeding) }
            
            // TODO: Load images with Glide/Coil
            // Glide.with(binding.ivProfilePicture.context)
            //     .load(feeding.profileImage)
            //     .placeholder(R.drawable.ic_person_placeholder)
            //     .into(binding.ivProfilePicture)
            
            // Glide.with(binding.ivFeedingPhoto.context)
            //     .load(feeding.photoUrl)
            //     .placeholder(R.color.surface_variant)
            //     .into(binding.ivFeedingPhoto)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedingViewHolder {
        val binding = ItemFeedingCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FeedingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedingViewHolder, position: Int) {
        holder.bind(feedings[position])
    }

    override fun getItemCount() = feedings.size
}

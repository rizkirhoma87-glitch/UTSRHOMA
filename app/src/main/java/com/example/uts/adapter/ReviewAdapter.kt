package com.example.uts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uts.databinding.ItemReviewBinding
import com.example.uts.helpers.FormatHelper
import com.example.uts.response.ReviewsItem

class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private val listReviews = ArrayList<ReviewsItem>()

    fun setData(items: List<ReviewsItem?>?) {
        listReviews.clear()
        if (items != null) {
            for (item in items) {
                if (item != null) {
                    listReviews.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(listReviews[position])
    }

    override fun getItemCount(): Int = listReviews.size

    inner class ReviewViewHolder(
        private val binding: ItemReviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: ReviewsItem) {
            binding.tvReviewerName.text = review.reviewerName ?: "Anonymous"
            binding.tvReviewRating.text = review.rating?.toString() ?: "0"
            binding.tvReviewDate.text = FormatHelper.formatDate(review.date)
            binding.tvReviewComment.text = review.comment ?: "-"
        }
    }
}

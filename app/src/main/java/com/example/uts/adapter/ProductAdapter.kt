package com.example.uts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uts.R
import com.example.uts.databinding.ItemProductBinding
import com.example.uts.helpers.FormatHelper
import com.example.uts.response.ProductsItem

class ProductAdapter(
    private val onItemClick: (ProductsItem) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val listProducts = ArrayList<ProductsItem>()

    fun setData(items: List<ProductsItem?>?) {
        listProducts.clear()
        if (items != null) {
            for (item in items) {
                if (item != null) {
                    listProducts.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(listProducts[position])
    }

    override fun getItemCount(): Int = listProducts.size

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductsItem) {
            binding.tvTitle.text = product.title ?: "-"
            binding.tvCategory.text = product.category ?: "-"
            binding.tvPrice.text = FormatHelper.formatPrice(product.price)
            binding.tvRating.text = FormatHelper.formatRating(product.rating)

            Glide.with(itemView.context)
                .load(product.thumbnail)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_broken_image)
                .into(binding.ivThumbnail)

            binding.root.setOnClickListener {
                onItemClick(product)
            }
        }
    }
}

package com.example.uts.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.uts.R
import com.example.uts.adapter.ReviewAdapter
import com.example.uts.databinding.ActivityDetailProductBinding
import com.example.uts.helpers.FormatHelper
import com.example.uts.response.ProductsItem
import com.example.uts.retrofit.ApiConfig
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProductBinding
    private lateinit var reviewAdapter: ReviewAdapter

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
        const val EXTRA_PRODUCT = "extra_product"
        const val EXTRA_PRODUCT_JSON = "extra_product_json"
        private const val TAG = "DetailProductActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ikon status bar putih kontras di atas Navy Blue
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = true
        }

        // Tangani insets status bar untuk scrim Navy Dark dan navigasi bawah
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val statusBars = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBars = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())

            binding.statusBarScrimDetail.layoutParams.height = statusBars.top
            binding.statusBarScrimDetail.requestLayout()

            binding.scrollView.updatePadding(bottom = navBars.bottom)
            windowInsets
        }

        setupToolbar()
        setupReviewsRecyclerView()

        val productId = intent.getIntExtra(EXTRA_PRODUCT_ID, -1)
        val productJson = intent.getStringExtra(EXTRA_PRODUCT_JSON)

        val product: ProductsItem? = try {
            if (!productJson.isNullOrEmpty()) Gson().fromJson(productJson, ProductsItem::class.java) else null
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengambil data produk dari intent: ${e.message}")
            null
        }

        if (product != null) {
            populateProductDetails(product)
        }

        if (productId != -1) {
            loadProductDetail(productId)
        } else if (product == null) {
            Toast.makeText(this, "Data produk tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.title = getString(R.string.title_detail)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupReviewsRecyclerView() {
        reviewAdapter = ReviewAdapter()
        binding.rvReviews.apply {
            layoutManager = LinearLayoutManager(this@DetailProductActivity)
            adapter = reviewAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun loadProductDetail(id: Int) {
        showLoading(true)
        val client = ApiConfig.getApiService().getProductDetail(id)
        client.enqueue(object : Callback<ProductsItem> {
            override fun onResponse(
                call: Call<ProductsItem>,
                response: Response<ProductsItem>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val detail = response.body()
                    if (detail != null && !isFinishing && !isDestroyed) {
                        populateProductDetails(detail)
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ProductsItem>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun populateProductDetails(product: ProductsItem) {
        if (isFinishing || isDestroyed) return

        binding.toolbar.title = product.title ?: getString(R.string.title_detail)

        val imageUrl = product.thumbnail ?: product.images?.firstOrNull()
        try {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_broken_image)
                .into(binding.ivDetailThumbnail)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading image: ${e.message}")
        }

        binding.tvDetailTitle.text = product.title ?: "-"
        binding.tvDetailCategory.text = product.category ?: "-"
        binding.tvDetailAvailability.text = product.availabilityStatus ?: "Tersedia"
        binding.tvDetailPrice.text = FormatHelper.formatPrice(product.price)

        val discount = when (val d = product.discountPercentage) {
            is Number -> d.toDouble()
            is String -> d.toString().toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
        if (discount > 0) {
            binding.tvDetailDiscount.visibility = View.VISIBLE
            binding.tvDetailDiscount.text = "$discount% OFF"
        } else {
            binding.tvDetailDiscount.visibility = View.GONE
        }

        binding.tvDetailRating.text = if (product.rating != null) {
            "${FormatHelper.formatRating(product.rating)} / 5.0"
        } else {
            "0.0 / 5.0"
        }

        val tagsList = product.tags?.filterNotNull()
        if (!tagsList.isNullOrEmpty()) {
            binding.tvDetailTags.visibility = View.VISIBLE
            binding.tvDetailTags.text = "Tags: ${tagsList.joinToString(", ") { "#$it" }}"
        } else {
            binding.tvDetailTags.visibility = View.GONE
        }

        binding.tvDetailDescription.text = product.description ?: "-"

        // Spesifikasi lengkap produk sesuai soal UTS
        binding.tvSpecId.text = product.id?.toString() ?: "-"
        binding.tvSpecBrand.text = product.brand ?: "-"
        binding.tvSpecSku.text = product.sku ?: "-"
        binding.tvSpecStock.text = "${product.stock ?: 0} unit"
        binding.tvSpecWeight.text = "${product.weight ?: 0} g"
        binding.tvSpecDimensions.text = FormatHelper.formatDimensions(
            product.dimensions?.width,
            product.dimensions?.height,
            product.dimensions?.depth
        )
        binding.tvSpecWarranty.text = product.warrantyInformation ?: "-"
        binding.tvSpecShipping.text = product.shippingInformation ?: "-"
        binding.tvSpecReturn.text = product.returnPolicy ?: "-"
        binding.tvSpecMinOrder.text = "${product.minimumOrderQuantity ?: 1} pcs"

        // Reviews
        val reviews = product.reviews
        if (!reviews.isNullOrEmpty()) {
            binding.tvNoReviews.visibility = View.GONE
            binding.rvReviews.visibility = View.VISIBLE
            reviewAdapter.setData(reviews)
        } else {
            binding.tvNoReviews.visibility = View.VISIBLE
            binding.rvReviews.visibility = View.GONE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

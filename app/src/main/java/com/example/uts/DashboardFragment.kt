package com.example.uts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts.adapter.ProductAdapter
import com.example.uts.databinding.FragmentDashboardBinding
import com.example.uts.ui.DetailProductActivity

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        setupRecyclerView()
        setupSwipeRefresh()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            val intent = Intent(requireContext(), DetailProductActivity::class.java).apply {
                putExtra(DetailProductActivity.EXTRA_PRODUCT_ID, product.id ?: -1)
                try {
                    putExtra(
                        DetailProductActivity.EXTRA_PRODUCT_JSON,
                        com.google.gson.Gson().toJson(product)
                    )
                } catch (e: Exception) {
                    // fallback to serializable
                }
            }
            startActivity(intent)
        }

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(
            R.color.navy_primary,
            R.color.navy_dark
        )
        binding.swipeRefresh.setOnRefreshListener {
            dashboardViewModel.findProducts(25)
        }
    }

    private fun setupListeners() {
        binding.btnRetry.setOnClickListener {
            dashboardViewModel.findProducts(25)
        }
    }

    private fun observeViewModel() {
        dashboardViewModel.listProducts.observe(viewLifecycleOwner) { products ->
            productAdapter.setData(products)
            if (products.isNullOrEmpty()) {
                binding.layoutError.visibility = View.VISIBLE
                binding.tvErrorMessage.text = "Tidak ada produk yang ditemukan."
            } else {
                binding.layoutError.visibility = View.GONE
                binding.rvProducts.post {
                    (binding.rvProducts.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(0, 0)
                }
            }
        }

        dashboardViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        dashboardViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.layoutError.visibility = View.VISIBLE
                binding.tvErrorMessage.text = error
            } else {
                binding.layoutError.visibility = View.GONE
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (binding.swipeRefresh.isRefreshing) {
            if (!isLoading) binding.swipeRefresh.isRefreshing = false
        } else {
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

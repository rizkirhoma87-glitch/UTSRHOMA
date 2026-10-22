package com.example.uts

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.uts.response.Response as ProductResponse
import com.example.uts.response.ProductsItem
import com.example.uts.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardViewModel : ViewModel() {

    private val _listProducts = MutableLiveData<List<ProductsItem>>()
    val listProducts: LiveData<List<ProductsItem>> = _listProducts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    companion object {
        private const val TAG = "DashboardViewModel"
        private const val DEFAULT_LIMIT = 25
    }

    init {
        findProducts(DEFAULT_LIMIT)
    }

    fun findProducts(limit: Int = DEFAULT_LIMIT) {
        _isLoading.value = true
        _errorMessage.value = null
        val client = ApiConfig.getApiService().getProducts(limit)
        client.enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val products = responseBody.products?.filterNotNull() ?: emptyList()
                        _listProducts.value = products
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    _errorMessage.value = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                _errorMessage.value = t.message ?: "Terjadi kesalahan saat memuat data"
            }
        })
    }
}
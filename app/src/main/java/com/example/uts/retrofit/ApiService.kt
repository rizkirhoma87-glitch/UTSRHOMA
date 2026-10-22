package com.example.uts.retrofit

import com.example.uts.response.Response as ProductResponse
import com.example.uts.response.ProductsItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("products")
    fun getProducts(
        @Query("limit") limit: Int = 25
    ): Call<ProductResponse>

    @GET("products/{id}")
    fun getProductDetail(
        @Path("id") id: Int
    ): Call<ProductsItem>
}

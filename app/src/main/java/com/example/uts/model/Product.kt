package com.example.uts.model

import java.io.Serializable

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val stock: Int,
    val availabilityStatus: String,
    val tags: List<String>,
    val dimensions: String,
    val rating: Double,
    val thumbnail: String,
    val images: List<String>
) : Serializable

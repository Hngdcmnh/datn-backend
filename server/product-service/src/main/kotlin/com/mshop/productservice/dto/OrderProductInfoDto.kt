package com.mshop.productservice.dto

data class OrderProductInfoDto(
    val productId: String? = null,
    val supplierId: String,
    val sku: String? = null,
    val name: String,
    val price: Float,
    val weight: Int,
    val height: Int,
    val length: Int,
    val width: Int,
    val brand: String,
    val images: List<String>? = null,
)

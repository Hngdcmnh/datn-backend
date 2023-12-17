package com.sudoo.productservice.dto

import java.time.LocalDateTime

data class ProductInfoDto(
    val productId: String,
    val supplierId: String,
    val sku: String,
    val name: String,
    val price: Float,
    val listedPrice: Float,
    val amount: Int,
    val soldAmount: Int,
    val color:String,
    val size:String,
    val discount: Int,
    val startDateDiscount: LocalDateTime? = null,
    val endDateDiscount: LocalDateTime? = null,
    val brand: String,
    val rate: Float,
    val saleable: Boolean,
    val images: List<String>? = null,
)

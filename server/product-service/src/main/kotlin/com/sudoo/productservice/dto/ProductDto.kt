package com.sudoo.productservice.dto

import java.time.LocalDateTime

data class ProductDto(
    val productId: String? = null,
    val supplierId: String,
    val sku: String? = null,
    val name: String,
    val description: String,
    val brand: String,
    val price: Float,
    val listedPrice: Float,
    val amount: Int,
    val color:String,
    val size:String,
    val soldAmount: Int,
    val rate: Float,
    val discount: Int,
    val startDateDiscount: LocalDateTime? = null,
    val endDateDiscount: LocalDateTime? = null,
    val saleable: Boolean,
    val weight: Int,
    val height: Int,
    val length: Int,
    val width: Int,
    val extras: ProductExtrasDto,
    var images: List<ImageDto>? = null,
    var supplier: SupplierInfoDto? = null,
    var categories: List<CategoryInfoDto>? = null,
)
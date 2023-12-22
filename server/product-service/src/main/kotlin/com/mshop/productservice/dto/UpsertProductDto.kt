package com.mshop.productservice.dto

import java.time.LocalDateTime

data class UpsertProductDto(
    val productId: String? = null,
    val sku: String? = null,
    val name: String? = null,
    val description: String? = null,
    val brand: String? = null,
    val price: Float? = null,
    val listedPrice: Float? = null,
    val amount: Int? = null,
    val color:String?  = null,
    val size:String? = null,
    val soldAmount: Int? = null,
    val discount: Int? = null,
    val startDateDiscount: LocalDateTime? = null,
    val endDateDiscount: LocalDateTime? = null,
    val saleable: Boolean? = null,
    val weight: Int? = null,
    val height: Int? = null,
    val length: Int? = null,
    val width: Int? = null,
    var extras: UpsertProductExtrasDto? = null,
    val categoryIds: List<String>? = null,
    val images: List<UpsertImageDto>? = null,
)
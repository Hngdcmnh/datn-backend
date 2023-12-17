package com.sudoo.cartservice.controller.dto

import java.time.LocalDateTime

data class ProductInfoDto(
    val productId: String = "",
    val sku: String = "",
    val name: String = "",
    val price: Float = 0.0f,
    val listedPrice: Float = 0.0f,
    val amount: Int = 0,
    val soldAmount:Int =0,
    val color:String = "",
    val size:String = "",
    val discount: Int = 0,
    val startDateDiscount: LocalDateTime? = null,
    val endDateDiscount: LocalDateTime? = null,
    val brand: String = "",
    val rate: Float = 0.0f,
    val saleable: Boolean = true,
    val images: List<String>? = null,
)

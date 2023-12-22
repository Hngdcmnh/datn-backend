package com.mshop.cartservice.controller.dto.order


data class OrderProductInfoDto(
    val productId: String? = "",
    val supplierId: String = "",
    val sku: String? = null,
    val name: String = "",
    val price: Float = 0.0f,
    val weight: Int = 0,
    val height: Int = 0,
    val length: Int = 0,
    val width: Int = 0,
    val brand: String = "",
    val images: List<String>? = listOf(),
)


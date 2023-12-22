package com.mshop.cartservice.controller.dto

import java.io.Serializable


data class ProductDto(
        val productId: String,
        val name: String,
        val description: String,
        val sku: String,
        val images: List<ImageDto>,
)

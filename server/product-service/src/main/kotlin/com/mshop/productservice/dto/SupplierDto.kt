package com.mshop.productservice.dto

import java.time.LocalDateTime

data class SupplierDto(
    val supplierId: String,
    val ghnShopId: Int,
    val name: String,
    val avatar: String,
    val contactUrl: String,
    val totalProducts: Int = 0,
    val rate: Float = 0.0f,
    val createAt: LocalDateTime,
    val address: AddressDto,
)
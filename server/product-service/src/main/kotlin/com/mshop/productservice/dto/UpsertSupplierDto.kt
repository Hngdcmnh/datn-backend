package com.mshop.productservice.dto

import java.time.LocalDateTime

data class UpsertSupplierDto(
    val supplierId: String? = null,
    val ghnShopId: Int? = null,
    val phoneNumber: String?,
    val name: String,
    val avatar: String,
    val contactUrl: String?,
    val totalProducts: Int? = null,
    val rate: Float? = null,
    val createAt: LocalDateTime?,
    val address: AddressDto? = null,
)

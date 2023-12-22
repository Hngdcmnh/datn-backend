package com.mshop.productservice.dto

data class SupplierInfoDto(
    val supplierId: String? = null,
    val ghnShopId: Int? = null,
//    val userId: String,
    val name: String,
    val avatar: String,
    val address: AddressDto,
    val contactUrl: String,
    val rate: Float
)
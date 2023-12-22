package com.mshop.productservice.dto

data class AddressDto(
    val addressId: String? = null,
    val provinceID: Int,
    val districtID: Int,
    val wardCode: String,
    val provinceName: String,
    val districtName: String,
    val wardName: String,
    val address: String,
    val location: Location? = null,
    val fullAddress: String? = null,
)

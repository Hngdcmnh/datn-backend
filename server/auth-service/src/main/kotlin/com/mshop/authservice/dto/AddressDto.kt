package com.mshop.authservice.dto

data class AddressDto(
    private var addressId: String? = "",

    val provinceID: Int = 0,

    val districtID: Int = 0,

    val wardCode: Int = 0,

    val provinceName: String = "",

    val districtName: String = "",

    val wardName: String = "",

    val address: String = "",

    val location: com.mshop.authservice.dto.LocationDto = com.mshop.authservice.dto.LocationDto(),

    val fullAddress: String = "",
)
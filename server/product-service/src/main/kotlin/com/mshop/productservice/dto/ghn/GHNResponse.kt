package com.mshop.productservice.dto.ghn

data class GHNResponse<T> (
    val code: Int = 0,
    val message: String = "",
    val data: T? = null
)
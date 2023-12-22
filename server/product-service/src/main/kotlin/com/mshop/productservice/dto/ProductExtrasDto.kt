package com.mshop.productservice.dto

data class ProductExtrasDto(
    val enable3DViewer: Boolean = false,
    val enableArViewer: Boolean = false,
    val source: String? = null,
)

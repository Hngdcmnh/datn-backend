package com.mshop.productservice.dto

data class CategoryDto(
    val categoryId: String? = null,
    val name: String,
    val image: String,
    val countProduct: Int? = null,
)
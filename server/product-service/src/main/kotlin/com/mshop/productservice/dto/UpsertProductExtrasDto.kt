package com.mshop.productservice.dto

data class UpsertProductExtrasDto(
    val enable3DViewer: Boolean? = null,
    val enableArViewer: Boolean? = null,
    val source: String? = null,
)

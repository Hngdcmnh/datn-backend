package com.mshop.productservice.dto

import com.mshop.base.Pagination

data class ProductPagination<out T> (
    val products: List<T>,
    val pagination: com.mshop.base.Pagination
)
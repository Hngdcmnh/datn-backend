package com.mshop.productservice.dto

import com.mshop.base.Pagination

data class ReviewPagination<out T> (
    val reviews: List<T>,
    val pagination: com.mshop.base.Pagination
)
package com.mshop.productservice.dto

import com.mshop.base.Pagination

data class CommentPagination<out T> (
    val comments: List<T>,
    val pagination: com.mshop.base.Pagination
)
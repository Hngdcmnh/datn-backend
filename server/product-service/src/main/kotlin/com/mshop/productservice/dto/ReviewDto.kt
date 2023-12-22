package com.mshop.productservice.dto

import java.time.LocalDateTime

data class ReviewDto(
    val userProductId: String? = null,
    val rate: Float,
    val isReviewed: Boolean,
    val comment: String,
    val updatedAt: LocalDateTime?,
    val createdAt: LocalDateTime?,
    var userInfo: UserInfoDto,
    val productInfo: ProductInfoDto,
)
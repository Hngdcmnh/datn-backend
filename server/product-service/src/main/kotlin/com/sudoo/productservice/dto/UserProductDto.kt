package com.sudoo.productservice.dto

import java.time.LocalDateTime

data class UserProductDto(
    val userProductId: String? = null,
    val productId: String,
    val rate: Float,
    val matchRate:Float,
    val avgRate:Float,
    val isReviewed: Boolean,
    val comment: String,
    val updatedAt: LocalDateTime?,
    val createdAt: LocalDateTime?,
    var userInfo: UserInfoDto? = null,
    var images: List<String>? = null,
)
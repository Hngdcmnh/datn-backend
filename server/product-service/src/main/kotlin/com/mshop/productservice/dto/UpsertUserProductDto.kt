package com.mshop.productservice.dto

import java.time.LocalDateTime

data class UpsertUserProductDto(
    val userProductId: String? = null,
    val productId: String? = null,
    val userId: String? = null,
    val rate: Float? = null,
    val matchRate: Float? = null,
    val avgRate: Float? = null,
    val comment: String? = null,
    var createdAt: LocalDateTime? = null,
    var images: List<String>? = null,
) {
    companion object {
        fun create(userId: String, productId: String): UpsertUserProductDto {
            return UpsertUserProductDto(
                productId = productId,
                userId = userId
            )
        }
    }
}
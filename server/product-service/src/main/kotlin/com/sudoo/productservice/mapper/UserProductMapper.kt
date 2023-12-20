package com.sudoo.productservice.mapper

import com.sudoo.domain.exception.BadRequestException
import com.sudoo.domain.utils.IdentifyCreator
import com.sudoo.productservice.dto.*
import com.sudoo.productservice.model.ProductInfo
import com.sudoo.productservice.model.UserProduct
import java.time.LocalDateTime

fun UserProductDto.toUserProduct(userId: String): UserProduct {
    return UserProduct(
        userProductId = IdentifyCreator.createOrElse(userProductId),
        productId = productId,
        userId = userId,
        rate = rate,
        matchRate = matchRate,
        avgRate = avgRate,
        isReviewed = isReviewed,
        comment = comment,
    ).also {
        it.isNewUserProduct = userProductId.isNullOrEmpty()
    }
}

fun UserProduct.toUserProductDto(userInfo: UserInfoDto? = null): UserProductDto {
    return UserProductDto(
        userProductId = userProductId,
        productId = productId,
        rate = rate,
        matchRate = matchRate,
        avgRate = avgRate,
        isReviewed = isReviewed,
        comment = comment,
        updatedAt = updatedAt,
        createdAt = createdAt,
        userInfo = userInfo,
        images = images?.map { it.url }
    )
}

fun UpsertUserProductDto.toUserProduct(userId: String, isReviewed: Boolean): UserProduct {
    return UserProduct(
        userProductId = IdentifyCreator.createOrElse(userProductId),
        productId = productId ?: throw BadRequestException("Required product id"),
        userId = this.userId ?: userId,
        rate = rate ?: -1f,
        matchRate = matchRate ?: 0f,
        avgRate = avgRate ?: 0f,
        isReviewed = isReviewed,
        updatedAt = LocalDateTime.now(),
        createdAt = createdAt ?: LocalDateTime.now(),
        comment = comment.orEmpty(),
    ).also {
        it.isNewUserProduct = userProductId.isNullOrEmpty()
    }
}

fun UpsertUserProductDto.combine(userProduct: UserProduct): UserProduct {
    return UserProduct(
        userProductId = userProductId ?: userProduct.userProductId,
        productId = productId ?: userProduct.productId,
        userId = userId ?: userProduct.userId,
        rate = if (rate == null) userProduct.rate else (userProduct.rate + rate)/2 ,
        matchRate = userProduct.matchRate,
        avgRate = userProduct.avgRate,
        isReviewed = true,
        updatedAt = LocalDateTime.now(),
        createdAt = createdAt ?: userProduct.createdAt,
        comment = comment ?: userProduct.comment,
    )
}


fun UserProduct.toReviewDto(productInfo: ProductInfoDto, userInfo: UserInfoDto): ReviewDto {
    return ReviewDto(
        userProductId = userProductId,
        rate = rate,
        isReviewed = isReviewed,
        comment = comment,
        updatedAt = updatedAt,
        createdAt = createdAt,
        userInfo = userInfo,
        productInfo = productInfo
    )
}
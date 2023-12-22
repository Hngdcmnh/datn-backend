package com.mshop.productservice.mapper

import com.mshop.common.Constants
import com.mshop.utils.IdentifyCreator
import com.mshop.productservice.dto.PromotionDto
import com.mshop.productservice.model.Promotion

fun PromotionDto.toPromotion(): Promotion {
    return Promotion(
        promotionId = IdentifyCreator.createOrElse(promotionId),
        supplierId = supplierId ?: com.mshop.common.Constants.ADMIN_ID,
        name = name,
        value = value,
        enable = enable,
        image = image,
        totalAmount = totalAmount
    ).also {
        it.isNewPromotion = promotionId.isNullOrEmpty()
    }
}

fun Promotion.toPromotionDto(): PromotionDto {
    return PromotionDto(
        promotionId = promotionId,
        supplierId = supplierId,
        name = name,
        value = value,
        enable = enable,
        image = image,
        totalAmount = totalAmount
    )
}
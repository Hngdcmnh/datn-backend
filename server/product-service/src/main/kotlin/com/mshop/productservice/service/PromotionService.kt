package com.mshop.productservice.service

import com.mshop.productservice.dto.PatchAmountPromotionDto
import com.mshop.productservice.dto.PromotionDto

interface PromotionService {
    suspend fun getAllPromotion(enable: Boolean?): List<PromotionDto>

    suspend fun getPromotion(promotionId: String): PromotionDto

    suspend fun upsertPromotion(promotion: PromotionDto): PromotionDto

    suspend fun deletePromotion(promotionId: String)

    suspend fun patchPromotion(patchPromotion: PatchAmountPromotionDto): PatchAmountPromotionDto
}
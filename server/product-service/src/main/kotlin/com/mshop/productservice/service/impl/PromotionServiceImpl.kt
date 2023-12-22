package com.mshop.productservice.service.impl

import com.mshop.exception.BadRequestException
import com.mshop.exception.NotFoundException
import com.mshop.productservice.dto.PatchAmountPromotionDto
import com.mshop.productservice.dto.PromotionDto
import com.mshop.productservice.mapper.toPromotion
import com.mshop.productservice.mapper.toPromotionDto
import com.mshop.productservice.repository.PromotionRepository
import com.mshop.productservice.service.PromotionService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import kotlin.math.abs

@Service
class PromotionServiceImpl(
    private val promotionRepository: PromotionRepository
) : PromotionService {
    override suspend fun getAllPromotion(enable: Boolean?): List<PromotionDto> {
        return enable?.let {
            promotionRepository.getAllByEnableAndTotalAmountGreaterThan(it, 0).map { promotion -> promotion.toPromotionDto() }.toList()
        } ?: promotionRepository.findAll().map { it.toPromotionDto() }.toList()
    }

    override suspend fun getPromotion(promotionId: String): PromotionDto {
        val promotion =
            promotionRepository.findById(promotionId) ?: throw com.mshop.exception.NotFoundException("Not found promotion $promotionId")
        return promotion.toPromotionDto()
    }

    override suspend fun upsertPromotion(promotion: PromotionDto): PromotionDto {
        val savedPromotion = promotionRepository.save(promotion.toPromotion())
        return savedPromotion.toPromotionDto()
    }

    override suspend fun deletePromotion(promotionId: String) {
        val promotion =
            promotionRepository.findById(promotionId) ?: throw com.mshop.exception.NotFoundException("Not found promotion $promotionId")
        promotionRepository.deleteById(promotionId)
    }

    override suspend fun patchPromotion(patchPromotion: PatchAmountPromotionDto): PatchAmountPromotionDto {
        val promotion = promotionRepository.findById(patchPromotion.promotionId)
            ?: throw com.mshop.exception.NotFoundException("Not found promotion ${patchPromotion.promotionId}")
        if (patchPromotion.amount < 0 && abs(patchPromotion.amount) > promotion.totalAmount) {
            throw com.mshop.exception.BadRequestException("Not enough promotion")
        }
        promotion.totalAmount += patchPromotion.amount
        promotionRepository.save(promotion)
        patchPromotion.amount = promotion.totalAmount
        return patchPromotion
    }
}
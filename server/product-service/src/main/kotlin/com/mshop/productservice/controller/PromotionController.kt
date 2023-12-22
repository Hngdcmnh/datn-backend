package com.mshop.productservice.controller

import com.mshop.base.BaseController
import com.mshop.base.BaseResponse
import com.mshop.productservice.dto.PatchAmountPromotionDto
import com.mshop.productservice.dto.PromotionDto
import com.mshop.productservice.service.PromotionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/promotions")
class PromotionController (
    private val promotionService: PromotionService
) : com.mshop.base.BaseController() {

    @GetMapping
    suspend fun getAllPromotion(
        @RequestParam("enable", required =  false) enable: Boolean?,
    ) : ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        promotionService.getAllPromotion(enable)
    }

    @GetMapping("/{promotionId}")
    suspend fun getPromotion(
        @PathVariable("promotionId") promotionId: String
    ) : ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        promotionService.getPromotion(promotionId)
    }

    @PostMapping
    suspend fun upsertPromotion(
        @RequestBody promotionDto: PromotionDto
    ) : ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        promotionService.upsertPromotion(promotionDto)
    }

    @DeleteMapping("/{promotionId}")
    suspend fun deletePromotion(
        @PathVariable("promotionId") promotionId: String
    ) : ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        promotionService.deletePromotion(promotionId)
    }

    @PutMapping("/internal/amount")
    suspend fun patchPromotion(
        @RequestBody promotion: PatchAmountPromotionDto
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        promotionService.patchPromotion(promotion)
    }
}
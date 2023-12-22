package com.mshop.productservice.controller

import com.mshop.base.BaseController
import com.mshop.base.BaseResponse
import com.mshop.base.OffsetRequest
import com.mshop.common.Constants
import com.mshop.productservice.dto.UpsertListUserProductDto
import com.mshop.productservice.dto.UpsertUserProductDto
import com.mshop.productservice.service.UserProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/discovery")
class CommentController(
    private val userProductService: UserProductService,
) : com.mshop.base.BaseController() {

    // only order service call this api whenever user payment success for product
    @PostMapping("/internal/user-product")
    suspend fun postUserProduct(
        @RequestHeader(com.mshop.common.Constants.HEADER_USER_ID) userId: String,
        @RequestBody upsertUserProductDto: UpsertUserProductDto
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        userProductService.postUserProduct(userId, upsertUserProductDto)
    }

    //Tạo UserProduct với tất cả product cho user mới
    @PostMapping("/internal/user-product/all")
    suspend fun postAllUserProduct(
        @RequestHeader(com.mshop.common.Constants.HEADER_USER_ID) userId: String,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        userProductService.postAllUserProductForNewUser(userId)
    }

    // only order service call this api whenever user payment success for product
    @PostMapping("/products/internal/user-product/list")
    suspend fun postListUserProduct(
        @RequestBody upsertListUserProductDto: UpsertListUserProductDto
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        userProductService.postListUserProduct(upsertListUserProductDto)
    }

    // user call this api whenever review product
    @PostMapping("/reviews")
    suspend fun upsertReviewed(
        @RequestHeader(com.mshop.common.Constants.HEADER_USER_ID) userId: String,
        @RequestBody upsertUserProductDto: UpsertUserProductDto
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        userProductService.upsertReview(userId, upsertUserProductDto)
    }

    @DeleteMapping("comments/{commentId}")
    suspend fun deleteComment(
        @PathVariable("commentId") commentId: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        userProductService.deleteComment(commentId)
    }

    @DeleteMapping("reviews/{reviewId}")
    suspend fun deleteReviewed(
        @PathVariable("reviewId") reviewId: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        userProductService.deleteComment(reviewId)
    }

    @GetMapping("/comments")
    suspend fun getComments(
        @RequestParam("productId", required = false) productId: String?,
        @RequestParam("isReviewed", required = false) isReViewed: Boolean?,
        @RequestParam("offset", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_OFFSET) offset: Int,
        @RequestParam("limit", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_LIMIT) limit: Int,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        val offsetRequest = com.mshop.base.OffsetRequest(offset, limit)
        productId?.let {
            isReViewed?.let {
                userProductService.getCommentsByProductIdAndReviewed(productId, it,  offsetRequest)
            } ?: userProductService.getCommentsByProductId(productId, offsetRequest)
        } ?: userProductService.getComments(offsetRequest)
    }

    @GetMapping("/reviews")
    suspend fun getReviews(
        @RequestHeader(com.mshop.common.Constants.HEADER_USER_ID) userId: String,
        @RequestParam("isReviewed", required = false) isReViewed: Boolean?,
        @RequestParam("offset", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_OFFSET) offset: Int,
        @RequestParam("limit", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_LIMIT) limit: Int,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        val offsetRequest = com.mshop.base.OffsetRequest(offset, limit)
        isReViewed?.let {
            userProductService.getReviewsByUserIdAndReviewed(userId, it,  offsetRequest)
        } ?: userProductService.getReviewsByUserId(userId, offsetRequest)
    }
}
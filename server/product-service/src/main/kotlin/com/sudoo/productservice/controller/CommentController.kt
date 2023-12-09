package com.sudoo.productservice.controller

import com.sudoo.domain.base.BaseController
import com.sudoo.domain.base.BaseResponse
import com.sudoo.domain.base.OffsetRequest
import com.sudoo.domain.common.Constants
import com.sudoo.productservice.dto.UpsertListUserProductDto
import com.sudoo.productservice.dto.UpsertUserProductDto
import com.sudoo.productservice.service.UserProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/discovery")
class CommentController(
    private val userProductService: UserProductService,
) : BaseController() {

    // only order service call this api whenever user payment success for product
    @PostMapping("/internal/user-product")
    suspend fun postUserProduct(
        @RequestHeader(Constants.HEADER_USER_ID) userId: String,
        @RequestBody upsertUserProductDto: UpsertUserProductDto
    ): ResponseEntity<BaseResponse<*>> = handle {
        userProductService.postUserProduct(userId, upsertUserProductDto)
    }

    // only order service call this api whenever user payment success for product
    @PostMapping("/products/internal/user-product/list")
    suspend fun postListUserProduct(
        @RequestBody upsertListUserProductDto: UpsertListUserProductDto
    ): ResponseEntity<BaseResponse<*>> = handle {
        userProductService.postListUserProduct(upsertListUserProductDto)
    }

    // user call this api whenever review product
    @PostMapping("/reviews")
    suspend fun upsertReviewed(
        @RequestHeader(Constants.HEADER_USER_ID) userId: String,
        @RequestBody upsertUserProductDto: UpsertUserProductDto
    ): ResponseEntity<BaseResponse<*>> = handle {
        userProductService.upsertReview(userId, upsertUserProductDto)
    }

    @DeleteMapping("comments/{commentId}")
    suspend fun deleteComment(
        @PathVariable("commentId") commentId: String
    ): ResponseEntity<BaseResponse<*>> = handle {
        userProductService.deleteComment(commentId)
    }

    @DeleteMapping("reviews/{reviewId}")
    suspend fun deleteReviewed(
        @PathVariable("reviewId") reviewId: String
    ): ResponseEntity<BaseResponse<*>> = handle {
        userProductService.deleteComment(reviewId)
    }

    @GetMapping("/comments")
    suspend fun getComments(
        @RequestParam("productId", required = false) productId: String?,
        @RequestParam("isReviewed", required = false) isReViewed: Boolean?,
        @RequestParam("offset", required = false, defaultValue = Constants.DEFAULT_OFFSET) offset: Int,
        @RequestParam("limit", required = false, defaultValue = Constants.DEFAULT_LIMIT) limit: Int,
    ): ResponseEntity<BaseResponse<*>> = handle {
        val offsetRequest = OffsetRequest(offset, limit)
        productId?.let {
            isReViewed?.let {
                userProductService.getCommentsByProductIdAndReviewed(productId, it,  offsetRequest)
            } ?: userProductService.getCommentsByProductId(productId, offsetRequest)
        } ?: userProductService.getComments(offsetRequest)
    }

    @GetMapping("/reviews")
    suspend fun getReviews(
        @RequestHeader(Constants.HEADER_USER_ID) userId: String,
        @RequestParam("isReviewed", required = false) isReViewed: Boolean?,
        @RequestParam("offset", required = false, defaultValue = Constants.DEFAULT_OFFSET) offset: Int,
        @RequestParam("limit", required = false, defaultValue = Constants.DEFAULT_LIMIT) limit: Int,
    ): ResponseEntity<BaseResponse<*>> = handle {
        val offsetRequest = OffsetRequest(offset, limit)
        isReViewed?.let {
            userProductService.getReviewsByUserIdAndReviewed(userId, it,  offsetRequest)
        } ?: userProductService.getReviewsByUserId(userId, offsetRequest)
    }
}
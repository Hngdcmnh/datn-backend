package com.sudoo.productservice.service

import com.sudoo.domain.base.OffsetRequest
import com.sudoo.productservice.dto.*

interface UserProductService {

    suspend fun postUserProduct(userId: String, upsertUserProductDto: UpsertUserProductDto): UpsertUserProductDto

    //Tạo UserProduct với tất cả product cho user mới
    suspend fun postAllUserProductForNewUser(userId: String): Boolean

    suspend fun postListUserProduct(upsertListUserProductDto: UpsertListUserProductDto): List<String>

    //Tạo UserProduct với tất cả user cho product mới
    suspend fun postAllUserProductForNewProduct(productId: String): Boolean


    //Update UserProduct khi user rate sản phẩm
    suspend fun upsertReview(userId: String, upsertUserProductDto: UpsertUserProductDto): UserProductDto

    suspend fun deleteComment(userProductId: String): String

    suspend fun getComments(offsetRequest: OffsetRequest): CommentPagination<UserProductDto>

    suspend fun getCommentsByProductId(
        productId: String,
        offsetRequest: OffsetRequest
    ): CommentPagination<UserProductDto>

    suspend fun getReviewsByUserId(
        userId: String,
        offsetRequest: OffsetRequest
    ): ReviewPagination<ReviewDto>

    suspend fun getCommentsByProductIdAndReviewed(
        productId: String,
        isReviewed: Boolean,
        offsetRequest: OffsetRequest
    ): CommentPagination<UserProductDto>

    suspend fun getReviewsByUserIdAndReviewed(
        userId: String,
        isReviewed: Boolean,
        offsetRequest: OffsetRequest
    ): ReviewPagination<ReviewDto>
}
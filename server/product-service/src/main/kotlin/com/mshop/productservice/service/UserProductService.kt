package com.mshop.productservice.service

import com.mshop.base.OffsetRequest
import com.mshop.productservice.dto.*

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

    suspend fun getComments(offsetRequest: com.mshop.base.OffsetRequest): CommentPagination<UserProductDto>

    suspend fun getCommentsByProductId(
        productId: String,
        offsetRequest: com.mshop.base.OffsetRequest
    ): CommentPagination<UserProductDto>

    suspend fun getReviewsByUserId(
        userId: String,
        offsetRequest: com.mshop.base.OffsetRequest
    ): ReviewPagination<ReviewDto>

    suspend fun getCommentsByProductIdAndReviewed(
        productId: String,
        isReviewed: Boolean,
        offsetRequest: com.mshop.base.OffsetRequest
    ): CommentPagination<UserProductDto>

    suspend fun getReviewsByUserIdAndReviewed(
        userId: String,
        isReviewed: Boolean,
        offsetRequest: com.mshop.base.OffsetRequest
    ): ReviewPagination<ReviewDto>
}
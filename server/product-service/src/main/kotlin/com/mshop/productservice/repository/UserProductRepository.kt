package com.mshop.productservice.repository

import com.mshop.productservice.model.UserProduct
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserProductRepository : CoroutineCrudRepository<UserProduct, String> {



    suspend fun getUserProductsByUserId(userId:String):Flow<UserProduct>
    suspend fun getUserProductsByUserIdAndProductId(userId: String, productId: String): Flow<UserProduct>

    @Query(
        """
        SELECT * FROM users_products
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getAllWithOffset(
        @Param("_offset") offset: Int = 0,
        @Param("_limit") limit: Int = 0
    ): Flow<UserProduct>

    @Query(
        """
        SELECT * FROM users_products
        WHERE users_products.product_id = :productId
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getAllByProductIdWithOffset(
        @Param("productId") productId: String,
        @Param("_offset") offset: Int = 0,
        @Param("_limit") limit: Int = 0
    ): Flow<UserProduct>

    @Query(
        """
        SELECT * FROM users_products
        WHERE users_products.user_id = :userId
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getAllByUserIdWithOffset(
        @Param("userId") userId: String,
        @Param("_offset") offset: Int = 0,
        @Param("_limit") limit: Int = 0
    ): Flow<UserProduct>

    @Query(
        """
        SELECT * FROM users_products
        WHERE users_products.product_id = :productId 
        AND users_products.is_reviewed = :isReviewed
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getByProductIdAndReviewedWithOffset(
        @Param("productId") productId: String,
        @Param("isReviewed") isReviewed: Boolean,
        @Param("_offset") offset: Int = 0,
        @Param("_limit") limit: Int = 0
    ): Flow<UserProduct>

    @Query(
        """
        SELECT * FROM users_products
        WHERE users_products.user_id = :userId 
        AND users_products.is_reviewed = :isReviewed
        LIMIT :_limit
        OFFSET :_offset
        
    """
    )
    fun getByUserIdAndReviewedWithOffset(
        @Param("userId") userId: String,
        @Param("isReviewed") isReviewed: Boolean,
        @Param("_offset") offset: Int = 0,
        @Param("_limit") limit: Int = 0
    ): Flow<UserProduct>

    suspend fun countByProductId(productId: String): Long

    suspend fun countByUserId(userId: String): Long

    @Query(
        """
        SELECT COUNT(users_products.user_product_id) 
        FROM users_products 
        WHERE users_products.product_id = :productId  
        AND users_products.is_reviewed = :isReviewed
    """
    )
    suspend fun countByProductIdAndReviewed(
        @Param("productId") productId: String,
        @Param("isReviewed") isReviewed: Boolean,
    ): Long

    @Query(
        """
        SELECT COUNT(users_products.user_product_id) 
        FROM users_products 
        WHERE users_products.user_id = :userId   
        AND users_products.is_reviewed = :isReviewed
    """
    )
    suspend fun countByUserIdAndReviewed(
        @Param("userId") userId: String,
        @Param("isReviewed") isReviewed: Boolean,
    ): Long
}
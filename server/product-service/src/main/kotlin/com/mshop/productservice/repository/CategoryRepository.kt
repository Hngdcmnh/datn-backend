package com.mshop.productservice.repository

import com.mshop.productservice.model.Category
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository : CoroutineCrudRepository<Category, String> {
    @Query("""
        SELECT categories_products.category_id 
        FROM categories_products 
        WHERE categories_products.product_id = :productId;
    """)
    fun getCategoryIdByProductId(@Param("productId") productId: String): Flow<String>
}
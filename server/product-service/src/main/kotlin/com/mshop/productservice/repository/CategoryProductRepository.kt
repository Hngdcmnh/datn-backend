package com.mshop.productservice.repository

import com.mshop.productservice.model.CategoryProduct
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface CategoryProductRepository : CoroutineCrudRepository<CategoryProduct, String> {

    @Modifying
    @Transactional
    @Query ("""
        DELETE FROM categories_products 
        WHERE categories_products.product_id = :productId AND categories_products.category_id = :categoryId;
    """
    )
    suspend fun deleteProductOfCategory(@Param("productId") productId: String, @Param("categoryId") categoryId: String)

    @Query("""
        SELECT COUNT(categories_products.product_id) FROM categories_products 
        WHERE categories_products.category_id = :categoryId;
    """
    )
    suspend fun countProductOfCategory(@Param("categoryId") categoryId: String): Long


    fun getByProductId(productId: String): Flow<CategoryProduct>

    suspend fun getByProductIdAndCategoryId(productId: String, categoryId: String): CategoryProduct?

    suspend fun deleteCategoryProductByCategoryIdAndProductId(categoryId: String, productId: String)
}
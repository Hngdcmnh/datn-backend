package com.mshop.productservice.repository

import com.mshop.productservice.model.OrderProductInfo
import com.mshop.productservice.model.Product
import com.mshop.productservice.model.ProductInfo
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : CoroutineCrudRepository<Product, String> {

    suspend fun getProductBySku(sku: String): Product?

    @Query(
        """
        SELECT 
        products.product_id, 
        products.supplier_id,
        products.sku, 
        products.name,
        products.brand,  
        products.price,
        products.listed_price,
        products.amount,
        products.sold_amount,
        products.color,
        products.size,
        products.saleable,
        products.rate,
        products.discount,
        products.start_date_discount,
        products.end_date_discount
        FROM products
        WHERE products.product_id = :productId
        LIMIT 1;
    """
    )
    suspend fun getProductInfoById(@Param("productId") productId: String): ProductInfo?

    @Query(
        """
        SELECT 
        products.product_id, 
        products.supplier_id,
        products.sku, 
        products.name,products.brand, 
         
        products.price,
        products.listed_price,
        products.amount,
        products.sold_amount,
        products.color,
        products.size,
        products.saleable,
        products.rate,
        products.discount,
        products.start_date_discount,
        products.end_date_discount
        FROM products 
        WHERE products.name LIKE :_name
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getProductInfoByNameWithOffset(
        @Param("_name") name: String,
        @Param("_offset") offset: Int = 0,
        @Param("_limit") limit: Int = 0
    ): Flow<ProductInfo>

    suspend fun countProductByNameContainsIgnoreCase(name: String): Long

    @Query(
        """
        SELECT 
        products.product_id, 
        products.supplier_id,
        products.sku, 
        products.name,
        products.brand, 
        products.price,
        products.listed_price,
        products.amount,
        products.sold_amount,
        products.color,
        products.size,
        products.saleable,
        products.rate,
        products.discount,
        products.start_date_discount,
        products.end_date_discount
        FROM products
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getProductInfoWithOffset(@Param("_offset") offset: Int = 0, @Param("_limit") limit: Int = 0): Flow<ProductInfo>



    @Query(
        """
        SELECT 
        products.product_id, 
        products.supplier_id,
        products.sku, 
        products.name,
        products.brand, 
        products.price,
        products.listed_price,
        products.amount,
        products.sold_amount,
        products.color,
        products.size,
        products.saleable,
        products.rate,
        products.discount,
        products.start_date_discount,
        products.end_date_discount
        FROM products 
        INNER JOIN users_products ON users_products.user_id = :_user_id
        WHERE products.product_id = users_products.product_id
        ORDER BY users_products.match_rate DESC
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getProductInfoByUserIdWithOffset(@Param("_user_id") userId:String ="", @Param("_offset") offset: Int = 0, @Param("_limit") limit: Int = 0): Flow<ProductInfo>

    @Query(
        """
        SELECT 
        products.product_id, 
        products.supplier_id,
        products.sku, 
        products.name,products.brand, 
        products.price,
        products.listed_price,
        products.amount,
        products.sold_amount,
        products.color,
        products.size,
        products.saleable,
        products.rate,
        products.discount,
        products.start_date_discount,
        products.end_date_discount
        FROM products
        INNER JOIN categories_products
        WHERE categories_products.category_id = :categoryId
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getProductInfoByCategoryIdWithOffset(
        @Param("categoryId") categoryId: String,
        @Param("_offset") offset: Int = 0,
        @Param("_limit") limit: Int = 0
    ): Flow<ProductInfo>

    @Query(
        """
        SELECT 
        products.product_id, 
        products.supplier_id,
        products.sku, 
        products.name,products.brand, 
        products.price,
        products.listed_price,
        products.amount,
        products.sold_amount,
        products.color,
        products.size,
        products.saleable,
        products.rate,
        products.discount,
        products.start_date_discount,
        products.end_date_discount
        FROM products
        INNER JOIN categories_products
        WHERE categories_products.category_id = :categoryId
        ORDER BY products.rate DESC
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getProductInfoByCategoryIdWithOffsetAndOrderByRatingDesc(
        @Param("categoryId") categoryId: String,
        @Param("_offset") offset: Int = 0,
        @Param("_limit") limit: Int = 0
    ): Flow<ProductInfo>

    @Query(
        """
        SELECT 
        products.product_id, 
        products.supplier_id,
        products.sku, 
        products.name,products.brand, 
        products.price,
        products.listed_price,
        products.amount,
        products.sold_amount,
        products.color,
        products.size,
        products.saleable,
        products.rate,
        products.discount,
        products.start_date_discount,
        products.end_date_discount
        FROM products
        INNER JOIN categories_products
        WHERE categories_products.category_id = :categoryId
        ORDER BY products.rate ASC 
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getProductInfoByCategoryIdWithOffsetAndOrderByRatingAsc(
        @Param("categoryId") categoryId: String,
        @Param("_offset") offset: Int = 0,
        @Param("_limit") limit: Int = 0
    ): Flow<ProductInfo>

    @Query(
        """
        SELECT 
        products.product_id, 
        products.supplier_id,
        products.sku, 
        products.name,products.brand, 
        products.price,
        products.listed_price,
        products.amount,
        products.sold_amount,
        products.color,
        products.size,
        products.saleable,
        products.rate,
        products.discount,
        products.start_date_discount,
        products.end_date_discount
        FROM products
        ORDER BY products.created_at DESC
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getProductInfoWithOffsetAndOrderByCreatedAtDesc(
        @Param("_offset") offset: Int = 0,
        @Param("_limit") limit: Int = 0
    ): Flow<ProductInfo>

    @Query(
        """
        SELECT 
        products.product_id, 
        products.supplier_id,
        products.sku, 
        products.name,products.brand, 
        products.price,
        products.listed_price,
        products.amount,
        products.sold_amount,
        products.color,
        products.size,
        products.saleable,
        products.rate,
        products.discount,
        products.start_date_discount,
        products.end_date_discount
        FROM products
        ORDER BY products.created_at ASC 
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getProductInfoWithOffsetAndOrderByCreatedAtAsc(
        @Param("_offset") offset: Int = 0,
        @Param("_limit") limit: Int = 0
    ): Flow<ProductInfo>

    suspend fun countBySupplierId(supplierId: String): Long

    @Query(
        """
        SELECT 
        products.product_id, 
        products.supplier_id,
        products.sku, 
        products.name,products.brand, 
        products.price,
        products.listed_price,
        products.amount,
        products.sold_amount,
        products.color,
        products.size,
        products.saleable,
        products.rate,
        products.discount,
        products.start_date_discount,
        products.end_date_discount
        FROM products 
        WHERE products.supplier_id = :supplierId
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getProductInfoBySupplierIdWithOffset(
        @Param("supplierId") supplierId: String,
        @Param("_offset") offset: Int = 0,
        @Param("_limit") limit: Int = 0
    ): Flow<ProductInfo>

    @Query(
        """
        SELECT 
        products.product_id, 
        products.supplier_id,
        products.sku, 
        products.name,products.brand, 
        products.price,
        products.listed_price,
        products.amount,
        products.sold_amount,
        products.color,
        products.size,
        products.saleable,
        products.rate,
        products.discount,
        products.start_date_discount,
        products.end_date_discount
        FROM products 
        WHERE products.supplier_id = :supplierId
        ORDER BY products.created_at DESC
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getProductInfoBySupplierIdWithOffsetAndOrderByCreatedAtDesc(
        @Param("supplierId") supplierId: String,
        @Param("_offset") offset: Int = 0,
        @Param("_limit") limit: Int = 0,
    ): Flow<ProductInfo>

    @Query(
        """
        SELECT 
        products.product_id, 
        products.supplier_id,
        products.sku, 
        products.name,products.brand, 
        products.price,
        products.listed_price,
        products.amount,
        products.sold_amount,
        products.color,
        products.size,
        products.saleable,
        products.rate,
        products.discount,
        products.start_date_discount,
        products.end_date_discount
        FROM products 
        WHERE products.supplier_id = :supplierId
        ORDER BY products.created_at ASC 
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getProductInfoBySupplierIdWithOffsetAndOrderByCreatedAtAsc(
        @Param("supplierId") supplierId: String,
        @Param("_offset") offset: Int = 0,
        @Param("_limit") limit: Int = 0,
    ): Flow<ProductInfo>

    @Query(
        """
        SELECT COUNT(products.product_id)
        FROM products
        INNER JOIN categories_products
        WHERE categories_products.category_id = :categoryId
        AND products.name LIKE :_name
    """
    )
    suspend fun countProductByCategoryIdAndName(
        @Param("categoryId") categoryId: String,
        @Param("_name") name: String
    ): Long

    @Query(
        """
        SELECT 
        products.product_id, 
        products.supplier_id,
        products.sku, 
        products.name,products.brand, 
        products.price,
        products.listed_price,
        products.amount,
        products.sold_amount,
        products.color,
        products.size,
        products.saleable,
        products.rate,
        products.discount,
        products.start_date_discount,
        products.end_date_discount
        FROM products
        INNER JOIN categories_products
        WHERE categories_products.category_id = :categoryId
        AND products.name LIKE :_name
        LIMIT :_limit
        OFFSET :_offset
    """
    )
    fun getProductInfoByCategoryIdAndNameWithOffset(
        @Param("categoryId") categoryId: String,
        @Param("_name") name: String,
        @Param("_offset") offset: Int = 0,
        @Param("_limit") limit: Int = 0
    ): Flow<ProductInfo>

    @Query(
        """
        SELECT 
        products.product_id, 
        products.supplier_id,
        products.sku, 
        products.name,products.brand, 
        products.price,
        products.listed_price,
        products.amount,
        products.sold_amount,
        products.color,
        products.size,
        products.saleable,
        products.rate,
        products.discount,
        products.start_date_discount,
        products.end_date_discount
        FROM products
        WHERE products.product_id IN (:ids)
    """
    )
    fun getListProductInfoByIds(@Param("ids") ids: Collection<String>): Flow<ProductInfo>

    @Query(
        """
        SELECT
        products.product_id,
        products.supplier_id,
        products.sku,
        products.name,
        products.brand,
        products.price,
        products.weight,
        products.height,
        products.length,
        products.width
        FROM products
        WHERE products.product_id IN (:ids)
    """
    )
    fun getListOrderProductInfoByIds(@Param("ids") ids: List<String>): Flow<OrderProductInfo>


}
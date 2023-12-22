package com.mshop.productservice.service

import com.mshop.base.OffsetRequest
import com.mshop.base.SortRequest
import com.mshop.productservice.dto.*

interface ProductService {
    suspend fun addProductToCategory(categoryProductDto: CategoryProductDto): CategoryProductDto
    suspend fun deleteProductOfCategory(categoryProductDto: CategoryProductDto): CategoryProductDto

    suspend fun upsertProduct(userId: String, productDto: UpsertProductDto): UpsertProductDto
    suspend fun patchProduct(productDto: UpsertProductDto): UpsertProductDto
    suspend fun deleteProduct(productId: String): String

    suspend fun getListProductInfo(offsetRequest: com.mshop.base.OffsetRequest, sortRequest: com.mshop.base.SortRequest? = null): ProductPagination<ProductInfoDto>

    suspend fun getRecommendListProductInfo(userId:String, offsetRequest: com.mshop.base.OffsetRequest): ProductPagination<ProductInfoDto>
    suspend fun getListProductInfoByCategory(
        categoryId: String,
        offsetRequest: com.mshop.base.OffsetRequest,
        sortRequest: com.mshop.base.SortRequest? = null
    ): ProductPagination<ProductInfoDto>

    suspend fun getListProductInfoBySupplier(
        supplierId: String,
        offsetRequest: com.mshop.base.OffsetRequest,
        sortRequest: com.mshop.base.SortRequest? = null,
    ): ProductPagination<ProductInfoDto>

    suspend fun getListProductInfoByUserId(
        userId: String,
        offsetRequest: com.mshop.base.OffsetRequest,
        sortRequest: com.mshop.base.SortRequest? = null,
    ): ProductPagination<ProductInfoDto>

    suspend fun getProductDetailById(productId: String): ProductDto
    suspend fun getProductInfoById(productId: String): ProductInfoDto
    suspend fun getProductDetailBySku(sku: String): ProductDto

    suspend fun getProductInfoByCategoryAndName(
        categoryId: String?,
        name: String,
        offsetRequest: com.mshop.base.OffsetRequest,
        sortRequest: com.mshop.base.SortRequest? = null,
    ): ProductPagination<ProductInfoDto>

    suspend fun getListProductInfoByIds(ids: List<String>): List<ProductInfoDto>

    suspend fun getListOrderProductInfoByIds(ids: List<String>, supplierId: String?): List<OrderProductInfoDto>

    suspend fun patchAmountProduct(patchProduct: PatchAmountProductDto): PatchAmountProductDto
}
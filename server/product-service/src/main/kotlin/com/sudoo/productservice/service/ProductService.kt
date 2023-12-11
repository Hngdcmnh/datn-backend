package com.sudoo.productservice.service

import com.sudoo.domain.base.OffsetRequest
import com.sudoo.domain.base.SortRequest
import com.sudoo.productservice.dto.*

interface ProductService {
    suspend fun addProductToCategory(categoryProductDto: CategoryProductDto): CategoryProductDto
    suspend fun deleteProductOfCategory(categoryProductDto: CategoryProductDto): CategoryProductDto

    suspend fun upsertProduct(userId: String, productDto: UpsertProductDto): UpsertProductDto
    suspend fun patchProduct(productDto: UpsertProductDto): UpsertProductDto
    suspend fun deleteProduct(productId: String): String

    suspend fun getListProductInfo(offsetRequest: OffsetRequest, sortRequest: SortRequest? = null): ProductPagination<ProductInfoDto>

    suspend fun getRecommendListProductInfo(userId:String, offsetRequest: OffsetRequest): ProductPagination<ProductInfoDto>
    suspend fun getListProductInfoByCategory(
        categoryId: String,
        offsetRequest: OffsetRequest,
        sortRequest: SortRequest? = null
    ): ProductPagination<ProductInfoDto>

    suspend fun getListProductInfoBySupplier(
        supplierId: String,
        offsetRequest: OffsetRequest,
        sortRequest: SortRequest? = null,
    ): ProductPagination<ProductInfoDto>

    suspend fun getListProductInfoByUserId(
        userId: String,
        offsetRequest: OffsetRequest,
        sortRequest: SortRequest? = null,
    ): ProductPagination<ProductInfoDto>

    suspend fun getProductDetailById(productId: String): ProductDto
    suspend fun getProductInfoById(productId: String): ProductInfoDto
    suspend fun getProductDetailBySku(sku: String): ProductDto

    suspend fun getProductInfoByCategoryAndName(
        categoryId: String?,
        name: String,
        offsetRequest: OffsetRequest,
        sortRequest: SortRequest? = null,
    ): ProductPagination<ProductInfoDto>

    suspend fun getListProductInfoByIds(ids: List<String>): List<ProductInfoDto>

    suspend fun getListOrderProductInfoByIds(ids: List<String>, supplierId: String?): List<OrderProductInfoDto>

    suspend fun patchAmountProduct(patchProduct: PatchAmountProductDto): PatchAmountProductDto
}
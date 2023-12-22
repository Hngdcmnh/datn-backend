package com.mshop.productservice.controller

import com.mshop.base.BaseController
import com.mshop.base.BaseResponse
import com.mshop.base.OffsetRequest
import com.mshop.base.SortRequest
import com.mshop.common.Constants
import com.mshop.validator.ProductValidator
import com.mshop.productservice.dto.CategoryProductDto
import com.mshop.productservice.dto.PatchAmountProductDto
import com.mshop.productservice.dto.UpsertProductDto
import com.mshop.productservice.service.CategoryService
import com.mshop.productservice.service.ImageService
import com.mshop.productservice.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
@RequestMapping("/discovery/products")
class ProductController(
    private val productService: ProductService,
    private val imageService: ImageService,
    private val categoryService: CategoryService,
) : com.mshop.base.BaseController() {

    @PostMapping
    suspend fun upsertProduct(
        @RequestHeader(com.mshop.common.Constants.HEADER_USER_ID) userId: String,
        @RequestBody productDto: UpsertProductDto,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        productService.upsertProduct(userId, productDto)
    }

    @PatchMapping
    suspend fun pathProduct(
        @RequestBody productDto: UpsertProductDto,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        productService.patchProduct(productDto)
    }

    @DeleteMapping("/{productId}")
    suspend fun deleteProduct(
        @PathVariable("productId") productId: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        productService.deleteProduct(productId)
    }

    @GetMapping
    suspend fun getListProductInfo(
        @RequestParam("categoryId", required = false, defaultValue = "") categoryId: String?,
        @RequestParam("name", required = false, defaultValue = "") name: String,
        @RequestParam("offset", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_OFFSET) offset: Int,
        @RequestParam("limit", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_LIMIT) limit: Int,
        @RequestParam("sortBy", required = false, defaultValue = "") sortBy: String,
        @RequestParam("orderBy", required = false, defaultValue = "asc") orderBy: String,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        val offsetRequest = com.mshop.base.OffsetRequest(offset, limit)
        val sortRequest = if (sortBy.isBlank()) null else com.mshop.base.SortRequest(sortBy, orderBy)
        productService.getProductInfoByCategoryAndName(categoryId, name, offsetRequest, sortRequest)
    }

    @GetMapping("/recommend")
    suspend fun getRecommendListProductInfo(
        @RequestHeader(com.mshop.common.Constants.HEADER_USER_ID) userId: String,
        @RequestParam("offset", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_OFFSET) offset: Int,
        @RequestParam("limit", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_LIMIT) limit: Int,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        val offsetRequest = com.mshop.base.OffsetRequest(offset, limit)
        productService.getRecommendListProductInfo(userId = userId ,offsetRequest = offsetRequest)
    }

    @PostMapping("/list")
    suspend fun getListProductInfoByIds(
        @RequestParam("orderInfo", required = false, defaultValue = "false") orderInfo: Boolean,
        @RequestParam("supplierId", required = false) supplierId: String? = null,
        @RequestBody ids: List<String>,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        com.mshop.utils.Logger.info("ids: ${ids[0]} supplierId: ${supplierId == null}")
        if (orderInfo) {
            productService.getListOrderProductInfoByIds(ids, supplierId)
        } else {
            productService.getListProductInfoByIds(ids)
        }
    }

    @GetMapping("/{identify}")
    suspend fun getProductDetail(
        @PathVariable("identify") identify: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        if (ProductValidator.validateSku(identify)) {
            productService.getProductDetailBySku(identify)
        } else {
            productService.getProductDetailById(identify)
        }
    }

    @GetMapping("/{productId}/info")
    suspend fun getProductInfo(
        @PathVariable("productId") productId: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        println("productId: $productId")
        productService.getProductInfoById(productId)
    }

    @GetMapping("/sku/{sku}")
    suspend fun getProductDetailBySku(
        @RequestHeader(com.mshop.common.Constants.HEADER_USER_ID) userId: String,
        @PathVariable("sku") sku: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        productService.getProductDetailBySku(sku)
    }

    @GetMapping("/{productId}/images")
    suspend fun getImagesByProductId(
        @PathVariable("productId") productId: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        imageService.getImageByOwnerId(productId)
    }

    @DeleteMapping("/{productId}/images/{imageId}")
    suspend fun deleteImageByProductId(
            @PathVariable("productId") productId: String,
            @PathVariable("imageId") imageId: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        imageService.deleteImage(imageId)
    }

    @GetMapping("/{productId}/categories")
    suspend fun getCategoriesByProductId(
        @PathVariable("productId") productId: String,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        categoryService.getCategoriesByProductId(productId)
    }

    @PostMapping("/{productId}/categories/{categoryId}")
    suspend fun upsertCategoryToProduct(
        @PathVariable("productId") productId: String,
        @PathVariable("categoryId") categoryId: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        val categoryProduct = CategoryProductDto(
            categoryId = categoryId,
            productId = productId
        )
        productService.addProductToCategory(categoryProduct)
    }

    @DeleteMapping("/{productId}/categories/{categoryId}")
    suspend fun deleteProductFromCategory(
        @PathVariable("productId") productId: String,
        @PathVariable("categoryId") categoryId: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        val categoryProduct = CategoryProductDto(
            categoryId = categoryId,
            productId = productId
        )
        productService.deleteProductOfCategory(categoryProduct)
    }

    @GetMapping("/search")
    suspend fun searchProductByName(
        @RequestParam("categoryId", required = false, defaultValue = "null") categoryId: String?,
        @RequestParam("name", required = false, defaultValue = "") name: String,
        @RequestParam("offset", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_OFFSET) offset: Int,
        @RequestParam("limit", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_LIMIT) limit: Int,
        @RequestParam("sortBy", required = false, defaultValue = "") sortBy: String,
        @RequestParam("orderBy", required = false, defaultValue = "asc") orderBy: String,
        @RequestParam("saleable", required = false, defaultValue = "") saleable: String,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        val offsetRequest = com.mshop.base.OffsetRequest(offset, limit)
        productService.getProductInfoByCategoryAndName(categoryId, name, offsetRequest)
    }

    @PutMapping("/internal/amount")
    suspend fun pathAmountPromotion(
        @RequestBody product: PatchAmountProductDto
    ) : ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        productService.patchAmountProduct(product)
    }

    @GetMapping("/headers")
    suspend fun getHeader(
        @RequestHeader header: Map<String, *>
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        header
    }
}
package com.mshop.productservice.controller

import com.mshop.base.BaseController
import com.mshop.base.BaseResponse
import com.mshop.base.OffsetRequest
import com.mshop.common.Constants
import com.mshop.productservice.dto.CategoryDto
import com.mshop.productservice.dto.CategoryProductDto
import com.mshop.productservice.service.CategoryService
import com.mshop.productservice.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/discovery/categories")
class CategoryController(
    private val categoryService: CategoryService,
    private val productService: ProductService,
) : com.mshop.base.BaseController() {

    @GetMapping
    suspend fun getCategories(
        @RequestParam("select", required = false, defaultValue = "") select: String,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        categoryService.getCategories(select)
    }

    @GetMapping("/{categoryId}")
    suspend fun getCategoryById(
        @PathVariable("categoryId") categoryId: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        categoryService.getCategoryById(categoryId)
    }

    @PostMapping
    suspend fun upsertCategory(
        @RequestBody categoryDto: CategoryDto
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        categoryService.upsertCategory(categoryDto)
    }

    @DeleteMapping("/{categoryId}")
    suspend fun deleteCategory(
        @PathVariable("categoryId") categoryId: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        categoryService.deleteCategory(categoryId)
    }

    @GetMapping("/{categoryId}/products")
    suspend fun getProductByCategory(
        @PathVariable("categoryId") categoryId: String,
        @RequestParam("offset", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_OFFSET) offset: Int,
        @RequestParam("limit", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_LIMIT) limit: Int,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        val offsetRequest = com.mshop.base.OffsetRequest(offset, limit)
        productService.getListProductInfoByCategory(categoryId, offsetRequest,)
    }

    @PostMapping("/{categoryId}/products")
    suspend fun upsertProductToCategory(
        @PathVariable("categoryId") categoryId: String,
        @RequestBody categoryProduct: CategoryProductDto
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        categoryProduct.categoryId = categoryId
        productService.addProductToCategory(categoryProduct)
    }

    @DeleteMapping("/{categoryId}/products")
    suspend fun deleteProductFromCategory(
        @PathVariable("categoryId") categoryId: String,
        @RequestBody categoryProduct: CategoryProductDto
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        categoryProduct.categoryId = categoryId
        productService.deleteProductOfCategory(categoryProduct)
    }


}
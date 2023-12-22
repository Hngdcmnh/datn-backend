package com.mshop.productservice.controller

import com.mshop.base.BaseController
import com.mshop.base.BaseResponse
import com.mshop.base.OffsetRequest
import com.mshop.base.SortRequest
import com.mshop.common.Constants
import com.mshop.productservice.dto.UpsertSupplierDto
import com.mshop.productservice.service.ProductService
import com.mshop.productservice.service.SupplierService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/discovery/suppliers")
class SupplierController(
    private val supplierService: SupplierService,
    private val productService: ProductService,
) : com.mshop.base.BaseController() {

    @GetMapping
    suspend fun getSuppliers(): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        supplierService.getSuppliers()
    }

    @GetMapping("/{supplierId}")
    suspend fun getSupplierById(
        @PathVariable("supplierId") supplierId: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        supplierService.getSupplierById(supplierId)
    }

    @GetMapping("/{supplierId}/info")
    suspend fun getSupplierInfoById(
        @PathVariable("supplierId") supplierId: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        supplierService.getSupplierInfoById(supplierId)
    }

    @GetMapping("/self")
    suspend fun getSupplierByUserId(
        @RequestHeader(com.mshop.common.Constants.HEADER_USER_ID) userId: String,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        supplierService.getSupplierByUserId(userId)
    }

    @GetMapping("/self/info")
    suspend fun getSupplierInfoByUserId(
        @RequestHeader(com.mshop.common.Constants.HEADER_USER_ID) userId: String,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        supplierService.getSupplierInfoByUserId(userId)
    }

    @PostMapping
    suspend fun upsertSupplier(
        @RequestHeader(com.mshop.common.Constants.HEADER_USER_ID) userId: String,
        @RequestBody supplierDto: UpsertSupplierDto
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        supplierService.upsertSupplier(userId, supplierDto)
    }

    @DeleteMapping("/{supplierId}")
    suspend fun deleteSupplier(
        @PathVariable("supplierId") supplierId: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        supplierService.deleteSupplier(supplierId)
    }

    @GetMapping("/{supplierId}/products")
    suspend fun getProductBySupplier(
        @PathVariable("supplierId") supplierId: String,
        @RequestParam("offset", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_OFFSET) offset: Int,
        @RequestParam("limit", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_LIMIT) limit: Int,
        @RequestParam("sortBy", required = false, defaultValue = "") sortBy: String,
        @RequestParam("orderBy", required = false, defaultValue = "asc") orderBy: String,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        val offsetRequest = com.mshop.base.OffsetRequest(offset, limit)
        val sortRequest = if (sortBy.isBlank()) null else com.mshop.base.SortRequest(sortBy, orderBy)
        productService.getListProductInfoBySupplier(supplierId, offsetRequest, sortRequest)
    }

    @GetMapping("/self/products")
    suspend fun getProductBySupplierSelf(
        @RequestHeader(com.mshop.common.Constants.HEADER_USER_ID) userId: String,
        @RequestParam("offset", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_OFFSET) offset: Int,
        @RequestParam("limit", required = false, defaultValue = com.mshop.common.Constants.DEFAULT_LIMIT) limit: Int,
        @RequestParam("sortBy", required = false, defaultValue = "") sortBy: String,
        @RequestParam("orderBy", required = false, defaultValue = "asc") orderBy: String,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        val offsetRequest = com.mshop.base.OffsetRequest(offset, limit)
        val sortRequest = if (sortBy.isBlank()) null else com.mshop.base.SortRequest(sortBy, orderBy)
        productService.getListProductInfoByUserId(userId, offsetRequest, sortRequest)
    }
}
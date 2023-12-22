package com.mshop.productservice.service

import com.mshop.productservice.dto.SupplierDto
import com.mshop.productservice.dto.SupplierInfoDto
import com.mshop.productservice.dto.UpsertSupplierDto

interface SupplierService {
    suspend fun getSuppliers(): List<SupplierDto>
    suspend fun getSupplierById(supplierId: String): SupplierDto
    suspend fun getSupplierInfoById(supplierId: String): SupplierInfoDto
    suspend fun getSupplierByUserId(userId: String): SupplierDto
    suspend fun getSupplierInfoByUserId(userId: String): SupplierInfoDto

    suspend fun upsertSupplier(userId: String, supplierDto: UpsertSupplierDto): SupplierDto

    suspend fun deleteSupplier(supplierId: String): String
}
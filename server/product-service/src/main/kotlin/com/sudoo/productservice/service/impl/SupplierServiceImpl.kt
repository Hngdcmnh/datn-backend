package com.sudoo.productservice.service.impl

import com.sudoo.domain.exception.ApiException
import com.sudoo.domain.exception.BadRequestException
import com.sudoo.domain.exception.NotFoundException
import com.sudoo.domain.validator.ProductValidator
import com.sudoo.productservice.dto.AddressDto
import com.sudoo.productservice.dto.SupplierDto
import com.sudoo.productservice.dto.SupplierInfoDto
import com.sudoo.productservice.dto.UpsertSupplierDto
import com.sudoo.productservice.dto.ghn.CreateStoreRequest
import com.sudoo.productservice.mapper.toSupplier
import com.sudoo.productservice.mapper.toSupplierDto
import com.sudoo.productservice.mapper.toSupplierInfoDto
import com.sudoo.productservice.repository.ProductRepository
import com.sudoo.productservice.repository.SupplierRepository
import com.sudoo.productservice.service.GHNService
import com.sudoo.productservice.service.SupplierService
import com.sudoo.productservice.service.UserService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class SupplierServiceImpl(
    private val supplierRepository: SupplierRepository,
    private val productRepository: ProductRepository,
    private val userService: UserService,
    private val ghnService: GHNService
) : SupplierService {
    override suspend fun getSuppliers(): List<SupplierDto> = coroutineScope {
        val suppliers = supplierRepository.findAll()
            .map {
                async {
                    it.toSupplierDto(
                        totalProducts = productRepository.countBySupplierId(it.supplierId).toInt(),
                        address = userService.getAddressById(it.addressId)
                    )
                }
            }
        suppliers.toList().awaitAll()
    }

    override suspend fun getSupplierById(supplierId: String): SupplierDto = coroutineScope {
        val supplier =
            supplierRepository.findById(supplierId) ?: throw NotFoundException("Not found supplier $supplierId")
        val totalProducts = async { productRepository.countBySupplierId(supplierId).toInt() }
        val address = async { userService.getAddressById(supplier.addressId) }
        supplier.toSupplierDto(totalProducts = totalProducts.await(), address = address.await())
    }

    override suspend fun getSupplierInfoById(supplierId: String): SupplierInfoDto {
        val supplier =
            supplierRepository.findById(supplierId) ?: throw NotFoundException("Not found supplier $supplierId")
        val address = userService.getAddressById(supplier.addressId)
        return supplier.toSupplierInfoDto(address = address)
    }

    override suspend fun getSupplierByUserId(userId: String): SupplierDto = coroutineScope {
        val supplier =
            supplierRepository.getByUserId(userId) ?: throw NotFoundException("Not found supplier of user $userId")
        val totalProducts = async { productRepository.countBySupplierId(supplier.supplierId).toInt() }
        val address = async { userService.getAddressById(supplier.addressId) }
        supplier.toSupplierDto(totalProducts = totalProducts.await(), address = address.await())
    }

    override suspend fun getSupplierInfoByUserId(userId: String): SupplierInfoDto {
        val supplier =
            supplierRepository.getByUserId(userId) ?: throw NotFoundException("Not found supplier of user $userId")
        val address = userService.getAddressById(supplier.addressId)
        return supplier.toSupplierInfoDto(address = address)
    }

    override suspend fun upsertSupplier(userId: String, supplierDto: UpsertSupplierDto): SupplierDto {
        if (supplierDto.address == null) throw BadRequestException("Required address to create store")
        val ghnShopId = if (supplierDto.supplierId.isNullOrEmpty()) {
            if (supplierDto.phoneNumber == null) throw BadRequestException("Required address to create store")
            ghnService.createStore(
                CreateStoreRequest(
                    districtId = supplierDto.address.districtID,
                    wardCode = supplierDto.address.wardCode,
                    name = supplierDto.name,
                    phone = supplierDto.phoneNumber,
                    address = supplierDto.address.address
                )
            ).shopId
        } else {
            supplierDto.ghnShopId ?: throw BadRequestException("Required shopId")
        }

        val supplier = supplierDto.toSupplier(userId, ghnShopId = ghnShopId)
        val address = if (supplier.isNewSupplier) {
            val addressDto = supplierDto.address
            userService.postAddress(addressDto).also {
                supplier.addressId =
                    it.addressId ?: throw ApiException(HttpStatus.BAD_REQUEST, "Error address must be not null")
            }
        } else {
            userService.getAddressById(supplier.addressId)
        }
        supplierRepository.save(supplier)
        return supplier.toSupplierDto(totalProducts = 0, address = address)
    }

    override suspend fun deleteSupplier(supplierId: String): String {
        val supplier =
            supplierRepository.findById(supplierId) ?: throw NotFoundException("Not found supplier $supplierId")

        supplierRepository.delete(supplier)
        return supplierId
    }

    private suspend fun saveAddress(address: AddressDto): String {
        val savedAddress = userService.postAddress(address)
        return savedAddress.addressId ?: throw ApiException(HttpStatus.BAD_REQUEST, "Error address must be not null")
    }
}
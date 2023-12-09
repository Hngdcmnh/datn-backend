package com.sudoo.cartservice.service.impl

import com.sudoo.cartservice.controller.dto.ProductInfoDto
import com.sudoo.cartservice.controller.dto.UpsertListUserProductDto
import com.sudoo.cartservice.controller.dto.order.OrderProductInfoDto
import com.sudoo.cartservice.service.ProductService
import com.sudoo.domain.base.BaseResponse
import com.sudoo.domain.exception.NotFoundException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull

@Service
class ProductServiceImpl(
    @Qualifier("product-service") private val client: WebClient
) : ProductService {
    override suspend fun getProductInfo(productId: String): ProductInfoDto {
        val response = try {
            client.get()
                .uri("/$productId/info")
                .retrieve()
                .awaitBodyOrNull<BaseResponse<ProductInfoDto>>()
                ?: throw NotFoundException("Not found product $productId")
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
        return response.data ?: throw NotFoundException("Not found product $productId")
    }

    override suspend fun getListOrderProductInfoByIds(
        productIds: List<String>,
        supplierId: String?
    ): List<OrderProductInfoDto> {
        val response = try {
            val uri = "/list?orderInfo=true"
            client.post()
                .uri(supplierId?.let { "$uri&supplierId=$it" } ?: uri)
                .bodyValue(productIds)
                .retrieve()
                .awaitBodyOrNull<BaseResponse<List<OrderProductInfoDto>>>()
                ?: throw NotFoundException("Not found product list")
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
        return response.data ?: throw NotFoundException("Not found product list")
    }

    override suspend fun upsertUserProductByUserAndSupplier(upsertListUserProduct: UpsertListUserProductDto): List<String> {
        val response = try {
            client.post()
                .uri("/internal/user-product/list")
                .bodyValue(upsertListUserProduct)
                .retrieve()
                .awaitBodyOrNull<BaseResponse<List<String>>>()
                ?: throw NotFoundException("Not found product list")
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
        return response.data ?: throw NotFoundException("Not found product list")
    }

}
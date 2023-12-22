package com.mshop.productservice.service.impl

import com.mshop.exception.ApiException
import com.mshop.productservice.dto.ghn.CreateStoreRequest
import com.mshop.productservice.dto.ghn.GHNResponse
import com.mshop.productservice.dto.ghn.GHNStoreDto
import com.mshop.productservice.service.GHNService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull

@Service
class GHNServiceImpl (
    @Qualifier("ghn-service") private val client: WebClient
) : GHNService {
    override suspend fun createStore(request: CreateStoreRequest): GHNStoreDto {
        val response = client.post()
            .uri("/shop/register")
            .header("")
            .bodyValue(request)
            .retrieve()
            .awaitBodyOrNull<GHNResponse<GHNStoreDto>>() ?: throw com.mshop.exception.ApiException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Create GHN store error"
        )
        return response.data ?: throw com.mshop.exception.ApiException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Create GHN store error"
        )
    }
}
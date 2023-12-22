package com.mshop.productservice.service.impl

import com.mshop.base.BaseResponse
import com.mshop.common.Constants
import com.mshop.exception.ApiException
import com.mshop.productservice.dto.AddressDto
import com.mshop.productservice.dto.UserInfoDto
import com.mshop.productservice.exception.AddressException
import com.mshop.productservice.exception.UserException
import com.mshop.productservice.service.UserService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull

@Service
class UserServiceImpl(
    @Qualifier("user-service") private val client: WebClient
) : UserService {
    override suspend fun getAllCustomer(): List<UserInfoDto> {
        val response = client.get()
            .uri("/users/info/all")
            .retrieve()
            .awaitBodyOrNull<com.mshop.base.BaseResponse<List<UserInfoDto>>>() ?: throw UserException()

        return response.data ?: throw UserException()
    }

    override suspend fun getUserInfo(userId: String): UserInfoDto {
        val response = client.get()
            .uri("/users/info")
            .header(com.mshop.common.Constants.HEADER_USER_ID, userId)
            .retrieve()
            .awaitBodyOrNull<com.mshop.base.BaseResponse<UserInfoDto>>() ?: throw UserException()

        return response.data ?: throw UserException()
    }

    override suspend fun postAddress(address: AddressDto): AddressDto {
        val response = client.post()
            .uri("/addresses")
            .bodyValue(address)
            .retrieve()
            .awaitBodyOrNull<com.mshop.base.BaseResponse<AddressDto>>() ?: throw com.mshop.exception.ApiException(
            HttpStatus.BAD_REQUEST,
            "Create address error"
        )
        return response.data ?: throw com.mshop.exception.ApiException(
            HttpStatus.BAD_REQUEST,
            "Create address error"
        )
    }

    override suspend fun getAddressById(addressId: String): AddressDto {
        val response = client.get()
            .uri("/addresses/$addressId")
            .retrieve()
            .awaitBodyOrNull<com.mshop.base.BaseResponse<AddressDto>>() ?: throw AddressException()
        return response.data ?: throw AddressException()
    }
}
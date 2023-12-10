package com.sudoo.productservice.service.impl

import com.sudoo.domain.base.BaseResponse
import com.sudoo.domain.common.Constants
import com.sudoo.domain.exception.ApiException
import com.sudoo.productservice.dto.AddressDto
import com.sudoo.productservice.dto.UserInfoDto
import com.sudoo.productservice.exception.AddressException
import com.sudoo.productservice.exception.UserException
import com.sudoo.productservice.service.UserService
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
            .awaitBodyOrNull<BaseResponse<List<UserInfoDto>>>() ?: throw UserException()

        return response.data ?: throw UserException()
    }

    override suspend fun getUserInfo(userId: String): UserInfoDto {
        val response = client.get()
            .uri("/users/info")
            .header(Constants.HEADER_USER_ID, userId)
            .retrieve()
            .awaitBodyOrNull<BaseResponse<UserInfoDto>>() ?: throw UserException()

        return response.data ?: throw UserException()
    }

    override suspend fun postAddress(address: AddressDto): AddressDto {
        val response = client.post()
            .uri("/addresses")
            .bodyValue(address)
            .retrieve()
            .awaitBodyOrNull<BaseResponse<AddressDto>>() ?: throw ApiException(HttpStatus.BAD_REQUEST, "Create address error")
        return response.data ?: throw ApiException(HttpStatus.BAD_REQUEST, "Create address error")
    }

    override suspend fun getAddressById(addressId: String): AddressDto {
        val response = client.get()
            .uri("/addresses/$addressId")
            .retrieve()
            .awaitBodyOrNull<BaseResponse<AddressDto>>() ?: throw AddressException()
        return response.data ?: throw AddressException()
    }
}
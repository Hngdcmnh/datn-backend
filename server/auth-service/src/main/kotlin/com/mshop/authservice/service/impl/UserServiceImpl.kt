package com.mshop.authservice.service.impl

import com.mshop.authservice.exception.UserException
import com.mshop.authservice.mapper.toUserDto
import com.mshop.authservice.model.Account
import com.mshop.authservice.service.UserService
import com.mshop.base.BaseResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull

@Service
class UserServiceImpl(
    @Qualifier("user-service") private val client: WebClient
) : UserService {

    override suspend fun createUserForAccount(account: Account): Boolean {
        val userDto = account.toUserDto()

        client.post()
            .uri("/")
            .bodyValue(userDto)
            .retrieve()
            .awaitBodyOrNull<BaseResponse<*>>() ?: throw com.mshop.authservice.exception.UserException()

        return true
    }
}
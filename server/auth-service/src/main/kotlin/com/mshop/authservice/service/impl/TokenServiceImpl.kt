package com.mshop.authservice.service.impl

import com.mshop.authservice.dto.TokenDto
import com.mshop.authservice.repository.AccountRepository
import com.mshop.authservice.service.AccountService
import com.mshop.authservice.service.TokenService
import com.mshop.authservice.utils.TokenUtils
import com.mshop.exception.BadRequestException
import org.springframework.stereotype.Service

@Service
class TokenServiceImpl(
    private val accountRepository: AccountRepository,
    private val tokenUtils: TokenUtils
) : TokenService {
    override suspend fun refreshToken(refreshToken: String): com.mshop.authservice.dto.TokenDto {
        val userId = tokenUtils.getUserIdFromRefreshToken(refreshToken)
        val account = accountRepository.findById(userId) ?: throw BadRequestException("Refresh token invalid")
        val token = tokenUtils.generateToken(userId)
        val newRefreshToken = tokenUtils.generateRefreshToken(userId)
        return com.mshop.authservice.dto.TokenDto(
            token = token,
            refreshToken = newRefreshToken,
            role = account.role
        )
    }
}
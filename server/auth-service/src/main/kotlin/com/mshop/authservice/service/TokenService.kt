package com.mshop.authservice.service

import com.mshop.authservice.dto.TokenDto

interface TokenService {
    suspend fun refreshToken(refreshToken: String): com.mshop.authservice.dto.TokenDto
}
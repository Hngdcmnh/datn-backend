package com.mshop.authservice.service

import com.mshop.authservice.dto.TokenDto
import com.mshop.authservice.dto.VerifyDto

interface OtpService {
    suspend fun generateOtp(emailOrPhoneNumber: String): Boolean
    suspend fun verifyOtp(verifyDto: com.mshop.authservice.dto.VerifyDto): com.mshop.authservice.dto.TokenDto?
}
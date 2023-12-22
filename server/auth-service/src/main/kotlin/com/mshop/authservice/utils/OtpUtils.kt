package com.mshop.authservice.utils

import com.mshop.authservice.dto.VerifyDto

interface OtpUtils {
    suspend fun generateOtp(emailOrPhoneNumber: String): Boolean
    suspend fun verifyOtp(verifyDto: com.mshop.authservice.dto.VerifyDto): Boolean
}
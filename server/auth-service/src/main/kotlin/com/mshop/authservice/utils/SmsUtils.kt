package com.mshop.authservice.utils

import com.mshop.authservice.dto.VerifyDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
@Qualifier("SMS")
class SmsUtils : OtpUtils{

    @Value("\${otp.timeout}")
    private var otpTimeout: Long = 0

    override suspend fun generateOtp(emailOrPhoneNumber: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun verifyOtp(verifyDto: com.mshop.authservice.dto.VerifyDto): Boolean {
        TODO("Not yet implemented")
    }
}
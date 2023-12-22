package com.mshop.authservice.controller

import com.mshop.authservice.dto.VerifyDto
import com.mshop.authservice.service.OtpService
import com.mshop.base.BaseController
import org.springframework.web.bind.annotation.*

@RestController
class OtpController(
    private val otpService: OtpService,
) : BaseController() {

    @GetMapping("/generate-otp/{emailOrPhoneNumber}")
    suspend fun generateOtp(@PathVariable emailOrPhoneNumber: String) = handle {
        otpService.generateOtp(emailOrPhoneNumber)
    }

    @PostMapping("/verify-otp")
    suspend fun verifyOtp(@RequestBody verifyDto: com.mshop.authservice.dto.VerifyDto) = handle {
        otpService.verifyOtp(verifyDto)
    }

}
package com.mshop.authservice.dto

data class VerifyDto(
    val emailOrPhoneNumber: String,
    val otp: String,
)
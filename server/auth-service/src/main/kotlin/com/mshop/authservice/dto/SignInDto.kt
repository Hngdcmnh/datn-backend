package com.mshop.authservice.dto

data class SignInDto(
    val emailOrPhoneNumber: String,
    val password: String
)

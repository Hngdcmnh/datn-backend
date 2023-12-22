package com.mshop.authservice.dto

import com.mshop.authservice.model.Provider
import com.mshop.authservice.model.Role

data class SignUpDto (
    val emailOrPhoneNumber: String,
    val password: String,
    val provider: Provider = Provider.AUTH_SERVICE,
    val role: Role = Role.CONSUMER,
)
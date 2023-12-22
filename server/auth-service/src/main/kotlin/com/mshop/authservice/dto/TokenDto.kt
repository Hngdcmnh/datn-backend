package com.mshop.authservice.dto

import com.mshop.authservice.model.Role
import com.mshop.common.Constants

data class TokenDto(
    val token: String,
    val refreshToken: String,
    val role: Role,
    val type: String = Constants.TOKEN_TYPE,
)

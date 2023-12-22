package com.mshop.authservice.exception

import com.mshop.exception.ApiException
import org.springframework.http.HttpStatus

class RefreshTokenExpired(message: String? = null) : ApiException(HttpStatus.BAD_REQUEST, message ?: "Refresh token is expired")
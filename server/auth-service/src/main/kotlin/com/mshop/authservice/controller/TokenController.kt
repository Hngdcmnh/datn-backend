package com.mshop.authservice.controller

import com.mshop.authservice.service.TokenService
import com.mshop.authservice.utils.TokenUtils
import com.mshop.base.BaseController
import com.mshop.common.Constants
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class TokenController(
    private val tokenService: TokenService
) : BaseController() {

    @GetMapping("/refresh-token")
    suspend fun refreshToken(@RequestHeader(Constants.REFRESH_TOKEN) refreshToken: String) = handle {
        tokenService.refreshToken(refreshToken)
    }

}
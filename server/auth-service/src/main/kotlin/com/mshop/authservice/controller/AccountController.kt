package com.mshop.authservice.controller

import com.mshop.authservice.dto.ChangePasswordDto
import com.mshop.authservice.dto.SignInDto
import com.mshop.authservice.dto.SignUpDto
import com.mshop.authservice.service.AccountService
import com.mshop.base.BaseController
import com.mshop.base.BaseResponse
import com.mshop.common.Constants
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class AccountController(
    private val accountService: AccountService
) : BaseController() {

    @GetMapping("/internal/accounts/role")
    suspend fun getRole(
        @RequestHeader(Constants.HEADER_USER_ID) userId: String,
    ): ResponseEntity<BaseResponse<*>> = handle {
        accountService.getRole(userId)
    }

    @PostMapping("/sign-in")
    suspend fun signIn(@RequestBody body: com.mshop.authservice.dto.SignInDto): ResponseEntity<BaseResponse<*>> = handle {
        accountService.signIn(body)
    }

    @PostMapping("/sign-up")
    suspend fun signUp(@RequestBody body: com.mshop.authservice.dto.SignUpDto): ResponseEntity<BaseResponse<*>> = handle {
        accountService.signUp(body)
    }

    @PostMapping("/change-password")
    suspend fun changePassword(
        @RequestHeader(Constants.HEADER_USER_ID) userId: String,
        @RequestBody changePasswordDto: com.mshop.authservice.dto.ChangePasswordDto
    ): ResponseEntity<BaseResponse<*>> = handle {
        accountService.changePassword(userId, changePasswordDto)
    }

    @GetMapping("/logout")
    suspend fun logout(@RequestHeader(Constants.HEADER_USER_ID) userId: String): ResponseEntity<BaseResponse<*>> =
        handle {
            accountService.logout(userId)
        }
}
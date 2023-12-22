package com.mshop.authservice.service

import com.mshop.authservice.dto.ChangePasswordDto
import com.mshop.authservice.dto.SignInDto
import com.mshop.authservice.dto.SignUpDto
import com.mshop.authservice.dto.TokenDto
import com.mshop.authservice.model.Role

interface AccountService {

    suspend fun getRole(userId: String): Role
    suspend fun signIn(signInDto: com.mshop.authservice.dto.SignInDto): com.mshop.authservice.dto.TokenDto
    suspend fun signUp(signUpDto: com.mshop.authservice.dto.SignUpDto)
    suspend fun changePassword(userId: String, changePasswordDto: com.mshop.authservice.dto.ChangePasswordDto): Boolean
    suspend fun logout(userId: String): Boolean

    suspend fun registerAdmin(signUpDto: com.mshop.authservice.dto.SignUpDto): Boolean
}
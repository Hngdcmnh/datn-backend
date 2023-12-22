package com.mshop.authservice.dto

data class ChangePasswordDto(
    var userId: String? = null,
    val oldPassword: String,
    val newPassword: String,
)
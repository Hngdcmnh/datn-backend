package com.mshop.authservice.mapper

import com.mshop.authservice.dto.UserDto
import com.mshop.authservice.model.Account

fun Account.toUserDto(): com.mshop.authservice.dto.UserDto {
    return com.mshop.authservice.dto.UserDto(
        userId = userId,
        fullName = emailOrPhoneNumber.substringBefore("@"),
        emailOrPhoneNumber = emailOrPhoneNumber
    )
}
package com.mshop.authservice.mapper

import com.mshop.authservice.dto.SignUpDto
import com.mshop.authservice.model.Account
import com.mshop.utils.IdentifyCreator
import java.time.LocalDateTime

fun com.mshop.authservice.dto.SignUpDto.toModel(isValidated: Boolean = false): Account {
    return Account(
        userId = IdentifyCreator.create(),
        emailOrPhoneNumber = emailOrPhoneNumber,
        password = password,
        provider = provider,
        role = role,
        createAt = LocalDateTime.now(),
        isValidated = isValidated,
    )
}
package com.mshop.authservice.dto

import com.mshop.authservice.model.Gender
import java.time.LocalDate

data class UserDto(
    val userId: String,
    val fullName: String,
    val emailOrPhoneNumber: String,
    val dob: LocalDate = LocalDate.now(),
    val bio: String = "New user",
    val avatar: String = "user_default.png",
    val cover: String = "user_default.png",
    val address: com.mshop.authservice.dto.AddressDto = com.mshop.authservice.dto.AddressDto(),
    val gender: Gender = Gender.OTHER,
)
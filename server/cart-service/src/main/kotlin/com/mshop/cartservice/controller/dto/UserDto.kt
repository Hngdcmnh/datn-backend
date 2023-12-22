package com.mshop.cartservice.controller.dto

import java.util.*


data class UserDto(
        val userId: String,
        val fullName: String,
        var phone: String,
        var bio: String,
        var gender: String,
        var avatar: String,
        var cover: String,
        var address: String,
        val dob: Date,
)
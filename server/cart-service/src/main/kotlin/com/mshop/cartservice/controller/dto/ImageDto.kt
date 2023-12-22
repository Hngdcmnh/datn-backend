package com.mshop.cartservice.controller.dto

import java.io.Serializable


data class ImageDto(
        var imageId: String,
        var url: String,
        var ownerId: String
)

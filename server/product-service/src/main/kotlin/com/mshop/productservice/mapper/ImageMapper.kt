package com.mshop.productservice.mapper

import com.mshop.utils.IdentifyCreator
import com.mshop.productservice.dto.ImageDto
import com.mshop.productservice.dto.UpsertImageDto
import com.mshop.productservice.dto.UpsertProductDto
import com.mshop.productservice.model.Image

fun ImageDto.toImage(): Image {
    return Image (
        imageId = IdentifyCreator.createOrElse(imageId),
        ownerId = ownerId,
        url = url,
    ).also {
        it.isNewImage = imageId.isNullOrEmpty()
    }
}

fun Image.toImageDto(): ImageDto {
    return ImageDto(
        imageId = imageId,
        ownerId = ownerId,
        url = url,
    )
}

fun UpsertImageDto.toImage(ownerId: String): Image {
    return Image (
            imageId = IdentifyCreator.createOrElse(imageId),
            ownerId = ownerId,
            url = url,
    ).also {
        it.isNewImage = imageId.isNullOrEmpty()
    }
}
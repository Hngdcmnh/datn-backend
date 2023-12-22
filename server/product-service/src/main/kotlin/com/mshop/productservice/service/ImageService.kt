package com.mshop.productservice.service

import com.mshop.productservice.dto.ImageDto

interface ImageService {
    suspend fun getImageByOwnerId(ownerId: String): List<ImageDto>
    suspend fun getImageById(imageId: String): ImageDto
    suspend fun upsertImage(imageDto: ImageDto): ImageDto
    suspend fun deleteImage(imageId: String): ImageDto

    suspend fun getBanners(): List<ImageDto>

    suspend fun upsertBanner(imageDto: ImageDto): ImageDto
}
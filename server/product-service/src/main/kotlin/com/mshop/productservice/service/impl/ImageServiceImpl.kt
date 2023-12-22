package com.mshop.productservice.service.impl

import com.mshop.exception.NotFoundException
import com.mshop.productservice.dto.ImageDto
import com.mshop.productservice.mapper.toImage
import com.mshop.productservice.mapper.toImageDto
import com.mshop.productservice.model.Image
import com.mshop.productservice.repository.ImageRepository
import com.mshop.productservice.service.ImageService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class ImageServiceImpl(
    private val imageRepository: ImageRepository,
) : ImageService {

    private val banner = "banner"

    override suspend fun getImageByOwnerId(ownerId: String): List<ImageDto> {
        return imageRepository.getAllByOwnerId(ownerId).map {
            it.toImageDto()
        }.toList()
    }

    override suspend fun getImageById(imageId: String): ImageDto {
        val image = imageRepository.findById(imageId) ?: throw com.mshop.exception.NotFoundException("Not found image $imageId")
        return image.toImageDto()
    }

    override suspend fun upsertImage(imageDto: ImageDto): ImageDto {
        val image = imageDto.toImage()
        imageRepository.save(image)
        return image.toImageDto()
    }

    override suspend fun deleteImage(imageId: String): ImageDto {
        val image = imageRepository.findById(imageId) ?: throw com.mshop.exception.NotFoundException("Not found image $imageId")
        imageRepository.delete(image)
        return image.toImageDto()
    }

    override suspend fun getBanners(): List<ImageDto> {
        return imageRepository.getAllByOwnerId(banner).map { it.toImageDto() }.toList()
    }

    override suspend fun upsertBanner(imageDto: ImageDto): ImageDto {
        val image = Image.from(ownerId = banner, url = imageDto.url)
        imageRepository.save(image)
        return image.toImageDto()
    }
}
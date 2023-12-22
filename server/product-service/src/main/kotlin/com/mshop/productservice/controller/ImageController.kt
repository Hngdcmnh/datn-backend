package com.mshop.productservice.controller

import com.mshop.base.BaseController
import com.mshop.base.BaseResponse
import com.mshop.productservice.dto.ImageDto
import com.mshop.productservice.service.ImageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/discovery/images")
class ImageController(
    private val imageService: ImageService
) : com.mshop.base.BaseController() {

    @PostMapping
    suspend fun upsertImage(
        @RequestBody imageDto: ImageDto
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        imageService.upsertImage(imageDto)
    }

    @DeleteMapping("/{imageId}")
    suspend fun deleteImage(
        @PathVariable("imageId") imageId: String
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        imageService.deleteImage(imageId)
    }
}
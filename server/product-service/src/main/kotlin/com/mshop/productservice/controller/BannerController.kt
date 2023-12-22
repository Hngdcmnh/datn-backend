package com.mshop.productservice.controller

import com.mshop.base.BaseController
import com.mshop.base.BaseResponse
import com.mshop.productservice.dto.ImageDto
import com.mshop.productservice.service.ImageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/discovery/banners")
class BannerController(
    private val imageService: ImageService
) : com.mshop.base.BaseController() {

    @GetMapping
    suspend fun getBanners(): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        imageService.getBanners()
    }

    @PostMapping
    suspend fun upsertBanner(
        @RequestBody banner: ImageDto
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        imageService.upsertBanner(banner)
    }

    @DeleteMapping("/{imageId}")
    suspend fun deleteBanner(
        @PathVariable("imageId") imageId: String,
    ): ResponseEntity<com.mshop.base.BaseResponse<*>> = handle {
        imageService.deleteImage(imageId)
    }
}
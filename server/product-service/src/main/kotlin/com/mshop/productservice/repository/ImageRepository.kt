package com.mshop.productservice.repository

import com.mshop.productservice.model.Image
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository : CoroutineCrudRepository<Image, String> {
    fun getAllByOwnerId(ownerId: String): Flow<Image>

    suspend fun getFirstByOwnerId(ownerId: String): Image
}
package com.mshop.productservice.service

import com.mshop.productservice.dto.ghn.CreateStoreRequest
import com.mshop.productservice.dto.ghn.GHNResponse
import com.mshop.productservice.dto.ghn.GHNStoreDto

interface GHNService {
    suspend fun createStore(request: CreateStoreRequest): GHNStoreDto
}
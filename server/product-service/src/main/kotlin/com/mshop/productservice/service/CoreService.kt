package com.mshop.productservice.service

import com.mshop.productservice.model.UserProduct

interface CoreService {
    suspend fun upsertComment(comment: UserProduct)

}
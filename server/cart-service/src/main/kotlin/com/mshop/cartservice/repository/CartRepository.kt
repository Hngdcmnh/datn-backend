package com.mshop.cartservice.repository

import com.mshop.cartservice.repository.entity.Cart
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface CartRepository: CoroutineCrudRepository<Cart, String> {
    suspend fun findCartByUserIdAndStatus(userId:String,status:String):  Flow<Cart>
    suspend fun countByUserIdAndStatus(userId: String,status: String):Int
}
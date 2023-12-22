package com.mshop.cartservice.repository

import com.mshop.cartservice.repository.entity.CartProduct
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CartProductRepository : CoroutineCrudRepository<CartProduct, String> {
    suspend fun findCartProductByCartIdAndProductId(cartId: String, productId: String): CartProduct?
    suspend fun findCartProductByCartId(cartId: String): Flow<CartProduct>
    suspend fun deleteCartProductByCartIdAndProductId(cartId: String, productId: String): Int


}
package com.mshop.cartservice.service

import com.mshop.cartservice.controller.dto.CartDto
import com.mshop.cartservice.controller.dto.CartProductDto
import com.mshop.cartservice.controller.dto.UpsertCartProductDto
import com.mshop.cartservice.controller.dto.order.OrderCartDto
import com.mshop.cartservice.controller.dto.order.OrderCartProductDto
import com.mshop.cartservice.repository.entity.Cart
import com.mshop.cartservice.repository.entity.CartProduct
import kotlinx.coroutines.flow.Flow


interface CartService {

    //---Cart-----------
    suspend fun createCartByStatus(userId: String, status: String): CartDto
    suspend fun createNewCart(userId: String): CartDto
    suspend fun getCountItemActiveCart(userId: String): Int

    //---Active Cart-----------
    suspend fun getActiveCart(userId: String): CartDto
    suspend fun getCartById(cartId: String): CartDto

    suspend fun getOrderCartById(cartId: String, supplierId: String? = null): OrderCartDto

    suspend fun upsertUserProductByUserAndSupplier(userId: String, supplierId: String, cartId: String): List<String>

    suspend fun getCartProducts(cartId: String): List<CartProductDto>
    suspend fun getOrderCartProducts(cartId: String, supplierId: String? = null): List<OrderCartProductDto>

    //---CartProductInActiveCart-------------

    suspend fun updateProductInActiveCart(
        userId: String,
        upsertCartProductDto: UpsertCartProductDto
    ): CartDto

    suspend fun deleteCartProduct(
        userId: String,
        cartId: String,
        productId: String,
    ): CartDto


    //---Processing Cart-------------

    suspend fun getProcessingCart(userId: String): OrderCartDto

    /**
     * Tạo processing cart từ active cart
     */
    suspend fun createProcessingCart(userId: String, cartProducts: List<CartProductDto>): CartDto

    /**
     * Delete processing cart if process is interrupted (back,...)
     */
    suspend fun deleteProcessingCart(userId: String): Boolean

    /**
     * Update active cart after checkout processing cart
     */
    suspend fun checkoutProcessingCart(userId: String)

    suspend fun getCartProductsByCartId(cartId: String): Flow<CartProduct>

}
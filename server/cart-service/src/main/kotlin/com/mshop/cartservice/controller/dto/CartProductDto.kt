package com.mshop.cartservice.controller.dto

import com.mshop.cartservice.repository.entity.CartProduct

data class CartProductDto(
    val cartProductId: String = "",
    val cartId: String = "",
    val quantity: Int = 0,
    val totalPrice: Double = 0.0,
    val purchasePrice:Double? = null,
    val product: ProductInfoDto? = null
)

fun CartProductDto.toCartProduct(): CartProduct {
    return CartProduct(
        cartProductId = this.cartProductId,
        cartId = this.cartId,
        purchasePrice = this.purchasePrice,
        quantity = this.quantity,
        productId = product?.productId ?: ""
    )
}
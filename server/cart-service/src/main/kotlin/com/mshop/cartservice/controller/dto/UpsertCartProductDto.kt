package com.mshop.cartservice.controller.dto

import com.mshop.cartservice.repository.entity.CartProduct
import com.mshop.utils.IdentifyCreator

data class UpsertCartProductDto(
    val cartProductId: String?,
    val productId: String,
    val quantity: Int,
)

fun UpsertCartProductDto.toCartProduct(cartId: String): CartProduct {
    return CartProduct(
        cartProductId = IdentifyCreator.createOrElse(cartProductId),
        cartId = cartId,
        productId = productId,
        quantity = quantity
    ).apply {
        isNewCartProduct = this@toCartProduct.cartProductId.isNullOrEmpty()
    }
}

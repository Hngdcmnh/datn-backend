package com.mshop.cartservice.controller.dto

import com.mshop.cartservice.repository.entity.Cart

data class CartDto(
    val userId: String = "",
    val cartId: String = "",
    var totalPrice: Double = 0.0,
    var quantity: Int = 0,
    val status: String = "",
    var cartProducts: List<CartProductDto> = listOf()
)

fun CartDto.toCart(): Cart {
    return Cart(userId = this.userId, cartId = this.cartId, quantity = this.quantity, status = this.status)
}
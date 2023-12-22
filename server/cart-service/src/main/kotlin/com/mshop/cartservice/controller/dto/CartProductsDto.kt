package com.mshop.cartservice.controller.dto


data class CartProductsDto(
    var cartProducts: List<CartProductDto> = listOf()
)
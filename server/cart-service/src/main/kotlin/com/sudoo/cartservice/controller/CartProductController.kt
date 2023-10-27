package com.sudoo.cartservice.controller

import com.sudoo.cartservice.controller.dto.UpsertCartProductDto
import com.sudoo.cartservice.service.CartService
import com.sudoo.domain.base.BaseController
import com.sudoo.domain.base.BaseResponse
import com.sudoo.domain.common.Constants
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class CartProductController(val cartService: CartService) : BaseController() {
    @PostMapping("/product")
    suspend fun updateProductToActiveCart(
        @RequestHeader(Constants.HEADER_USER_ID) userId: String,
        @RequestBody upsertCartProductDto: UpsertCartProductDto
    ): ResponseEntity<BaseResponse<*>> = handle {
        cartService.updateProductInActiveCart(userId, upsertCartProductDto)
    }

    //--------------------------------------------------------------------------------------------

    @DeleteMapping("/product")
    suspend fun deleteProductInCart(
        @RequestHeader(Constants.HEADER_USER_ID) userId: String,
        @PathVariable("cartId") cartId: String,
        @PathVariable("cartProductId") cartProductId: String
    ): ResponseEntity<BaseResponse<*>> = handle {
        cartService.deleteCartProduct(userId, cartProductId)
    }

}
package com.mshop.cartservice.controller

import com.mshop.cartservice.controller.dto.UpsertCartProductDto
import com.mshop.cartservice.service.CartService
import com.mshop.base.BaseController
import com.mshop.base.BaseResponse
import com.mshop.common.Constants
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
        @RequestParam("cartId") cartId: String,
        @RequestParam("productId") productId: String
    ): ResponseEntity<BaseResponse<*>> = handle {
        cartService.deleteCartProduct(userId,cartId, productId)
    }

    @GetMapping("/internal/{cartId}/products")
    suspend fun getCartProductByCartId(
        @PathVariable("cartId") cartId: String,
    ): ResponseEntity<BaseResponse<*>> = handle {
        cartService.getCartProductsByCartId(cartId)
    }

    @PostMapping("/internal/{cartId}/user-product")
    suspend fun upsertUserProductByUserAndSupplier(
        @PathVariable("cartId") cartId: String,
        @RequestParam("userId") userId: String,
        @RequestParam("supplierId") supplierId: String,
    ): ResponseEntity<BaseResponse<*>> = handle {
        cartService.upsertUserProductByUserAndSupplier(userId, supplierId, cartId)
    }
}
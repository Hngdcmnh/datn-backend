package com.mshop.cartservice.repository.entity

import com.mshop.cartservice.controller.dto.CartProductDto
import com.mshop.cartservice.controller.dto.ProductInfoDto
import com.mshop.cartservice.controller.dto.order.OrderCartProductDto
import com.mshop.cartservice.controller.dto.order.OrderProductInfoDto
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "cart_product")
data class CartProduct(
    @Id
    @Column("cart_product_id")
    var cartProductId: String = "",

    @Column("cart_id")
    var cartId: String = "",

    @Column("product_id")
    var productId: String = "",

    @Column("quantity")
    var quantity: Int = 0,

    @Column("purchase_price")
    var purchasePrice: Double? = null,
) : Persistable<String> {

    @Transient
    internal var isNewCartProduct: Boolean = false
    override fun getId(): String = cartProductId
    override fun isNew(): Boolean = isNewCartProduct
}

fun CartProduct.toCartProductDto(product: ProductInfoDto): CartProductDto {
    return CartProductDto(
        cartProductId = this.cartProductId,
        cartId = this.cartId,
        purchasePrice = this.purchasePrice,
        quantity = this.quantity,
        totalPrice = (product.price * this.quantity).toDouble(),
        product = product
    )
}

fun CartProduct.toOrderCartProductDto(product: OrderProductInfoDto): OrderCartProductDto {
    return OrderCartProductDto(
        cartProductId = this.cartProductId,
        cartId = this.cartId,
        purchasePrice = this.purchasePrice,
        quantity = this.quantity,
        totalPrice = this.quantity * product.price * 1.0,
        product = product
    )
}
package com.mshop.productservice.model

import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column

data class OrderProductInfo(
    @Column("product_id")
    val productId: String,
    @Column("supplier_id")
    val supplierId: String,
    @Column("sku")
    val sku: String,
    @Column("name")
    val name: String,
    @Column("brand")
    val brand: String,
    @Column("price")
    val price: Float,
    @Column("weight")
    val weight: Int,
    @Column("height")
    val height: Int,
    @Column("length")
    val length: Int,
    @Column("width")
    val width: Int,
) {

    @Transient
    var images: List<String>? = null
}

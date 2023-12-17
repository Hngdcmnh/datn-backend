package com.sudoo.productservice.model

import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("products")
data class ProductInfo(
    @Column("product_id")
    val productId: String,
    @Column("supplier_id")
    val supplierId: String,
    @Column("sku")
    val sku: String,
    @Column("name")
    val name: String,
    @Column("brand")
    var brand: String,
    @Column("price")
    val price: Float,
    @Column("listed_price")
    val listedPrice: Float,
    @Column("amount")
    val amount: Int,
    @Column("sold_amount")
    val soldAmount: Int,
    @Column("color")
    val color: String,
    @Column("size")
    val size: String,
    @Column("saleable")
    val saleable: Boolean,
    @Column("rate")
    val rate: Float,
    @Column("discount")
    val discount: Int,
    @Column("start_date_discount")
    val startDateDiscount: LocalDateTime?,
    @Column("end_date_discount")
    val endDateDiscount: LocalDateTime?,
) {

    @Transient
    var images: List<String>? = null
}
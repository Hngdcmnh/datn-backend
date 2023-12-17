package com.sudoo.productservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("products")
data class Product(
    @Id
    @Column("product_id")
    val productId: String,

    @Column("supplier_id")
    val supplierId: String,

    @Column("sku")
    val sku: String,

    @Column("name")
    val name: String,

    @Column("description")
    val description: String,

    @Column("brand")
    var brand: String = "",

    @Column("price")
    val price: Float,

    @Column("listed_price")
    val listedPrice: Float,

    @Column("amount")
    var amount: Int,

    @Column("color")
    var color: String,

    @Column("size")
    var size: String,

    @Column("sold_amount")
    var soldAmount: Int,

    @Column("rate")
    var rate: Float,

    @Column("total_rate_amount")
    var totalRateAmount: Int,

    @Column("discount")
    val discount: Int,

    @Column("start_date_discount")
    val startDateDiscount: LocalDateTime?,

    @Column("end_date_discount")
    val endDateDiscount: LocalDateTime?,

    @Column("saleable")
    val saleable: Boolean,

    // gram
    @Column("weight")
    val weight: Int,

    // cm
    @Column("height")
    val height: Int,

    // cm
    @Column("length")
    val length: Int,

    // cm
    @Column("width")
    val width: Int,

    @Column("created_at")
    val createdAt: LocalDateTime,

    ) : Persistable<String> {

    @Transient
    var images: List<Image>? = null

    @Transient
    var supplier: Supplier? = null

    @Transient
    var categories: List<Category>? = null

    @Transient
    internal var isNewProduct: Boolean = false

    override fun getId(): String = productId

    override fun isNew(): Boolean = isNewProduct
}
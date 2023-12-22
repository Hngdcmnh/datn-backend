package com.mshop.productservice.model

import com.mshop.common.Constants
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Persistent
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("promotions")
data class Promotion(
    @Id
    @Column("promotion_id")
    val promotionId: String,

    @Column("supplier_id")
    val supplierId: String,

    @Column("enable")
    val enable: Boolean,

    @Column("image")
    val image: String,

    @Column("name")
    val name: String,

    @Column("value")
    val value: Float,

    @Column("total_amount")
    var totalAmount: Int,
) : Persistable<String> {

    @get:Transient
    internal val isSystemPromotion: Boolean
        get() = supplierId == com.mshop.common.Constants.ADMIN_ID

    @Transient
    internal var isNewPromotion: Boolean = false

    override fun getId(): String = promotionId

    override fun isNew(): Boolean = isNewPromotion


}

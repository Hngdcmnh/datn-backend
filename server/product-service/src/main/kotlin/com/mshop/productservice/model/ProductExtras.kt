package com.mshop.productservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Persistent
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("product_extras")
data class ProductExtras(
    @Id
    @Column("product_id")
    val productId: String,
    @Column("enable_3D_viewer")
    val enable3DViewer: Boolean,
    @Column("enable_ar_viewer")
    val enableArViewer: Boolean,
    @Column("source")
    val source: String?,
) : Persistable<String> {

    @Transient
    internal var isNewExtras: Boolean = false

    override fun getId(): String = productId

    override fun isNew(): Boolean = isNewExtras
}

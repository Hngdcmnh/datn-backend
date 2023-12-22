package com.mshop.productservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("categories")
data class Category(
    @Id
    @Column("category_id")
    val categoryId: String,

    @Column("name")
    val name: String,

    @Column("image")
    val image: String,

) : Persistable<String> {

    @Transient
    val products: List<Product>? = null

    @Transient
    internal var isNewCategory: Boolean = false

    override fun getId(): String = categoryId

    override fun isNew(): Boolean = isNewCategory
}
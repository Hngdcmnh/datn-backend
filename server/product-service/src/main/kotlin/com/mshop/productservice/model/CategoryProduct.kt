package com.mshop.productservice.model

import com.mshop.utils.IdentifyCreator
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("categories_products")
data class CategoryProduct(
    @Id
    @Column("category_product_id")
    val categoryProductId: String,
    @Column("product_id")
    val productId: String,
    @Column("category_id")
    val categoryId: String,
) : Persistable<String> {
    companion object {
        fun from(productId: String, categoryId: String): CategoryProduct {
            return CategoryProduct(
                categoryProductId = IdentifyCreator.create(),
                productId = productId,
                categoryId = categoryId,
            ).apply {
                isNewCategoryProduct = true
            }
        }
    }

    @Transient
    internal var isNewCategoryProduct: Boolean = false

    override fun getId(): String = categoryProductId

    override fun isNew(): Boolean = isNewCategoryProduct
}

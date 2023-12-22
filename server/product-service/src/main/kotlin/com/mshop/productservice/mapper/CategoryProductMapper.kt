package com.mshop.productservice.mapper

import com.mshop.utils.IdentifyCreator
import com.mshop.productservice.dto.CategoryProductDto
import com.mshop.productservice.model.CategoryProduct

fun CategoryProductDto.toCategoryProduct(): CategoryProduct {
    return CategoryProduct(
        categoryProductId = IdentifyCreator.createOrElse(categoryProductId),
        productId = productId,
        categoryId = categoryId,
    ).also {
        it.isNewCategoryProduct = categoryProductId.isNullOrEmpty()
    }
}

fun CategoryProduct.toCategoryProductDto(): CategoryProductDto {
    return CategoryProductDto(
        categoryProductId = categoryProductId,
        productId = productId,
        categoryId = categoryId,
    )
}
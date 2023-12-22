package com.mshop.productservice.mapper

import com.mshop.utils.IdentifyCreator
import com.mshop.productservice.dto.CategoryDto
import com.mshop.productservice.dto.CategoryInfoDto
import com.mshop.productservice.model.Category

fun Category.toCategoryDto(countProduct: Int? = null): CategoryDto {
    return CategoryDto(
        categoryId = categoryId,
        name = name,
        image = image,
        countProduct = countProduct,
    )
}

fun Category.toCategoryInfoDto(): CategoryInfoDto {
    return CategoryInfoDto(
        categoryId = categoryId,
        name = name,
        image = image,
    )
}

fun CategoryDto.toCategory(): Category {
    return Category(
        categoryId = IdentifyCreator.createOrElse(categoryId),
        name = name,
        image = image,
    ).also {
        it.isNewCategory = categoryId.isNullOrEmpty()
    }
}
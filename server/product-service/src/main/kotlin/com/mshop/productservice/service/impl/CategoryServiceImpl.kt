package com.mshop.productservice.service.impl

import com.mshop.exception.NotFoundException
import com.mshop.productservice.dto.CategoryDto
import com.mshop.productservice.dto.CategoryInfoDto
import com.mshop.productservice.mapper.toCategory
import com.mshop.productservice.mapper.toCategoryDto
import com.mshop.productservice.repository.CategoryProductRepository
import com.mshop.productservice.repository.CategoryRepository
import com.mshop.productservice.service.CategoryService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val categoryProductRepository: CategoryProductRepository,
) : CategoryService {
    override suspend fun getCategories(select: String): List<CategoryDto> = coroutineScope{
        if (select.isBlank()) {
            categoryRepository.findAll().map { it.toCategoryDto() }.toList()
        } else {
            categoryRepository.findAll().map {
                async {
                    val countProduct = categoryProductRepository.countProductOfCategory(it.categoryId).toInt()
                    it.toCategoryDto(countProduct = countProduct)
                }
            }.toList().awaitAll()
        }
    }

    override suspend fun getCategoryInfos(): List<CategoryInfoDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getCategoryById(categoryId: String): CategoryDto {
        val category =
            categoryRepository.findById(categoryId) ?: throw com.mshop.exception.NotFoundException("Not found category $categoryId")
        val countProduct = categoryProductRepository.countProductOfCategory(category.categoryId).toInt()
        return category.toCategoryDto(countProduct = countProduct)
    }

    override suspend fun getCategoriesByProductId(productId: String): List<CategoryDto> = coroutineScope {
        val categories = categoryRepository.getCategoryIdByProductId(productId)
            .map { categoryId ->
                val category = categoryRepository.findById(categoryId)
                category!!.toCategoryDto()
            }
        categories.toList()
    }

    override suspend fun upsertCategory(categoryDto: CategoryDto): CategoryDto {
        val category = categoryDto.toCategory()
        categoryRepository.save(category)
        return if (category.isNew) {
            category.toCategoryDto(countProduct = 0)
        } else {
            val countProduct = categoryProductRepository.countProductOfCategory(category.categoryId).toInt()
            category.toCategoryDto(countProduct = countProduct)
        }
    }

    override suspend fun deleteCategory(categoryId: String): String {
        val category =
            categoryRepository.findById(categoryId) ?: throw com.mshop.exception.NotFoundException("Not found category $categoryId")
        categoryRepository.delete(category)
        return categoryId
    }
}
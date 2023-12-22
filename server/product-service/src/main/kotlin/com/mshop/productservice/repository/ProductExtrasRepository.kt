package com.mshop.productservice.repository

import com.mshop.productservice.model.ProductExtras
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductExtrasRepository : CoroutineCrudRepository<ProductExtras, String> {
}
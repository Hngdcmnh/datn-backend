package com.mshop.productservice.repository

import com.mshop.productservice.model.Promotion
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PromotionRepository : CoroutineCrudRepository<Promotion, String> {

    suspend fun deleteAllBySupplierId(supplierId: String)

    fun getAllByEnable(enable: Boolean): Flow<Promotion>

    fun getAllByEnableAndTotalAmountGreaterThan(enable: Boolean, amount: Int): Flow<Promotion>

}
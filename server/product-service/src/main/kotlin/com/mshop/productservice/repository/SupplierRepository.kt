package com.mshop.productservice.repository

import com.mshop.productservice.model.Supplier
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SupplierRepository : CoroutineCrudRepository<Supplier, String> {
     suspend fun findByName(name: String): Flow<Supplier>
     suspend fun getByUserId(userId: String): Supplier?

     @Query("""
          SELECT suppliers.supplier_id FROM suppliers WHERE suppliers.user_id = :userId LIMIT 1
     """)
     suspend fun getSupplierIdByUserId(@Param("userId") userId: String): String?

     @Query("""
          SELECT suppliers.brand FROM suppliers WHERE suppliers.user_id = :userId LIMIT 1
     """)
     suspend fun getBrandByUserId(@Param("userId") userId: String): String?

     @Query("""
          SELECT suppliers.brand FROM suppliers WHERE suppliers.supplier_id = :supplierId LIMIT 1     
     """)
     suspend fun getBrand(@Param("supplierId") supplierId: String): String
}
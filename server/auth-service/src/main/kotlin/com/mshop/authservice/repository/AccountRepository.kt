package com.mshop.authservice.repository

import com.mshop.authservice.model.Account
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Repository
interface AccountRepository : CoroutineCrudRepository<Account, String> {

    @Query("""
        SELECT * FROM accounts WHERE accounts.email_or_phone_number = :emailOrPhoneNumber LIMIT 1;
    """)
    suspend fun getAccountByEmailOrPhoneNumber(@Param("emailOrPhoneNumber") emailOrPhoneNumber: String): Account?

    @Query("""
        SELECT 
        CASE WHEN EXISTS(
            SELECT 1 FROM accounts WHERE accounts.email_or_phone_number = :emailOrPhoneNumber LIMIT 1
        )
        THEN 1
        ELSE 0
        END;
    """)
    suspend fun existsByEmailOrPhoneNumber(@Param("emailOrPhoneNumber") emailOrPhoneNumber: String): Int

    @Modifying
    @Transactional
    @Query(
        value = """
            UPDATE accounts SET accounts.is_validated=TRUE WHERE accounts.user_id = :addressId;
        """
    )
    suspend fun validate(@Param("addressId") userId: String)
}
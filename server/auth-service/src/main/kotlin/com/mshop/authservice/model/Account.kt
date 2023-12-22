package com.mshop.authservice.model

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("accounts")
data class Account(
    @Id
    @Column("user_id")
    val userId: String,

    @Column("email_or_phone_number")
    var emailOrPhoneNumber: String,

    @Column("password")
    var password: String,

    @Column("role")
    var role: Role,

    @Column("provider")
    var provider: Provider = Provider.AUTH_SERVICE,

    @Column("is_validated")
    var isValidated: Boolean = false,

    @Column("create_at")
    var createAt: LocalDateTime = LocalDateTime.now(),
) : Persistable<String> {

    @Transient
    internal var isNewAccount: Boolean = true

    override fun getId(): String = userId

    override fun isNew(): Boolean = isNewAccount

}
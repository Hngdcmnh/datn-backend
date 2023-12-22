package com.mshop.authservice.service

import com.mshop.authservice.model.Account

interface UserService {
    suspend fun createUserForAccount(account: Account): Boolean
}
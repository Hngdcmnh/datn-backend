package com.mshop.productservice.service

import com.mshop.productservice.dto.AddressDto
import com.mshop.productservice.dto.UserInfoDto

interface UserService {

    suspend fun getAllCustomer():List<UserInfoDto>
    suspend fun getUserInfo(userId: String): UserInfoDto

    suspend fun postAddress(address: AddressDto): AddressDto

    suspend fun getAddressById(addressId: String): AddressDto
}
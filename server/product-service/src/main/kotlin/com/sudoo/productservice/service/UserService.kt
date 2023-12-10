package com.sudoo.productservice.service

import com.sudoo.productservice.dto.AddressDto
import com.sudoo.productservice.dto.UserInfoDto

interface UserService {

    suspend fun getAllCustomer():List<UserInfoDto>
    suspend fun getUserInfo(userId: String): UserInfoDto

    suspend fun postAddress(address: AddressDto): AddressDto

    suspend fun getAddressById(addressId: String): AddressDto
}
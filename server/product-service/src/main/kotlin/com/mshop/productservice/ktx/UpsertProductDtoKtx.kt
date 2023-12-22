package com.mshop.productservice.ktx

import com.mshop.exception.BadRequestException
import com.mshop.productservice.dto.UpsertProductDto

fun UpsertProductDto.validate(): Boolean {
    return if (
        name != null &&
        description != null &&
        listedPrice != null &&
        amount != null
    ) {
        true
    } else {
        throw com.mshop.exception.BadRequestException("Missing value")
    }
}
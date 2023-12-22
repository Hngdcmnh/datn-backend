package com.mshop.productservice.dto.ghn

import com.fasterxml.jackson.annotation.JsonProperty

data class GHNStoreDto(
    @JsonProperty("shop_id")
    val shopId: Int,
)

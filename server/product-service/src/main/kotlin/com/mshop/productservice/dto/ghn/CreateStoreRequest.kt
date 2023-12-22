package com.mshop.productservice.dto.ghn

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateStoreRequest(
    @JsonProperty("district_id")
    val districtId: Int,

    @JsonProperty("ward_code")
    val wardCode: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("phone")
    val phone: String,

    @JsonProperty("address")
    val address: String,
)

package com.mshop.cartservice.controller.dto


data class RouteDto(
        val weight: Double = 0.0,
        val duration: ValueDto = ValueDto(),
        val distance: ValueDto = ValueDto()
)

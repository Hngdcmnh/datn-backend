package com.mshop.cartservice.repository.entity

enum class CartStatus(val value: String) {
    ACTIVE("active"),
    PROCESSING("processing"),
    COMPLETED("completed"),
}
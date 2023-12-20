package com.sudoo.apigateway.config

enum class ServerServices(val id: String, val uri: String, val pattern: Array<String>) {
    AUTH_SERVICE("auth-service", "lb://auth-service", arrayOf("/api/v1/auth/**")),
    PRODUCT_SERVICE("product-service", "lb://product-service", arrayOf("/api/v1/discovery/**", "/api/v1/promotions/**")),
    STORAGE_SERVICE("storage-service", "lb://storage-service", arrayOf("/api/v1/storage/**")),
    USER_SERVICE("user-service", "lb://user-service", arrayOf("/api/v1/users/**", "/api/v1/addresses/**")),
    CART_SERVICE("cart-service", "lb://cart-service", arrayOf("/api/v1/carts/**")),
    ORDER_SERVICE("order-service", "lb://order-service", arrayOf("/api/v1/orders/**", "/api/v1/payment/**", "/payment/**")),
    NOTIFICATION_SERVICE("notification-service", "lb://notification-service", arrayOf("/api/v1/notifications/**", "/api/v1/chats/**")),
}
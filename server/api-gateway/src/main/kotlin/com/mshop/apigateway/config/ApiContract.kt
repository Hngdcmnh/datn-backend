package com.mshop.apigateway.config

internal enum class ApiContract(val paths: List<String>) {
    EXTERNAL_API_ENDPOINTS(
        listOf(
            "/sign-in",
            "/sign-up",
            "/logout",
            "/refresh-token",
            "/generate-otp",
            "/verify-otp",
            "/images",
            "/files",
            "/return-vnpay",
            "/ipn-vnpay",
            "/static",
            "/templates",
            "/share",
        )
    ),
    INTERNAL_API_ENDPOINTS(listOf("/internal")),
}
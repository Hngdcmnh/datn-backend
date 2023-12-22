package com.mshop.cartservice.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class InternalServiceConfig {

    @Bean
    @Primary
    @Qualifier("product-service")
    fun userWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl("http://product-service:8083/api/v1/discovery/products")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

}
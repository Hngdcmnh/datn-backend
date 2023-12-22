package com.mshop.authservice.config

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
    @Qualifier("user-service")
    fun userWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl("http://user-service:8081/api/v1/users")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

}
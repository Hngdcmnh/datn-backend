package com.mshop.productservice.config

import com.mshop.common.Constants
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ExternalServiceConfig {

    @Bean
    @Primary
    @Qualifier("ghn-service")
    fun ghnWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl("https://dev-online-gateway.ghn.vn/shiip/public-api/v2")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(com.mshop.common.Constants.GHNTokenKey, com.mshop.common.Constants.GHNTokenValue)
            .build()
    }
}
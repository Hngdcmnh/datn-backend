package com.mshop.apigateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {

    @Bean
    fun corsFilter(): CorsWebFilter {
        return CorsWebFilter(corsConfigurationSource())
    }

    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration().applyPermitDefaultValues()
//        val config = CorsConfiguration()
        config.addAllowedMethod(HttpMethod.GET)
        config.addAllowedMethod(HttpMethod.PUT)
        config.addAllowedMethod(HttpMethod.POST)
        config.addAllowedMethod(HttpMethod.DELETE)
        config.addAllowedMethod(HttpMethod.PATCH)

        source.registerCorsConfiguration("/**", config)
        return source
    }
}
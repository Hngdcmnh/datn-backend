package com.sudoo.apigateway.config

import com.sudoo.apigateway.filter.ApiFilter
import com.sudoo.apigateway.filter.BaseResponsePostFilter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

@Configuration
class ApiGatewayConfig(
    @Lazy private val apiFilter: ApiFilter,
) {

    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes().run {
            ServerServices.values().forEach { service ->
                route(service.id) { predicate ->
                    predicate.path(*service.pattern).filters {
                        filter ->
                        filter.filter(apiFilter)
                        filter.dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_UNIQUE")
                    }
                        .uri(service.uri)
                }
            }
            build()
        }
    }

    @Bean
    fun postBaseResponseFilter() = BaseResponsePostFilter()

}
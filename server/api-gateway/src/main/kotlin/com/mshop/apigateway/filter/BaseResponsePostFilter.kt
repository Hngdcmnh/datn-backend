package com.mshop.apigateway.filter

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class BaseResponsePostFilter : GlobalFilter, Ordered {
    val objectMapper = ObjectMapper()
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        print("Pre BaseResponsePostFilter")
        return chain.filter(exchange)

    }

    override fun getOrder(): Int = -1

}
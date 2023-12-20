package com.sudoo.apigateway.filter

import com.sudoo.apigateway.config.ApiContract
import com.sudoo.apigateway.utils.TokenUtils
import com.sudoo.domain.common.Constants
import com.sudoo.domain.common.SudooError
import com.sudoo.domain.exception.ApiException
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.function.Predicate

@Component
@Order(1)
class ApiFilter(
    private val tokenUtils: TokenUtils
) : GatewayFilter {
    private val log = LoggerFactory.getLogger(ApiFilter::class.java)

    private val isApiSecured: Predicate<ServerHttpRequest> = Predicate<ServerHttpRequest> { request ->
        ApiContract.EXTERNAL_API_ENDPOINTS.paths.stream()
            .noneMatch { endpoint ->
                request.uri.path.contains(endpoint)
            }
    }

    private val isApiInternal: Predicate<ServerHttpRequest> = Predicate<ServerHttpRequest> { request ->
        ApiContract.INTERNAL_API_ENDPOINTS.paths.stream()
            .anyMatch { endpoint ->
                request.uri.path.contains(endpoint)
            }
    }

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
            var request = exchange.request
            var newExchange = exchange

            log.info("[${request.method}] ${request.path}")

            if (isApiInternal.test(request)) {
                throw ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, SudooError.INTERNAL_API_NOT_ALLOW.message)
            }

            if (isApiSecured.test(request)) {
                if (!request.headers.containsKey(Constants.AUTHORIZATION)) {
                    throw ResponseStatusException(HttpStatus.UNAUTHORIZED, SudooError.UNAUTHORIZED.message)
                } else {
                    var token = request.headers.getOrEmpty(Constants.AUTHORIZATION).first()

                    if (token.startsWith(Constants.TOKEN_TYPE)) {
                        token = token.replace(Constants.TOKEN_TYPE + " ", "")
                    } else {
                        throw ResponseStatusException(HttpStatus.UNAUTHORIZED, SudooError.MUST_CONTAIN_TOKEN_TYPE.message)
                    }

                    try {
                        if (tokenUtils.validateToken(token)) {
                            val userId = tokenUtils.getUserIdFromToken(token)
                            request = request.mutate().headers {
                                it.remove(Constants.AUTHORIZATION)
                                it.add(Constants.HEADER_USER_ID, userId)
                            }.build()
                            newExchange = exchange.mutate().request(request).build()
                        }
                    } catch (e: ApiException) {
                        log.error("Authentication failed :  : " + e.message)
                        throw ResponseStatusException(HttpStatus.UNAUTHORIZED, e.message)
                    }
                }
            }
            return chain.filter(newExchange)
    }
}
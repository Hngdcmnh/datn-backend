package com.mshop.productservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class ProductServiceApplication

fun main(args: Array<String>) {
    runApplication<com.mshop.productservice.ProductServiceApplication>(*args)
}

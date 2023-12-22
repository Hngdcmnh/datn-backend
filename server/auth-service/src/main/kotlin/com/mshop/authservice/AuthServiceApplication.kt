package com.mshop.authservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class AuthServiceApplication

fun main(args: Array<String>) {
    runApplication<com.mshop.authservice.AuthServiceApplication>(*args)
}

package com.mshop.productservice.ktx

fun String.toBoolean(): Boolean {
    return this.lowercase() == "true"
}
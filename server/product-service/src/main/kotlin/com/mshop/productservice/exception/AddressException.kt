package com.mshop.productservice.exception

import com.mshop.exception.ApiException
import org.springframework.http.HttpStatus

class AddressException : com.mshop.exception.ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Not found address")
package com.mshop.authservice.exception

import com.mshop.common.mshopError
import com.mshop.exception.ApiException
import org.springframework.http.HttpStatus

class WrongPasswordException : ApiException(HttpStatus.BAD_REQUEST, mshopError.WRONG_PASSWORD.message)
package com.mshop.authservice.exception

import com.mshop.exception.ApiException
import org.springframework.http.HttpStatus

class UserException : ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't create user")
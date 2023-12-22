package com.mshop.authservice.exception

import com.mshop.common.mshopError
import com.mshop.exception.ApiException
import org.springframework.http.HttpStatus

class ErrorSendEmail : ApiException(HttpStatus.BAD_REQUEST, "Error when send email!")
package com.mshop.authservice.exception

import com.mshop.common.mshopError
import com.mshop.exception.ApiException
import org.springframework.http.HttpStatus

class EmailOrPhoneNumberExistedException : ApiException(HttpStatus.BAD_REQUEST, mshopError.EMAIL_OR_PHONE_NUMBER_EXISTED.message)
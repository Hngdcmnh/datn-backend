package com.mshop.authservice.service.impl

import com.mshop.authservice.dto.TokenDto
import com.mshop.authservice.dto.VerifyDto
import com.mshop.authservice.exception.EmailOrPhoneNumberInvalidException
import com.mshop.authservice.repository.AccountRepository
import com.mshop.authservice.service.OtpService
import com.mshop.authservice.service.UserService
import com.mshop.authservice.utils.OtpUtils
import com.mshop.authservice.utils.TokenUtils
import com.mshop.exception.BadRequestException
import com.mshop.validator.AuthValidator
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class OtpServiceImpl(
    @Qualifier("Email") private val emailUtils: OtpUtils,
    @Qualifier("SMS") private val smsUtils: OtpUtils,
    private val userService: UserService,
    private val accountRepository: AccountRepository,
    private val tokenUtils: TokenUtils,
) : OtpService {
    override suspend fun generateOtp(emailOrPhoneNumber: String): Boolean =
        getOtpUtils(emailOrPhoneNumber).generateOtp(emailOrPhoneNumber)

    override suspend fun verifyOtp(verifyDto: com.mshop.authservice.dto.VerifyDto): com.mshop.authservice.dto.TokenDto? {
        if (getOtpUtils(verifyDto.emailOrPhoneNumber).verifyOtp(verifyDto)) {
            val account = accountRepository.getAccountByEmailOrPhoneNumber(verifyDto.emailOrPhoneNumber)
                ?: throw com.mshop.authservice.exception.EmailOrPhoneNumberInvalidException()

            return if (account.isValidated) {
                //TODO("Change password here after verify otp")
                null
            } else {
                userService.createUserForAccount(account)
                accountRepository.validate(account.userId)
                val token = tokenUtils.generateToken(account.userId)
                val refreshToken = tokenUtils.generateRefreshToken(account.userId)
                com.mshop.authservice.dto.TokenDto(token = token, refreshToken = refreshToken, role = account.role)
            }
        } else {
            throw BadRequestException("Verify failed")
        }
    }


    private fun getOtpUtils(emailOrPhoneNumber: String): OtpUtils {
        return when {
            AuthValidator.validatePhoneNumber(emailOrPhoneNumber) -> smsUtils
            AuthValidator.validateEmail(emailOrPhoneNumber) -> emailUtils
            else -> throw com.mshop.authservice.exception.EmailOrPhoneNumberInvalidException()
        }
    }
}
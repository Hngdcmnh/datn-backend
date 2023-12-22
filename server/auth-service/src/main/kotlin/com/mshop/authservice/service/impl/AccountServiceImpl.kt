package com.mshop.authservice.service.impl

import com.mshop.authservice.dto.ChangePasswordDto
import com.mshop.authservice.dto.SignInDto
import com.mshop.authservice.dto.SignUpDto
import com.mshop.authservice.dto.TokenDto
import com.mshop.authservice.exception.EmailOrPhoneNumberExistedException
import com.mshop.authservice.exception.EmailOrPhoneNumberInvalidException
import com.mshop.authservice.exception.EmailOrPhoneNumberNotValidatedException
import com.mshop.authservice.exception.WrongPasswordException
import com.mshop.authservice.mapper.toModel
import com.mshop.authservice.model.Account
import com.mshop.authservice.model.Role
import com.mshop.authservice.repository.AccountRepository
import com.mshop.authservice.service.AccountService
import com.mshop.authservice.service.OtpService
import com.mshop.authservice.service.UserService
import com.mshop.authservice.utils.TokenUtils
import com.mshop.exception.ApiException
import com.mshop.exception.CommonException
import com.mshop.exception.NotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository,
    private val encoder: PasswordEncoder,
    private val tokenUtils: TokenUtils,
    private val otpService: OtpService,
    private val userService: UserService,
) : AccountService {

    @Value("\${otp.enable}")
    private var enableOtp: Boolean = true
    override suspend fun getRole(userId: String): Role {
        val account = accountRepository.findById(userId) ?: throw NotFoundException("Not found role of user $userId")
        return account.role
    }

    override suspend fun signIn(signInDto: com.mshop.authservice.dto.SignInDto): com.mshop.authservice.dto.TokenDto {
        val account = accountRepository.getAccountByEmailOrPhoneNumber(signInDto.emailOrPhoneNumber)
            ?: throw com.mshop.authservice.exception.EmailOrPhoneNumberInvalidException()

        if (!encoder.matches(signInDto.password, account.password)) {
            throw com.mshop.authservice.exception.WrongPasswordException()
        }
        if (!account.isValidated) {
            throw com.mshop.authservice.exception.EmailOrPhoneNumberNotValidatedException()
        }
        val token = tokenUtils.generateToken(account.userId)
        val refreshToken = tokenUtils.generateRefreshToken(account.userId)
        return com.mshop.authservice.dto.TokenDto(token = token, refreshToken = refreshToken, role = account.role)
    }

    override suspend fun signUp(signUpDto: com.mshop.authservice.dto.SignUpDto) {
        if (accountRepository.existsByEmailOrPhoneNumber(signUpDto.emailOrPhoneNumber) == 1) {
            throw com.mshop.authservice.exception.EmailOrPhoneNumberExistedException()
        }
        val account = signUpDto.toModel(isValidated = !enableOtp)
        userService.createUserForAccount(account)
        saveAccount(account)
    }

    override suspend fun changePassword(userId: String, changePasswordDto: com.mshop.authservice.dto.ChangePasswordDto): Boolean {
        val account = accountRepository.findById(userId) ?: throw CommonException()
        if (!encoder.matches(account.password, changePasswordDto.oldPassword)) {
            throw com.mshop.authservice.exception.WrongPasswordException()
        }
        account.password = changePasswordDto.newPassword
        saveAccount(account)
        return true
    }

    override suspend fun logout(userId: String): Boolean {
        return true
    }

    override suspend fun registerAdmin(signUpDto: com.mshop.authservice.dto.SignUpDto): Boolean {
        if (accountRepository.existsByEmailOrPhoneNumber(signUpDto.emailOrPhoneNumber) == 1) {
            return true
        }
        val account = signUpDto.toModel(isValidated = false)
        return try {
            userService.createUserForAccount(account)
            account.isValidated = true
            saveAccount(account)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun saveAccount(account: Account, isHashPassword: Boolean = true) {
        try {
            if (isHashPassword) {
                account.password = encoder.encode(account.password)
            }
            accountRepository.save(account)
        } catch (e: Exception) {
            e.printStackTrace()
            throw ApiException(HttpStatus.INSUFFICIENT_STORAGE, e.message)
        }
    }
}
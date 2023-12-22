package com.mshop.authservice.utils

import com.mshop.authservice.dto.VerifyDto
import com.mshop.authservice.exception.ErrorSendEmail
import com.mshop.authservice.exception.OTPExpired
import com.mshop.utils.Logger
import org.apache.commons.lang.RandomStringUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Component
@Qualifier("Email")
class EmailUtils(
    private val mailSender: JavaMailSender,
    private val redis: RedisTemplate<String, Any>,
) : OtpUtils {

    @Value("\${otp.timeout}")
    private var otpTimeout: Long = 0

    @Value("\${spring.mail.username}")
    private lateinit var senderEmail: String

    override suspend fun generateOtp(emailOrPhoneNumber: String): Boolean {
        val otp = (100000 + Random.Default.nextInt(900000)).toString()
        redis.opsForValue().set(emailOrPhoneNumber, otp, otpTimeout, TimeUnit.MILLISECONDS)
        Logger.info(message = "otp = $otp")
        sendOTPEmail(emailOrPhoneNumber, otp)
        return true
    }

    override suspend fun verifyOtp(verifyDto: com.mshop.authservice.dto.VerifyDto): Boolean {
        val otp = redis.opsForValue().getAndDelete(verifyDto.emailOrPhoneNumber) as String?
        if (!otp.isNullOrEmpty()) {
            return otp == verifyDto.otp
        }
        throw com.mshop.authservice.exception.OTPExpired()
    }

    private fun sendOTPEmail(email: String, otp: String) {
        try {
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message)

            helper.apply {
                setFrom(senderEmail, "mshop")
                setTo(email)

                val subject = "Here's your One Time Password (OTP) - Expire in 2 minutes!"
                val content = """
                <p>Hello $email!<p>
                <p>For security reason, you're required to use the following One Time Password to verify:</p>
                <p><b> $otp <b><p>
                <br>
                <p>Note: this OTP is set to expire in 2 minutes.<p>
            """.trimIndent()

                helper.setSubject(subject)
                helper.setText(content, true)

                mailSender.send(message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw com.mshop.authservice.exception.ErrorSendEmail()
        }
    }
}
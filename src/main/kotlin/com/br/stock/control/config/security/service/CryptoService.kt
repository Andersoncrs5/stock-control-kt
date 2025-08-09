package com.br.stock.control.config.security.service

import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CryptoService(
    private val passwordEncoder: PasswordEncoder
) {
    private val logger = LoggerFactory.getLogger(CryptoService::class.java)

    fun encoderPassword(password: String): String{
        logger.debug("Encoding password...")
        val encode = this.passwordEncoder.encode(password)
        logger.debug("Password encoder!")
        return encode
    }

    fun verifyPassword(password: String, passwordHash: String): Boolean {
        logger.debug("Checking password...")
        val matches = this.passwordEncoder.matches(password, passwordHash)
        logger.debug("Password checked! Returning the result")
        return matches
    }

    fun checkUpdateEncoder(password: String): Boolean {
        logger.debug("Checking if password updated")
        val encode = this.passwordEncoder.upgradeEncoding(password)
        return encode
    }
}
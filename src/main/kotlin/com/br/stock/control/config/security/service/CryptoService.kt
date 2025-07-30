package com.br.stock.control.config.security.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CryptoService(
    private val passwordEncoder: PasswordEncoder
) {
    fun cryptoPassword(password: String): String{
        return this.passwordEncoder.encode(password)
    }

    fun verifyPassword(password: String, passwordHash: String): boolean {
        return this.passwordEncoder.matches(password, passwordHash)
    }
}
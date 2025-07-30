package com.br.stock.control.unitTest.config

import com.br.stock.control.config.security.service.CryptoService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class CryptoServiceTest {

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    private lateinit var crypto: CryptoService

    @Test
    fun `Encoding password`() {
        val password: String = "12345678"
        `when`(passwordEncoder.encode(password)).thenReturn(anyString())

        val result = crypto.encoderPassword(password)

        assertNotNull(result, "Password came null")

        verify(passwordEncoder, times(1)).encode(password)
    }

    @Test
    fun `should verifyPassword and return should be true`() {
        val password = "12345678"
        val passwordHash = "passwordHash"

        `when`(passwordEncoder.matches(password, passwordHash)).thenReturn(true)

        val result = this.crypto.verifyPassword(password, passwordHash)

        assertTrue(result, "The result of verify password is false")

        verify(passwordEncoder, times(1)).matches(password, passwordHash)
    }

    @Test
    fun `should verifyPassword and return should be false`() {
        val password = "12345678"
        val passwordHash = "passwordHash"

        `when`(passwordEncoder.matches(password, passwordHash)).thenReturn(false)

        val result = this.crypto.verifyPassword(password, passwordHash)

        assertFalse(result, "The result of verify password is true")

        verify(passwordEncoder, times(1)).matches(password, passwordHash)
    }



}
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
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.assertj.core.api.Assertions.assertThat

@ExtendWith(MockitoExtension::class)
class CryptoServiceTest {

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    private lateinit var crypto: CryptoService

    @Test
    fun `Encoding password`() {
        val password: String = "12345678"
        whenever(passwordEncoder.encode(password)).thenReturn(anyString())

        val result = crypto.encoderPassword(password)

        assertNotNull(result, "Password came null")
        assertThat(result).isNotEqualTo(password)

        verify(passwordEncoder, times(1)).encode(password)
        verifyNoMoreInteractions(passwordEncoder)
    }

    @Test
    fun `should verifyPassword and return should be true`() {
        val password = "12345678"
        val passwordHash = "passwordHash"

        whenever(passwordEncoder.matches(password, passwordHash)).thenReturn(true)

        val result = this.crypto.verifyPassword(password, passwordHash)

        assertTrue(result, "The result of verify password is false")

        verify(passwordEncoder, times(1)).matches(password, passwordHash)
        verifyNoMoreInteractions(passwordEncoder)
    }

    @Test
    fun `should verifyPassword and return should be false`() {
        val password = "12345678"
        val passwordHash = "passwordHash"

        whenever(passwordEncoder.matches(password, passwordHash)).thenReturn(false)

        val result = this.crypto.verifyPassword(password, passwordHash)

        assertFalse(result, "The result of verify password is true")

        verify(passwordEncoder, times(1)).matches(password, passwordHash)
        verifyNoMoreInteractions(passwordEncoder)
    }

    @Test
    fun `should checkUpdateEncoder return true`() {
        val password: String = "12345678"
        whenever(passwordEncoder.upgradeEncoding(password)).thenReturn(true)

        val result: Boolean = this.crypto.checkUpdateEncoder(password)

        assertThat(result).isTrue

        verify(passwordEncoder, times(1)).upgradeEncoding(password)
        verifyNoMoreInteractions(passwordEncoder)
    }

    @Test
    fun `should checkUpdateEncoder return false`() {
        val password: String = "12345678"
        whenever(passwordEncoder.upgradeEncoding(password)).thenReturn(false)

        val result: Boolean = this.crypto.checkUpdateEncoder(password)

        assertThat(result).isFalse

        verify(passwordEncoder, times(1)).upgradeEncoding(password)
        verifyNoMoreInteractions(passwordEncoder)
    }

}
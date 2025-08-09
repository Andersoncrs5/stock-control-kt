package com.br.stock.control.unitTest.service

import com.br.stock.control.model.entity.Contact
import com.br.stock.control.model.entity.Role
import com.br.stock.control.model.entity.User
import com.br.stock.control.repository.UserRepository
import com.br.stock.control.service.UserService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userService: UserService

    private val user: User = User(
        id = UUID.randomUUID().toString(),
        name = "test_username",
        email = "testuser@example.com",
        passwordHash = "12345678",
        fullName = "test_username",
        accountNonExpired = false,
        credentialsNonExpired = false,
        accountNonLocked = false,
        lastLoginAt = LocalDateTime.now(),
        version = 0,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        roles = setOf(
            Role(
                name = "ROLE_USER",
                id = UUID.randomUUID().toString(),
                description = "",
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        ),
        addressId = UUID.randomUUID().toString(),
        contact = Contact(),
        refreshToken = "${UUID.randomUUID()}-${UUID.randomUUID()}"
    )

    @Test
    fun `should get user`() {
        val userCopy = user.copy(id = UUID.randomUUID().toString())

        whenever(userRepository.findById(userCopy.id)).thenReturn(Optional.of(userCopy))

        val result = userService.get(userCopy.id)

        assertNotNull(result, "User is null")
        assertEquals(userCopy.id, result.id)

        verify(userRepository, times(1)).findById(userCopy.id)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should save user`() {
        val userCopy = user.copy(id = "")

        whenever(userRepository.save(any())).thenReturn(user)

        val result = userService.saveUser(userCopy)

        assertEquals(result.email, user.email)
        assertEquals(result.name, user.name)

        verify(userRepository, times(1)).save(any())
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should delete user`() {
        val userCopy = user.copy()
        doNothing().whenever(userRepository).delete(userCopy)

        this.userService.deleteUser(userCopy)

        verify(userRepository, times(1)).delete(userCopy)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should update user`() {
        val userId = UUID.randomUUID().toString();
        val userBefore = user.copy(id = userId, name = "update")
        val userAfter = user.copy(id = userId, name = "update")

        whenever(userRepository.save<User>(userBefore)).thenReturn(userAfter)

        val result = userService.updateUser(userAfter)

        assertNotNull(result, "Result came null")
        assertEquals(result.id, userAfter.id, "User id are different")
        assertEquals(result.name, userAfter.name, "Name are different")

        verify(userRepository, times(1)).save(userBefore)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should return null`() {
        whenever(userRepository.findById(any())).thenReturn(Optional.empty())

        val result = userService.get(UUID.randomUUID().toString())

        assertNull(result, "User is not null")

        verify(userRepository, times(1)).findById(any())
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should delete many users`() {
        val ids = List(5) { UUID.randomUUID().toString() }

        doNothing().whenever(userRepository).deleteAllById(ids)

        this.userService.deleteMany(ids)

        verify(userRepository, times(1)).deleteAllById(ids)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should return true`() {
        whenever(userRepository.existsByEmail(anyString())).thenReturn(true)

        val existsByEmail: Boolean = this.userService.existsByEmail("")

        assertTrue(existsByEmail, "The result is false")

        verify(userRepository, times(1)).existsByEmail(anyString())
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should return false`() {
        whenever(userRepository.existsByEmail(anyString())).thenReturn(false)

        val existsByEmail: Boolean = this.userService.existsByEmail("")

        assertFalse { existsByEmail }

        verify(userRepository, times(1)).existsByEmail(anyString())
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should get user by name`() {
        val userCopy = this.user.copy()
        whenever(userRepository.findByName(user.name)).thenReturn(userCopy)

        val result = this.userService.getUserByName(user.name)

        assertNotNull(result, "User is null")
        assertEquals(userCopy.id, result.id, "Ids are different")
        assertEquals(userCopy.name, result.name, "Names are different")

        verify(userRepository, times(1)).findByName(user.name)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should get user by name return null`() {
        whenever(userRepository.findByName(user.name)).thenReturn(null)

        val result = this.userService.getUserByName(user.name)

        assertNull(result, "User is not null")

        verify(userRepository, times(1)).findByName(user.name)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should return true in get user by name`() {
        whenever(userRepository.existsByName(anyString())).thenReturn(true)

        val exists: Boolean = this.userService.existsByName("")

        assertTrue(exists, "The result is false")

        verify(userRepository, times(1)).existsByName(anyString())
    }

    @Test
    fun `should return false in get user by name`() {
        whenever(userRepository.existsByName(anyString())).thenReturn(false)

        val exists: Boolean = this.userService.existsByName("")

        assertFalse { exists }

        verify(userRepository, times(1)).existsByName(anyString())
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should return user`() {
        val refreshToken = user.refreshToken as String
        whenever(userRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.of<User>(this.user))

        val result = this.userService.getUserByRefreshToken(refreshToken)

        assertTrue(result.isPresent, "User is null")

        verify(userRepository, times(1)).findByRefreshToken(refreshToken)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should return null where search user by refresh token`() {
        val refreshToken = user.refreshToken as String
        whenever(userRepository.findByRefreshToken(refreshToken)).thenReturn(Optional.empty())

        val result = this.userService.getUserByRefreshToken(refreshToken)

        assertTrue(result.isEmpty, "User is not null")

        verify(userRepository, times(1)).findByRefreshToken(refreshToken)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should change status accountNonExpired`() {
        val userCopy = user.copy(accountNonExpired = true)
        whenever(userRepository.save(user)).thenReturn(userCopy)

        val result: User = this.userService.changeStatusAccountNonExpired(user);

        assertThat(result).isNotNull
        assertThat(result.accountNonExpired).isEqualTo(user.accountNonExpired)

        verify(userRepository, times(1)).save(user)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should change status accountNonLocked`() {
        val userCopy = user.copy(accountNonLocked = true)
        whenever(userRepository.save(user)).thenReturn(userCopy)

        val result: User = this.userService.changeStatusAccountNonLocked(user)

        assertThat(result).isNotNull
        assertThat(result.accountNonLocked).isEqualTo(user.accountNonLocked)

        verify(userRepository, times(1)).save(user)
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun `should change status credentialsNonExpired`() {
        val userCopy = user.copy(credentialsNonExpired = true)
        whenever(userRepository.save(user)).thenReturn(userCopy)

        val result: User = this.userService.changeStatusCredentialsNonExpired(user)

        assertThat(result).isNotNull
        assertThat(result.credentialsNonExpired).isEqualTo(user.credentialsNonExpired)

        verify(userRepository, times(1)).save(user)
        verifyNoMoreInteractions(userRepository)
    }

}
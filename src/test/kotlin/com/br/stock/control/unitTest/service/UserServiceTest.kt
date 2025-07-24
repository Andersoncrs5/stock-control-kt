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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals

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
        accountNonExpired = true,
        credentialsNonExpired = true,
        accountNonLocked = true,
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
        contact = Contact()
    )

    @Test
    fun `should get user`() {
        val userId = UUID.randomUUID().toString()
        val userCopy = user.copy(id = userId)

        `when`(userRepository.findById(userId)).thenReturn(Optional.of(userCopy))

        val result = userService.getUser(userId)

        assertNotNull(result, "User is null")
        assertEquals(userId, result.id)
        verify(userRepository, times(1)).findById(userId)
    }

    @Test
    fun `should save user`() {
        val userCopy = user.copy(id = "")

        `when`(userRepository.save(any())).thenReturn(user)

        val result = userService.saveUser(userCopy)

        assertEquals(result.email, user.email)
        assertEquals(result.name, user.name)

        verify(userRepository, times(1)).save(any())
    }

    @Test
    fun `should delete user`() {
        val userCopy = user.copy()
        doNothing().`when`(userRepository).delete(userCopy)

        this.userService.deleteUser(userCopy)

        verify(userRepository, times(1)).delete(userCopy)
    }

    @Test
    fun `should update user`() {
        val userId = UUID.randomUUID().toString();
        val userBefore = user.copy(id = userId, name = "update")
        val userAfter = user.copy(id = userId, name = "update")

        `when`(userRepository.save<User>(userBefore)).thenReturn(userAfter)

        val result = userService.updateUser(userAfter)

        assertNotNull(result, "Result came null")
        assertEquals(result.id, userAfter.id, "User id are different")
        assertEquals(result.name, userAfter.name, "Name are different")

        verify(userRepository, times(1)).save(userBefore)
    }

    @Test
    fun `should return null`() {
        `when`(userRepository.findById(any())).thenReturn(Optional.empty())

        val result = userService.getUser(UUID.randomUUID().toString())

        assertNull(result)
    }

    @Test
    fun `should delete many products`() {
        val ids = listOf(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
        )

        doNothing().`when`(userRepository).deleteAllById(ids)

        this.userService.deleteMany(ids)

        verify(userRepository, times(1)).deleteAllById(ids)
    }

}
